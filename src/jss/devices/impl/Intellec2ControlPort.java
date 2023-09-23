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

public class Intellec2ControlPort implements GenericDataDevice, GenericDataAccessDevice {

	Simulation sim;
	DataBus bus;
	DeviceConfiguration config;

	String connection_boot_rom;
	String connection_boot_ram;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		this.sim=sim;
		this.config=config;
		connection_boot_rom=config.getString("connection_boot_rom");
		connection_boot_ram=config.getString("connection_boot_ram");
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		this.bus=bus;
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		return 0;
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		switch((int)value) {
		case 0: // Disable auxiliary PROM
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Disable auxiliary PROM",value));
			break;
		case 1: // Turn off bus override
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Turn off bus override",value));
			break;
		case 2: // Move boot to E800h
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Move boot to E800h",value));
			break;
		case 4: // Turn off boot/diagnostic
			bus.getDeviceByConnectionName(connection_boot_rom).setEnabled(false);
			bus.getDeviceByConnectionName(connection_boot_ram).setEnabled(true);
			this.sim.writeToCurrentLog(String.format("Control Port: [%02X] Turn off boot/diagnostic",value));
			break;
		case 5: // Enable interrupts
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Enable interrupts",value));
			break;
		case 8: // Enable auxiliary PROM
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Enable auxiliary PROM",value));
			break;
		case 9: // Turn on bus override
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Turn on bus override",value));
			break;
		case 0x0C: // Turn on boot/diagnostic
			bus.getDeviceByConnectionName(connection_boot_rom).setEnabled(true);
			bus.getDeviceByConnectionName(connection_boot_ram).setEnabled(false);
			this.sim.writeToCurrentLog(String.format("Control Port: [%02X] Turn on boot/diagnostic",value));
			break;
		case 0x0D: // Disable interrupts
			this.sim.writeToCurrentLog(String.format("** Control Port: [%02X] Disable interrupts",value));
			break;
		default:
			this.sim.writeToCurrentLog(String.format("Control Port: Unknown command [%02X]",value));
		}
	}

}
