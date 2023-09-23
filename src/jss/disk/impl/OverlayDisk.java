package jss.disk.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import jss.configuration.ConfigurationValue;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.disk.AbstractDisk;
import jss.disk.Disk;
import jss.disk.DiskSector;

public class OverlayDisk extends AbstractDisk {

	protected Disk baseDisk;
	protected Disk ovrDisk;
	
	public OverlayDisk() {
		super();
	}
	

	@Override
	public DiskSector[] readSector(int head, int track, int sector, int numSectors) {
		DiskSector[] ret=ovrDisk.readSector(head, track, sector, numSectors);
		if(ret==null)return baseDisk.readSector(head, track, sector, numSectors);
		
		boolean needsBase=false;
		for(DiskSector ds:ret)if(ds==null) {needsBase=true;break;}
		
		if(needsBase) {
			DiskSector []r1=baseDisk.readSector(head, track, sector, numSectors);
			for(int i=0;i<ret.length;i++) {
				if(ret[i]==null)ret[i]=r1[i];
			}
		}
		return ret;
	}

	@Override
	public int writeSector(DiskSector[] diskSectors) {
		return ovrDisk.writeSector(diskSectors);
	}
	
	@Override
	public void loadImage(File imageFile) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		this.imageFile=imageFile;
		
		baseDisk=DiskUtil.loadImage(sim,config,imageFile);
		
		this.heads=baseDisk.getHeads();
		this.tracksPerHead=baseDisk.getTracksPerHead();
		this.sectorsPerTrack=baseDisk.getSectorsPerTrack();
		this.sectorSize=baseDisk.getSectorSize();
		
		int pos = imageFile.getName().lastIndexOf('.');
		File ovrFile = new File(imageFile.getParent(), imageFile.getName().substring(0,pos)+".ovr");
		ovrDisk=new OVRDisk();
		DeviceConfiguration ovrConfig=new DeviceConfiguration("internal_overlay","OVRDisk");
		ovrConfig.set("heads", new ConfigurationValue(this.heads));
		ovrConfig.set("tracks_per_head", new ConfigurationValue(this.tracksPerHead));
		ovrConfig.set("sectors_per_track", new ConfigurationValue(this.sectorsPerTrack));
		ovrConfig.set("sector_size", new ConfigurationValue(this.sectorSize));
		ovrConfig.set("debug", new ConfigurationValue(this.config.getOptLong("debug", 0)));
		ovrDisk.configure(ovrConfig, sim);
		ovrDisk.initialize();
		ovrDisk.loadImage(ovrFile);
	}
	
	@Override
	public HashMap<String,DiskSector> getSectorsMap(){
		HashMap<String,DiskSector> mapBase=baseDisk.getSectorsMap();
		HashMap<String,DiskSector> mapOvr=ovrDisk.getSectorsMap();
		
		HashMap<String,DiskSector> map=new HashMap<>(mapBase.size());
		
		for(int head=0;head<this.heads;head++) {
			for(int track=0;track<this.tracksPerHead;track++) {
				for(int sector=0;sector<this.sectorsPerTrack;sector++) {
					String sid=String.format("%d_%d_%d", head,track,sector);
					DiskSector ds=null;
					if(mapOvr.containsKey(sid)) {
						ds=mapOvr.get(sid);
					}else if(mapBase.containsKey(sid)) {
						ds=mapBase.get(sid);
					}				
					
					if(ds!=null) {
						map.put(sid, ds);
					}
				}
			}
		}
		
		return map;
	}
	
	@Override
	public void setSectorsMap(HashMap<String,DiskSector> map) {
		this.ovrDisk.setSectorsMap(map);
	}
	
	
	
	@Override
	public int writeCompleteImage() {
		sim.writeToCurrentLog("OverlayDisk Starting to write a complete image. Will merge the overlay into the base image.");

		this.baseDisk.setSectorsMap(this.getSectorsMap());
		this.baseDisk.writeCompleteImage();
		this.ovrDisk.deleteImage();
		
		sim.writeToCurrentLog("OverlayDisk Write complete image done");
		return 0;
	}

	@Override
	public void deleteImage() {
		this.baseDisk.deleteImage();
		this.ovrDisk.deleteImage();
		sim.writeToCurrentLog("OVR Delete image complete");
	}
	
}
