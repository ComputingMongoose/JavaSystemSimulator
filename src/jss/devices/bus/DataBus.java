package jss.devices.bus;

import jss.devices.GenericDataDevice;

public interface DataBus extends GenericDataDevice {

	public void attachDataDevice(GenericDataDevice device, long start, long end, long offset);
	
}
