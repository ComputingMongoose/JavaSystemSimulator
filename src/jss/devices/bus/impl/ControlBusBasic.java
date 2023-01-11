package jss.devices.bus.impl;

import java.util.HashMap;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.simulation.Simulation;

public class ControlBusBasic implements ControlBus {

	private HashMap<String,Boolean> signals;
	private HashMap<String,byte[]> signalData;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException {
		
		String sSignals=config.getString("signals");
		signals=new HashMap<>(sSignals.length());
		signalData=new HashMap<>(sSignals.length());
		for(String s:sSignals.split("[,]")) {
			signals.put(s, Boolean.FALSE);
			signalData.put(s,null);
		}

	}

	@Override
	public void initialize()
			throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		for(String s:signals.keySet()) {
			signals.put(s, Boolean.FALSE);
			signalData.put(s,null);
		}
	}

	@Override
	public void setSignal(String name) throws ControlBusUnknownSignalException {
		if(!signals.containsKey(name))throw new ControlBusUnknownSignalException(name);
		signals.put(name, Boolean.TRUE);
	}

	@Override
	public void clearSignal(String name) throws ControlBusUnknownSignalException {
		if(!signals.containsKey(name))throw new ControlBusUnknownSignalException(name);
		signals.put(name, Boolean.FALSE);
	}

	@Override
	public boolean isSignalSet(String name) throws ControlBusUnknownSignalException {
		if(!signals.containsKey(name))throw new ControlBusUnknownSignalException(name);
		return signals.get(name);
	}

	@Override
	public void setSignalData(String name, byte[] data) throws ControlBusUnknownSignalException {
		if(!signals.containsKey(name))throw new ControlBusUnknownSignalException(name);
		signalData.put(name, data);
	}

	@Override
	public byte[] getSignalData(String name) throws ControlBusUnknownSignalException {
		if(!signals.containsKey(name))throw new ControlBusUnknownSignalException(name);
		return signalData.get(name);
	}

}
