package jss.devices.impl;

import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataAccessDevice;
import jss.devices.GenericDataDevice;
import jss.devices.bus.DataBus;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class Intellec2IOC implements GenericDataDevice, GenericDataAccessDevice {

	Simulation sim;
	DataBus bus;
	DeviceConfiguration config;
	
	int status;
	int statusReady;

	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		this.sim=sim;
		this.config=config;
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		statusReady=0;
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		this.bus=bus;
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		switch((int)address) {
		case 0: // IOCI I/O CONTROLLER INPUT DATA (FROM DBB) PORT
			if(statusReady!=0) {
				statusReady=0;
				return status;
			}
			break;
		case 1: // IOCS I/O CONTROLLER INPUT DBB STATUS PORT
			if(statusReady!=0)return 1;
			break;
		default:
			sim.writeToCurrentLog(String.format("IOC Unknown address %X",address));
		}
		return 0;
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		switch((int)address) {
		case 0: // I/O CONTROLLER OUTPUT DATA (TO DBB) PORT
			sim.writeToCurrentLog(String.format("** IOC write data [%X]",value));
			break;
		case 1: // I/O CONTROLLER OUTPUT CONTROL COMMAND PORT
			switch((int)value) {
			case 0x10: // CRT OUTPUT DATA COMMAND
				sim.writeToCurrentLog(String.format("** IOC CRT Output Data Command"));
				break;
			case 0x11: // CRT DEVICE STATUS COMMAND
				sim.writeToCurrentLog(String.format("IOC CRT Device Status Command"));
				statusReady=1;
				status=0;
				break;
			case 0x12: // KEYBOARD INPUT DATA COMMAND
				sim.writeToCurrentLog(String.format("** IOC Keyboard Input Data Command"));
				break;
			case 0x13: // KEYBOARD DEVICE STATUS COMMAND
				sim.writeToCurrentLog(String.format("IOC Keyboard Device Status Command"));
				statusReady=1;
				status=0;
				break;
			default:
				sim.writeToCurrentLog(String.format("** IOC Unknown command port write [%X]",value));
			}
			break;
		default:
			sim.writeToCurrentLog(String.format("** IOC Unknown address %X",address));
		}
	}

}
