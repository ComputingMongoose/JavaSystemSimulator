package jss.disk;

import java.io.File;
import java.io.IOException;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.simulation.Simulation;

public abstract class AbstractDisk implements Disk {

	protected int heads;
	protected int tracksPerHead;
	protected int sectorsPerTrack;
	protected int sectorSize;
	protected File imageFile;
	protected boolean debug;
	protected boolean readOnly;
	
	protected DeviceConfiguration config;
	protected Simulation sim;
	
	public AbstractDisk() {
	}

	@Override
	public void configure(DeviceConfiguration config, Simulation sim) throws DeviceConfigurationException, ConfigurationValueTypeException, IOException{
		this.sim=sim;
		this.config=config;
		
		this.heads=(int) config.getOptLong("heads", -1);
		this.tracksPerHead=(int) config.getOptLong("tracks_per_head", -1);
		this.sectorsPerTrack=(int) config.getOptLong("sectors_per_track", -1);
		this.sectorSize=(int) config.getOptLong("sector_size", -1);
		this.debug=config.getOptLong("debug", 0)==1;
		this.readOnly=config.getOptLong("read_only", 0)==1;
	}
	
	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException{
		;
	}
	
	
	public File getImageFile() {return this.imageFile;}
	public void setImageFile(File imageFile) {this.imageFile=imageFile;}
	
	public int getHeads() {
		return this.heads;
	}
	public int getTracksPerHead() {
		return this.tracksPerHead;
	}
	public int getSectorsPerTrack() {
		return this.sectorsPerTrack;
	}
	public int getSectorSize() {
		return this.sectorSize;
	}
	public long getDiskSize() {
		return ((long)heads)*((long)tracksPerHead)*((long)sectorsPerTrack)*((long)sectorSize);
	}
	
	@Override
	public void close() throws IOException {
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
}
