package jss.devices.bus;

import jss.devices.GenericDevice;

public interface ControlBus extends GenericDevice {

	public void setSignal(String name) throws ControlBusUnknownSignalException;
	public void clearSignal(String name) throws ControlBusUnknownSignalException;
	public boolean isSignalSet(String name) throws ControlBusUnknownSignalException;
	public void setSignalData(String name,byte[] data) throws ControlBusUnknownSignalException;
	public byte[] getSignalData(String name) throws ControlBusUnknownSignalException;
	
}
