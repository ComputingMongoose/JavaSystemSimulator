package jss.simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jss.configuration.ConfigurationValue;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDataAccessDevice;
import jss.devices.GenericDataDevice;
import jss.devices.GenericDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBus;
import jss.devices.bus.GenericConnectionBus;
import jss.devices.bus.impl.ControlBusBasic;
import jss.devices.bus.impl.DataBusNoError;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.impl.Intel4004;
import jss.devices.cpu.impl.Intel4040;
import jss.devices.cpu.impl.Intel8008;
import jss.devices.cpu.impl.Intel8080;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.PROMController;
import jss.devices.memory.PROMDevice;
import jss.devices.memory.impl.MemoryW4D4;
import jss.devices.memory.impl.MemoryW8D16;
import jss.devices.memory.impl.MemoryW8D32;
import jss.devices.memory.impl.MemoryW8D64;
import jss.devices.memory.impl.MemoryW8D8;
import jss.devices.memory.impl.PROMControllerBasic;
import jss.devices.memory.impl.PROMW4D4;
import jss.devices.memory.impl.PROMW8D16;
import jss.devices.memory.impl.PROMW8D32;
import jss.devices.memory.impl.PROMW8D64;
import jss.devices.memory.impl.PROMW8D8;
import jss.devices.memory.impl.ROMW4D4;
import jss.devices.memory.impl.ROMW8D16;
import jss.devices.memory.impl.ROMW8D32;
import jss.devices.memory.impl.ROMW8D64;
import jss.devices.memory.impl.ROMW8D8;
import jss.devices.peripherals.ADM3ATerminal;
import jss.devices.peripherals.ASR33Teletype;
import jss.devices.peripherals.IntellecIMM8_90;
import jss.devices.peripherals.Intellec_4_40_frontpanel;
import jss.devices.peripherals.Intellec_4_frontpanel;
import jss.devices.peripherals.Intellec_8_80_frontpanel;
import jss.devices.peripherals.Intellec_8_frontpanel;
import jss.devices.peripherals.LEDs4;
import jss.devices.peripherals.SimulationControl;
import jss.devices.peripherals.Switches4;
import jss.devices.peripherals.TelnetTerminal;

public class Simulation extends Thread {

	private String name;
	private String baseFolder;
	private HashMap<String,GenericDevice> devices;
	private ArrayList<GenericExecutionDevice> executionDevices;
	
	private Object lock;
	
	private StringBuffer simulationCurrentLog;
	
	private boolean suspended;
	private boolean manual_step;
	
	private long stepNumber;
	private long maxSteps;
	private long delayBetweenSteps_ms;
	private long delayBetweenSteps_ns;
	long lastLogTS;
	
	private ArrayList<GenericExecutionDevice> skipClock;
	
	private Simulation() {
		this.devices=new HashMap<>(100);
		this.executionDevices=new ArrayList<>(100);
		this.lock=new Object();
		simulationCurrentLog=new StringBuffer();
		suspended=false;
		manual_step=false;
		stepNumber=0;
		maxSteps=0;
		delayBetweenSteps_ms=10;
		delayBetweenSteps_ns=0;
		skipClock=new ArrayList<>(50);
	}
	
	public void setSkipClock(GenericExecutionDevice o) {skipClock.add(o);}
	
	public Path getFilePath(String name) {
		return Paths.get(baseFolder, name);
	}
	
