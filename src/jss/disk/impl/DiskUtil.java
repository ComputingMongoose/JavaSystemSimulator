package jss.disk.impl;

import java.io.File;
import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.disk.Disk;
import jss.simulation.Simulation;

public class DiskUtil {

	public static Disk loadImage(Simulation sim, DeviceConfiguration config, File diskFile) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		Disk disk=null;
		
		if(diskFile.getName().toLowerCase().endsWith(".imd")) disk=new IMDDisk();
		else if(diskFile.getName().toLowerCase().endsWith(".ovr")) disk=new OVRDisk();
		
		if(disk==null) {
			sim.writeToCurrentLog("DiskUtil.loadImage ERROR Unknown file extension");
		}else {
			disk.configure(config, sim);
			disk.initialize();
			disk.loadImage(diskFile);
		}
		
		return disk;
	}
	
}
