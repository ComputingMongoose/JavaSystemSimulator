package jss.disk;

import jss.devices.GenericDevice;

public interface DiskDrive extends GenericDevice {

	public boolean isReady();
	public Disk getDisk();
	
}
