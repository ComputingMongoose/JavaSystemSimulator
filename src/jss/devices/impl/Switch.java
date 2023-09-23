package jss.devices.impl;

import java.io.IOException;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataDevice;
import jss.devices.bus.DataBus;
import jss.devices.bus.DataBusDevice;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class Switch implements GenericDataDevice, DataBus {

	Simulation sim;
	int currentDev;
	DataBusDevice devices[]=new DataBusDevice[2];
	int numAttached;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		numAttached=0;
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		this.currentDev=0;
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		switch((int)address) {
		case 0:
			return devices[currentDev].read(0);
		case 1:
			return currentDev;
		default:
			sim.writeToCurrentLog(String.format("SWITCH: READ: Invalid address [%x]",address));
		}
		return 0;
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		switch((int)address) {
		case 0:
			devices[currentDev].write(0, value);
			break;
		case 1:
			if(value==0)currentDev=0; else currentDev=1;
			break;
		default:
			sim.writeToCurrentLog(String.format("SWITCH: WRITE: Invalid address [%x] [%x]",address,value));
		}
	}

	@Override
	public void attachDataDevice(GenericDataDevice device, long start, long end, long offset,String name,boolean enabled) {
		if(numAttached>=2) {
			sim.writeToCurrentLog("SWITCH: Already has 2 attached devices");
			return;
		}
		if(start!=0 || end!=0) {
			sim.writeToCurrentLog("SWITCH: start and end address will be set to 0");
		}
		devices[numAttached++]=new DataBusDevice(device,0,0,offset,name,enabled);
	}

	@Override
	public DataBusDevice getDeviceByConnectionName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
