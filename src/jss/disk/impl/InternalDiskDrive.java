package jss.disk.impl;

import java.io.File;
import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.disk.Disk;
import jss.disk.DiskDrive;
import jss.simulation.Simulation;

public class InternalDiskDrive implements DiskDrive {

	Disk disk;
	Simulation sim;
	DeviceConfiguration config;
	
	boolean overlay;
	boolean debug;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException, ConfigurationValueOptionException {
		this.sim=sim;
		this.config=config;
		disk=null;
		debug=(config.getOptLong("debug", 0)==1);
		overlay=(config.getOptLong("use_overlay", 0)==1);
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		if(disk!=null) {disk.close(); disk=null;}
		String d=config.getOptString("load_disk", "");
		if(d!=null && d.length()>0)loadDisk(sim.getFilePath(d).toFile());
	}

	@Override
	public boolean isReady() {
		return (disk!=null);
	}

	@Override
	public Disk getDisk() {
		return disk;
	}

	public void loadDisk(File diskFile) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		try {
			if(disk!=null) {disk.close();disk=null;}
		} catch (IOException e) {
			e.printStackTrace();
			sim.writeToCurrentLog(String.format("Exception closing disk: %s", e.getMessage()));
		}
		
		if(overlay) {
			disk=new OverlayDisk();
			disk.configure(config, sim);
			disk.initialize();
			disk.loadImage(diskFile);
		}else {
			disk=DiskUtil.loadImage(sim,config,diskFile);
		
			if(disk!=null)
				if(debug)sim.writeToCurrentLog("InternalDisk loadDisk OK");
		}
	}

	
}
