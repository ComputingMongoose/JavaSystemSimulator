package jss.disk;

import java.util.List;

import jss.devices.GenericDataDevice;

public interface DiskController extends GenericDataDevice {

	public void attachDiskDrive(DiskDrive dd);
	public List<DiskDrive> getDrives();
	
}