	public static GenericDevice createDevice(String type, JSONArray jconfig, Simulation sim) throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException {
		DeviceConfiguration config=new DeviceConfiguration();
		if(jconfig!=null) {
			for(int i=0;i<jconfig.length();i++) {
				JSONObject c=jconfig.getJSONObject(i);
				String key=c.getString("key");
				try {
					long ivalue=c.getLong("value");
					ConfigurationValue cv=new ConfigurationValue(ivalue);
					config.set(key, cv);
				}catch(JSONException ex) {
					String svalue=c.getString("value");
					ConfigurationValue cv=new ConfigurationValue(svalue);
					config.set(key, cv);
				}
			}
		}
		
		GenericDevice dev=null;
		if(type.contentEquals("Intel4040")) dev=new Intel4040();
		else if(type.contentEquals("Intel4004")) dev=new Intel4004();
		else if(type.contentEquals("Intel8008")) dev=new Intel8008();
		else if(type.contentEquals("Intel8080")) dev=new Intel8080();
		else if(type.contentEquals("ControlBusBasic")) dev=new ControlBusBasic();
		else if(type.contentEquals("DataBusNoError")) dev=new DataBusNoError();
		else if(type.contentEquals("MemoryW4D4")) dev=new MemoryW4D4();
		else if(type.contentEquals("MemoryW8D8")) dev=new MemoryW8D8();
		else if(type.contentEquals("MemoryW8D16")) dev=new MemoryW8D16();
		else if(type.contentEquals("MemoryW8D32")) dev=new MemoryW8D32();
		else if(type.contentEquals("MemoryW8D64")) dev=new MemoryW8D64();
		else if(type.contentEquals("ROMW4D4")) dev=new ROMW4D4();
		else if(type.contentEquals("ROMW8D8")) dev=new ROMW8D8();
		else if(type.contentEquals("ROMW8D16")) dev=new ROMW8D16();
		else if(type.contentEquals("ROMW8D32")) dev=new ROMW8D32();
		else if(type.contentEquals("ROMW8D64")) dev=new ROMW8D64();
		else if(type.contentEquals("PROMW4D4")) dev=new PROMW4D4();
		else if(type.contentEquals("PROMW8D8")) dev=new PROMW8D8();
		else if(type.contentEquals("PROMW8D16")) dev=new PROMW8D16();
		else if(type.contentEquals("PROMW8D32")) dev=new PROMW8D32();
		else if(type.contentEquals("PROMW8D64")) dev=new PROMW8D64();
		else if(type.contentEquals("PROMControllerBasic")) dev=new PROMControllerBasic();
		else if(type.contentEquals("Intellec_4_40_frontpanel")) dev=new Intellec_4_40_frontpanel();
		else if(type.contentEquals("Intellec_4_frontpanel")) dev=new Intellec_4_frontpanel();
		else if(type.contentEquals("Intellec_8_frontpanel")) dev=new Intellec_8_frontpanel();
		else if(type.contentEquals("Intellec_8_80_frontpanel")) dev=new Intellec_8_80_frontpanel();
		else if(type.contentEquals("Switches4")) dev=new Switches4();
		else if(type.contentEquals("LEDs4")) dev=new LEDs4();
		else if(type.contentEquals("ASR33Teletype")) dev=new ASR33Teletype();
		else if(type.contentEquals("ADM3ATerminal")) dev=new ADM3ATerminal();
		else if(type.contentEquals("IntellecIMM8_90")) dev=new IntellecIMM8_90();
		else if(type.contentEquals("TelnetTerminal")) dev=new TelnetTerminal();
		else if(type.contentEquals("SimulationControl")) dev=new SimulationControl();
		
		dev.configure(config, sim);
		dev.initialize();
		
		return dev;
	}
	
	public static Simulation loadFromFolder(String path) throws JSONException, IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		Simulation sim=new Simulation();
		sim.baseFolder=path;

		JSONObject json = new JSONObject(new String ( Files.readAllBytes(Paths.get(path,"simulation.json"))));
		
		sim.name=json.getString("name");
		sim.maxSteps=json.optLong("maximum_steps", 0);
		sim.delayBetweenSteps_ms=json.optLong("delay_between_steps_ms",10);
		sim.delayBetweenSteps_ns=json.optLong("delay_between_steps_ns",0);
		
		JSONArray devices=json.getJSONArray("devices");
		for(int i=0;i<devices.length();i++) {
			JSONObject dev=devices.getJSONObject(i);
			GenericDevice gdev=createDevice(dev.getString("type"),dev.optJSONArray("configuration"), sim);
			sim.devices.put(dev.getString("name"),gdev);
			if(gdev instanceof GenericExecutionDevice)
				sim.executionDevices.add((GenericExecutionDevice)gdev);
		}
		
