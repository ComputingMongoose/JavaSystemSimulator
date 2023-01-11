package jss.devices.memory.impl;

import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;
import jss.devices.memory.PROMController;
import jss.devices.memory.PROMDevice;
import jss.simulation.Simulation;

public class PROMControllerBasic implements PROMController {

	PROMDevice prom;
	boolean default_write_enable;
	long last_write_data;
	
	@Override
	public void setLastWriteData(long data) {
		last_write_data=data;
	}
	
	@Override
	public long read(long address) throws MemoryAccessException {
		switch((int)address) {
		case 0: return prom.getWriteEnable()?1:0;
		case 1: return last_write_data>>4;
		case 2: return last_write_data;
		default:
			throw new MemoryAccessException(address,MemoryOperation.READ);
		}
		
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		if(address==1)
			prom.setWriteEnable(value!=0);
	}

	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		prom=null;
		default_write_enable=(config.getLong("write_enable")!=0);
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		// Nothing to do
	}

	@Override
	public void attachPROM(PROMDevice device) {
		prom=device;
		prom.setWriteEnable(default_write_enable);
	}

}
