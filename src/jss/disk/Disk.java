package jss.disk;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDevice;

public interface Disk extends GenericDevice {

	public int getHeads();
	public int getTracksPerHead();
	public int getSectorsPerTrack();
	public int getSectorSize();
	public long getDiskSize();
	public File getImageFile();
	public void setImageFile(File imageFile);
	
	public HashMap<String,DiskSector> getSectorsMap();
	public void setSectorsMap(HashMap<String,DiskSector> map);
	
	public DiskSector[] readSector(int head, int track, int sector, int numSectors);
	public int writeSector(DiskSector[] ds);
	
	public void loadImage(File imageFile) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException;
	public void deleteImage();
	
	public int writeCompleteImage();
	public void close() throws IOException;
	
}