		JSONArray connections=json.getJSONArray("connections");
		for(int i=0;i<connections.length();i++) {
			JSONObject c=connections.getJSONObject(i);
			
			String type=c.getString("type");
			if(type.contentEquals("attachDataDevice")) {
				DataBus src=(DataBus)sim.devices.get(c.getString("src"));
				GenericDataDevice dev=(GenericDataDevice)sim.devices.get(c.getString("dev"));
				src.attachDataDevice(dev, c.getLong("start"), c.getLong("end"), c.getLong("offset"));
			}else if(type.contentEquals("attachToDataBus")) {
				GenericDataAccessDevice src=(GenericDataAccessDevice)sim.devices.get(c.getString("src"));
				DataBus dev=(DataBus)sim.devices.get(c.getString("dev"));
				src.attachToDataBus(dev);
			}else if(type.contentEquals("attachToControlBus")) {
				GenericControlDevice src=(GenericControlDevice)sim.devices.get(c.getString("src"));
				ControlBus dev=(ControlBus)sim.devices.get(c.getString("dev"));
				src.attachToControlBus(dev);
			}else if(type.contentEquals("attachGenericDevice")) {
				GenericConnectionBus src=(GenericConnectionBus)sim.devices.get(c.getString("src"));
				GenericDevice dev=sim.devices.get(c.getString("dev"));
				src.attachGenericDevice(dev);
			}else if(type.contentEquals("attachPROM")) {
				PROMController src=(PROMController)sim.devices.get(c.getString("src"));
				PROMDevice dev=(PROMDevice)sim.devices.get(c.getString("dev"));
				src.attachPROM(dev);
			}else if(type.contentEquals("attachPROMController")) {
				PROMDevice src=(PROMDevice)sim.devices.get(c.getString("src"));
				PROMController dev=(PROMController)sim.devices.get(c.getString("dev"));
				src.attachPROMController(dev);
			}
		}
		
		return sim;
	}

	public String getSimulationName() {
		return name;
	}
	
	private void writeToCurrentLog(String s) {
		synchronized(lock) {
			LocalDateTime now=LocalDateTime.now();
			lastLogTS=
					(((((now.getYear()*100+now.getMonthValue())*100+now.getDayOfMonth())*100+
					now.getHour())*100+now.getMinute())*100+now.getSecond())*1000+
					(now.getNano()%1000);
			simulationCurrentLog.append(s);
			simulationCurrentLog.append("\n");
		}
	}
	
	public String getCurrentLog() {
		synchronized(lock) {
			return simulationCurrentLog.toString();
		}
	}
	
	public boolean getSuspended() {
		synchronized(lock) {return suspended;}
	}
	
	public void setSuspended(boolean s) {
		synchronized(lock) {suspended=s;}
	}
	
	public void doManualStep() {
		synchronized(lock) {manual_step=true;}
	}

	
	@Override
	public void run() {
		while(true) {
			boolean do_cycle=true;
			synchronized(lock) {
				if(suspended) {
					if(!manual_step)do_cycle=false;
					else manual_step=false;
				}
				if(do_cycle)stepNumber++;
			}
			
			if(do_cycle) {
				for(GenericExecutionDevice dev:this.executionDevices) {
					boolean skip=false;
					for(GenericExecutionDevice o:skipClock)
						if(o==dev) {skip=true;break;}
					if(!skip) {
						try {
							dev.step();
						} catch (MemoryAccessException e1) {
							writeToCurrentLog("MemoryAccessException "+e1.getAddress());
							e1.printStackTrace();
						} catch (ControlBusUnknownSignalException e2) {
							writeToCurrentLog("ControlBusUnknownSignalException "+e2.getSignal());
							e2.printStackTrace();
						} catch (CPUInvalidOpcodeException e3) {
							writeToCurrentLog("CPUInvalidOpcodeException "+e3.getOpcode());
							e3.printStackTrace();
						}
					}
				}
				skipClock.clear();
			}
			
			Thread.yield();
			try {
				if(!do_cycle)Thread.sleep(500);
				else {
					if(maxSteps>0 && stepNumber>maxSteps)setSuspended(true);
					if(this.delayBetweenSteps_ms>0)
						Thread.sleep(this.delayBetweenSteps_ms, (int) this.delayBetweenSteps_ns);
					else if(this.delayBetweenSteps_ns>0) {
						long start = System.nanoTime();
					    long end=0;
					    do{
					        end = System.nanoTime();
					    }while(start + delayBetweenSteps_ns >= end);						
					}
						
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public long getStepNumber() {
		return stepNumber;
	}

	public long getMaxSteps() {
		return maxSteps;
	}

	public long getDelayBetweenSteps_ms() {
		return delayBetweenSteps_ms;
	}

	public long getDelayBetweenSteps_ns() {
		return delayBetweenSteps_ns;
	}
	
	public int getNumberOfDevices() {
		return devices.size();
	}

	public long getLastLogTS() {
		synchronized(lock) { return lastLogTS; }
	}
}
