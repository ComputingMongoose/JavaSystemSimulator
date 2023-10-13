package jss.simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import jss.devices.GenericMultiDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBus;
import jss.devices.bus.GenericConnectionBus;
import jss.devices.bus.impl.ControlBusBasic;
import jss.devices.bus.impl.DataBusBits;
import jss.devices.bus.impl.DataBusNoError;
import jss.devices.cpu.CPUDevice;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.Disassembler;
import jss.devices.cpu.impl.Intel4004;
import jss.devices.cpu.impl.Intel4040;
import jss.devices.cpu.impl.Intel8008;
import jss.devices.cpu.impl.Intel8080;
import jss.devices.cpu.impl.Intel8080Disassembler;
import jss.devices.cpu.impl.Intel8088;
import jss.devices.display.GenericDisplayDevice;
import jss.devices.display.impl.IBM5151;
import jss.devices.displayadapter.GenericDisplayAdapter;
import jss.devices.displayadapter.impl.MDA;
import jss.devices.impl.DMAController;
import jss.devices.impl.Intellec2ControlPort;
import jss.devices.impl.Intellec2IOC;
import jss.devices.impl.PIC_8259A;
import jss.devices.impl.PIT;
import jss.devices.impl.Switch;
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
import jss.devices.peripherals.GenericTerminal;
import jss.devices.peripherals.HeathkitH19Terminal;
import jss.devices.peripherals.IntellecIMM8_90;
import jss.devices.peripherals.Intellec_4_40_frontpanel;
import jss.devices.peripherals.Intellec_4_frontpanel;
import jss.devices.peripherals.Intellec_8_80_frontpanel;
import jss.devices.peripherals.Intellec_8_frontpanel;
import jss.devices.peripherals.LEDs4;
import jss.devices.peripherals.MDS230_frontpanel;
import jss.devices.peripherals.SimulationControl;
import jss.devices.peripherals.Switches4;
import jss.devices.peripherals.TelnetTerminal;
import jss.disk.Disk;
import jss.disk.DiskController;
import jss.disk.DiskDrive;
import jss.disk.impl.FloppyDiskController;
import jss.disk.impl.MDS230_x2_floppydrives;

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
	
	private String onCPUInvalidOpcodeException;
	
	private ArrayList<GenericExecutionDevice> skipClock;
	
	private SystemSettings systemSettings;
	
	private Simulation() {
		this.systemSettings=SystemSettings.getSystemSettings();
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
		this.onCPUInvalidOpcodeException="CONTINUE";
	}
	
	public void setSkipClock(GenericExecutionDevice o) {skipClock.add(o);}
	
	public Path getFilePath(String name) {
		return Paths.get(baseFolder, name);
	}
	
	public static GenericDevice createDevice(String type, String name, JSONArray jconfig, Simulation sim) throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException {
		DeviceConfiguration config=new DeviceConfiguration(name,type);
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
		else if(type.contentEquals("Intel8088")) dev=new Intel8088();
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
		else if(type.contentEquals("HeathkitH19Terminal")) dev=new HeathkitH19Terminal();
		else if(type.contentEquals("GenericTerminal")) dev=new GenericTerminal();
		else if(type.contentEquals("IntellecIMM8_90")) dev=new IntellecIMM8_90();
		else if(type.contentEquals("TelnetTerminal")) dev=new TelnetTerminal();
		else if(type.contentEquals("SimulationControl")) dev=new SimulationControl();
		else if(type.contentEquals("MDA")) dev=new MDA();
		else if(type.contentEquals("IBM5151")) dev=new IBM5151();
		else if(type.contentEquals("PIT")) dev=new PIT();
		else if(type.contentEquals("DMAController")) dev=new DMAController();
		else if(type.contentEquals("Switch")) dev=new Switch();
		else if(type.contentEquals("DataBusBits")) dev=new DataBusBits();
		else if(type.contentEquals("MDS230_frontpanel")) dev=new MDS230_frontpanel();
		else if(type.contentEquals("FloppyDiskController")) dev=new FloppyDiskController();
		else if(type.contentEquals("8259A")) dev=new PIC_8259A();
		else if(type.contentEquals("MDS230_x2_FloppyDrives")) dev=new MDS230_x2_floppydrives();
		else if(type.contentEquals("Intellec2ControlPort")) dev=new Intellec2ControlPort();
		else if(type.contentEquals("Intellec2IOC")) dev=new Intellec2IOC();
		
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
		sim.onCPUInvalidOpcodeException=json.optString("onCPUInvalidOpcodeException", "CONTINUE").toUpperCase();
		
		JSONArray devices=json.getJSONArray("devices");
		for(int i=0;i<devices.length();i++) {
			JSONObject dev=devices.getJSONObject(i);
			GenericDevice gdev=createDevice(dev.getString("type"),dev.getString("name"), dev.optJSONArray("configuration"), sim);
			sim.devices.put(dev.getString("name"),gdev);
			if(gdev instanceof GenericExecutionDevice)
				sim.executionDevices.add((GenericExecutionDevice)gdev);
		}
		
		JSONArray connections=json.getJSONArray("connections");
		for(int i=0;i<connections.length();i++) {
			JSONObject c=connections.getJSONObject(i);
			
			GenericDevice device=null;
			String devStr=c.getString("dev");
			if(devStr.lastIndexOf(':')>0) {
				int devid=Integer.parseInt(devStr.substring(devStr.lastIndexOf(':')+1));
				devStr=devStr.substring(0,devStr.lastIndexOf(':'));
				device=((GenericMultiDevice)sim.devices.get(devStr)).getDevice(devid);
			}else device=sim.devices.get(c.getString("dev"));
			
			String type=c.getString("type");
			if(type.contentEquals("attachDataDevice")) {
				DataBus src=(DataBus)sim.devices.get(c.getString("src"));
				GenericDataDevice dev=(GenericDataDevice)device;
				src.attachDataDevice(dev, c.getLong("start"), c.getLong("end"), c.getLong("offset"), c.optString("name", ""),c.optBoolean("enabled", true));
			}else if(type.contentEquals("attachToDataBus")) {
				GenericDataAccessDevice src=(GenericDataAccessDevice)sim.devices.get(c.getString("src"));
				DataBus dev=(DataBus)device;
				src.attachToDataBus(dev);
			}else if(type.contentEquals("attachToControlBus")) {
				GenericControlDevice src=(GenericControlDevice)sim.devices.get(c.getString("src"));
				ControlBus dev=(ControlBus)device;
				src.attachToControlBus(dev);
			}else if(type.contentEquals("attachGenericDevice")) {
				GenericConnectionBus src=(GenericConnectionBus)sim.devices.get(c.getString("src"));
				src.attachGenericDevice(device);
			}else if(type.contentEquals("attachPROM")) {
				PROMController src=(PROMController)sim.devices.get(c.getString("src"));
				PROMDevice dev=(PROMDevice)device;
				src.attachPROM(dev);
			}else if(type.contentEquals("attachPROMController")) {
				PROMDevice src=(PROMDevice)sim.devices.get(c.getString("src"));
				PROMController dev=(PROMController)device;
				src.attachPROMController(dev);
			}else if(type.contentEquals("attachDisplayDevice")) {
				GenericDisplayAdapter src=(GenericDisplayAdapter)sim.devices.get(c.getString("src"));
				GenericDisplayDevice dev=(GenericDisplayDevice)device;
				src.attachDisplayDevice(dev);
			}else if(type.contentEquals("attachDiskDrive")) {
				DiskController src=(DiskController)sim.devices.get(c.getString("src"));
				DiskDrive dev=(DiskDrive)device;
				src.attachDiskDrive(dev);
			}
		}
		
		return sim;
	}

	public String getSimulationName() {
		return name;
	}
	
	public void writeToCurrentLog(String s) {
		System.out.println(s);
		synchronized(lock) {
			//LocalDateTime now=LocalDateTime.now();
			lastLogTS=System.currentTimeMillis();
					/*((((((now.getYear()%100)*100+now.getMonthValue())*100+now.getDayOfMonth())*100+
					now.getHour())*100+now.getMinute())*100+now.getSecond());//*1000+
					//(now.getNano()%1000);*/
			simulationCurrentLog.append(s);//String.format("%d-%d-%d %d:%d:%d %s",now.getYear(),now.getMonthValue(),now.getDayOfMonth(),now.getHour(),now.getMinute(),now.getSecond(),s));
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

	public boolean getManualStep() {
		boolean ret=false;
		synchronized(lock) {ret=manual_step;}
		return ret;
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
							
							if(this.onCPUInvalidOpcodeException.contentEquals("SUSPEND")) {
								this.setSuspended(true);
							}
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
	
	public List<String> getDevicesByType(String type){
		ArrayList<String> ret=new ArrayList<>(100);
		
		for(Map.Entry<String,GenericDevice> entry:devices.entrySet()) {		
			if(type.contentEquals("GenericDataDevice")) {
				if(entry.getValue() instanceof GenericDataDevice) {
					ret.add(entry.getKey());
				}
				
			}else if(type.contentEquals("GenericExecutionDevice")) {
				if(entry.getValue() instanceof GenericExecutionDevice) {
					ret.add(entry.getKey());
				}
			}else if(type.contentEquals("CPUDevice")) {
				if(entry.getValue() instanceof CPUDevice) {
					ret.add(entry.getKey());
				}
			}else if(type.contentEquals("Disk")) {
				if(entry.getValue() instanceof Disk) {
					ret.add(entry.getKey());
				}
			}else if(type.contentEquals("DiskController")) {
				if(entry.getValue() instanceof DiskController) {
					ret.add(entry.getKey());
				}
			}else if(type.contentEquals("DiskDrive")) {
				if(entry.getValue() instanceof DiskDrive) {
					ret.add(entry.getKey());
				}
				
			}
		}
		
		return ret;
	}
	
	public GenericDevice getDevice(String name) {
		return devices.get(name);
	}
	
	public Disassembler getDisassembler(CPUDevice d) {
		if(d instanceof Intel8080) {
			return new Intel8080Disassembler();
		}
		return null;
	}

	public SystemSettings getSystemSettings() {
		return systemSettings;
	}
}
