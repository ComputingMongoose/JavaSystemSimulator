package jss.devices.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDataAccessDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBus;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public abstract class AbstractCPUDevice implements CPUDevice, GenericControlDevice, GenericDataAccessDevice {
	protected DataBus memoryBus=null;
	protected DataBus ioBus=null;
	protected ControlBus controlBus=null;

	protected HashMap<Long,Boolean> breakpoints=null;
	protected Object lockBreakpoints=new Object();
	
	protected Simulation sim;
	protected DeviceConfiguration config;
	
	public abstract void stepImpl() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException;
	public abstract long getCurrentAddress();
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException {
		this.sim=sim;
		this.config=config;
		
		String br=config.getOptString("breakpoints", "");
		if(br.length()>0) {
			String data[]=br.split("[,]");
			for(String s:data) {
				this.addBreakpoint(Long.parseLong(s,16));
			}
		}
	}
	
	@Override
	public void initialize()
			throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
	}	
	
	@Override
	public void attachToDataBus(DataBus bus) {
		if(memoryBus==null)memoryBus=bus;
		else if(ioBus==null)ioBus=bus;
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		controlBus=bus;
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		stepImpl();
		boolean doSuspend=false;
		synchronized(lockBreakpoints) {
			if(breakpoints!=null && breakpoints.containsKey(getCurrentAddress()))doSuspend=true;
		}
		if(doSuspend) {
			sim.writeToCurrentLog(String.format("Reached CPU breakpoint [%X]",getCurrentAddress()));
			sim.setSuspended(true);
		}
	}

	@Override
	public DataBus getMemoryBus() {
		return memoryBus;
	}

	@Override
	public void addBreakpoint(long address) {
		synchronized(lockBreakpoints) {
			if(breakpoints==null)breakpoints=new HashMap<>();
			breakpoints.put(address, Boolean.TRUE);
		}

	}

	@Override
	public void clearBreakpoints() {
		synchronized(lockBreakpoints) {
			if(breakpoints!=null)breakpoints.clear();
		}
	}

	@Override
	public List<Long> getBreakpoints() {
		ArrayList<Long> ret=new ArrayList<>();
		synchronized(lockBreakpoints) {
			if(breakpoints!=null) {
				for(Long l:breakpoints.keySet())ret.add(l);
			}
		}
		return ret;
	}

}
