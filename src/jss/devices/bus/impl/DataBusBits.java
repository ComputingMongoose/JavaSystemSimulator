package jss.devices.bus.impl;

import java.util.ArrayList;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataDevice;
import jss.devices.bus.DataBus;
import jss.devices.bus.DataBusDevice;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class DataBusBits implements DataBus {

	ArrayList<DataBusDevice> devices;
	
	long last_address;
	long last_data;
	Simulation sim;
	boolean bLogMisses;
	
	public DataBusBits() {
		devices=new ArrayList<>(100);
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException {
		this.sim=sim;
		bLogMisses=config.getOptLong("log_misses", 0)!=0;
		
	}

	@Override
	public void initialize()
			throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
	}

	@Override
	public void attachDataDevice(GenericDataDevice device, long start, long end, long offset, String name, boolean enabled) {
		devices.add(new DataBusDevice(device,start,end,offset,name,enabled));
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		last_address=address;
		last_data=0;
		boolean found=false;
		try {
			for(DataBusDevice d:devices) {
				if(d.isValidAddress(address)) {
					last_data|=d.read(address);
					found=true;
				}
			}
		}catch(MemoryAccessException e) {;}
		if(bLogMisses && !found) {
			sim.writeToCurrentLog(String.format("BUS READ MISS [%x]", address));
		}
		return last_data;
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		last_address=address;
		last_data=value;
		boolean found=false;
		try {
			for(DataBusDevice d:devices) {
				if(d.isValidAddress(address)) {
					d.write(address,value);
					found=true;
				}
			}
		}catch(MemoryAccessException e) {;}
		if(bLogMisses && !found) {
			sim.writeToCurrentLog(String.format("BUS WRITE MISS [%x] [%x]", address,value));
		}

	}

	public long getLast_address() {
		return last_address;
	}

	public long getLast_data() {
		return last_data;
	}

	@Override
	public DataBusDevice getDeviceByConnectionName(String name) {
		for(DataBusDevice d:this.devices) {
			if(d.getName().contentEquals(name))return d;
		}
		return null;
	}


}
