package jss.devices.impl;

import java.io.IOException;
import java.util.ArrayList;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDataDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class DMAController implements GenericDataDevice, GenericExecutionDevice, GenericControlDevice {

	Simulation sim;
	int []port_num_rw=new int[16];
	int []port_data=new int[16];
	
	ControlBus controlBus;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		
		for(int i=0;i<port_num_rw.length;i++) {
			port_num_rw[i]=0;
			port_data[i]=0;
		}
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		long ret=0;
		if(address>=0 && address<=port_num_rw.length) {
			if(port_num_rw[(int)address]==0) {
				ret=port_data[(int)address]&0xFF;
				port_num_rw[(int)address]=1;
			}else {
				ret=(port_data[(int)address]>>8)&0xFF;
				port_num_rw[(int)address]=0;
			}
		}else {
			sim.writeToCurrentLog(String.format("DMA READ UNKNOWN port [%x]", address));
		}

		sim.writeToCurrentLog(String.format("DMA read port [%x] ==> [%x]", address,ret));

		return ret;
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		if(address>=0 && address<=port_num_rw.length) {
			sim.writeToCurrentLog(String.format("DMA WRITE port [%x] [%x]", address,value));
			
			if(port_num_rw[(int)address]==0) {
				port_data[(int)address]=(int)((port_data[(int)address]&0xFF00) | (value&0xFF));
				port_num_rw[(int)address]=1;
			}else {
				port_data[(int)address]=(int)((port_data[(int)address]&0xFF) | ((value&0xFF)<<8));
				port_num_rw[(int)address]=0;
			}
		}else {
			sim.writeToCurrentLog(String.format("DMA WRITE UNKNOWN port [%x]", address));
		}
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		this.controlBus=bus;
	}
	
}
