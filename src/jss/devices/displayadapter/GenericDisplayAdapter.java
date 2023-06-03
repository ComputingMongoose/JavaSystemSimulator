package jss.devices.displayadapter;

import jss.devices.GenericDataDevice;
import jss.devices.display.GenericDisplayDevice;
import jss.devices.bus.DataBus;

public interface GenericDisplayAdapter extends GenericDataDevice, DataBus {
	public void attachDisplayDevice(GenericDisplayDevice disp);
}
