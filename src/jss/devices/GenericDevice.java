package jss.devices;

import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.simulation.Simulation;

public interface GenericDevice {

	public void configure(DeviceConfiguration config, Simulation sim) throws DeviceConfigurationException, ConfigurationValueTypeException, IOException;
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException;
	
}
