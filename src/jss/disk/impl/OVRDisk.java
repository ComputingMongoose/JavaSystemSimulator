package jss.disk.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.HashMap;

import jss.disk.AbstractDisk;
import jss.disk.DiskSector;

public class OVRDisk extends AbstractDisk {
	HashMap<String,DiskSector> sectorsMap=new HashMap<>(1000);
	HashMap<String,Long> sectorsPosMap=new HashMap<>(1000);
	
	final static String signature="OVR10";

	
	public OVRDisk() {
		super();
	}
	

	@Override
	public DiskSector[] readSector(int head, int track, int sector, int numSectors) {
		DiskSector[] ret=new DiskSector[numSectors];
		int ch=head;
		int ct=track;
		int cs=sector;
		for(int s=0;s<numSectors;s++) {
			String sid=String.format("%d_%d_%d", ch,ct,cs);
			if(!this.sectorsMap.containsKey(sid)) {
				if(debug)sim.writeToCurrentLog(String.format("OVRDisk: Sector not found %s",sid));
				ret[s]=null;
			}else
				ret[s]=sectorsMap.get(sid);
			
			cs++;
			if(cs>this.sectorsPerTrack) {
				cs=0;
				ct++;
				if(ct>this.tracksPerHead) {
					ct=0;
					ch++;
					if(ch>this.heads)ch=0;
				}
			}
		}
		return ret;
	}

	@Override
	public int writeSector(DiskSector[] diskSectors) {
		if(isReadOnly())return -1;
		
		try {
			RandomAccessFile f=new RandomAccessFile(imageFile,"rw");
			
			boolean writeEOF=false;
			
			for(DiskSector ds:diskSectors) {
				String sid=String.format("%d_%d_%d", ds.head,ds.track,ds.sector);
				long pos=0;
				if(sectorsPosMap.containsKey(sid)) {
					pos=sectorsPosMap.get(sid);
				}else {
					if(f.length()<signature.length()) {
						for(int i=0;i<signature.length();i++)
							f.write(signature.charAt(i));
						f.write(this.heads);
						f.write(this.tracksPerHead);
						f.write(this.sectorsPerTrack);
						int c1=this.sectorSize&0xFF;
						int c2=(this.sectorSize&0xFFFF)>>8;
						f.write(c1);
						f.write(c2);
						pos=signature.length()+5;
					}else {
						pos=f.length()-3;
					}
					writeEOF=true;
					sectorsPosMap.put(sid, pos);
				}
				sectorsMap.put(sid, ds);
				
				f.seek(pos);
				f.write(ds.head);
				f.write(ds.track);
				f.write(ds.sector);
				f.write(ds.deleted);
				f.write(ds.data);
			}
			
			if(writeEOF) {
				f.write(-1);
				f.write(-1);
				f.write(-1);
			}
			
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			sim.writeToCurrentLog("OVR ERROR Exception writing data: "+e.getMessage());
			return -1;
		}
		
		return 0;
	}
	
	@Override
	public int writeCompleteImage() {
		sim.writeToCurrentLog("OVR Starting to write a complete image");

		// Any existing positions are now obsolete
		if(sectorsPosMap==null)sectorsPosMap=new HashMap<>(1000);
		else sectorsPosMap.clear();		
		
		try {
			RandomAccessFile f=new RandomAccessFile(imageFile,"rw");

			long fpos=0;
			long current_fpos=0;
			
			for(int i=0;i<signature.length();i++)
				f.write(signature.charAt(i));
			f.write(this.heads);
			f.write(this.tracksPerHead);
			f.write(this.sectorsPerTrack);
			int c1=this.sectorSize&0xFF;
			int c2=(this.sectorSize&0xFFFF)>>8;
			f.write(c1);
			f.write(c2);
			fpos=signature.length()+5;
			
			for(int head=0;head<this.heads;head++) {
				for(int track=0;track<this.tracksPerHead;track++) {
					for(int sector=0;sector<this.sectorsPerTrack;sector++) {
						String sid=String.format("%d_%d_%d", head,track,sector);
						current_fpos=fpos;
						DiskSector ds=null;
						if(sectorsMap.containsKey(sid)) {
							ds=sectorsMap.get(sid);
						}else {
							ds=new DiskSector(head,track,sector);
							ds.error=0;
							ds.deleted=0;
							ds.data=new byte[this.sectorSize];
							for(int i=0;i<ds.data.length;i++)ds.data[i]=0;
							sectorsMap.put(sid,ds);
						}
						sectorsPosMap.put(sid, current_fpos);
						f.write(ds.head); fpos++;
						f.write(ds.track); fpos++;
						f.write(ds.sector); fpos++;
						f.write(ds.deleted); fpos++;
						f.write(ds.data); fpos+=ds.data.length;
						
					}
				}
			}
			
			f.write(-1);
			f.write(-1);
			f.write(-1);
			
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			sim.writeToCurrentLog("OVR ERROR Exception writing data: "+e.getMessage());
			return -1;
		}
		
		
		sim.writeToCurrentLog("OVR Write complete image done");
		return 0;
	}	
	
	@Override
	public void loadImage(File imageFile) throws IOException {
		this.imageFile=imageFile;
		
		if(sectorsMap==null) {
			sectorsMap=new HashMap<>(1000);
			sectorsPosMap=new HashMap<>(1000);
		}
		else {
			sectorsPosMap.clear();
			sectorsMap.clear();
		}
		
		sim.writeToCurrentLog(String.format("OVR loading image [%s]", imageFile.getName()));
		
		if(!Files.exists(imageFile.toPath())) {
			sim.writeToCurrentLog("OVR file not found");
			return ;
		}
		
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(imageFile));
		long fpos=0;

		for(int i=0;i<signature.length();i++) {
			int c=in.read();
			if(c!=signature.charAt(i)) {
				sim.writeToCurrentLog("OVR Disk Invalid signature");
				in.close();
				return;
			}
			fpos++;
		}
		

		this.heads=in.read(); fpos++;
		this.tracksPerHead=in.read(); fpos++;
		this.sectorsPerTrack=in.read(); fpos++;
		this.sectorSize=in.read(); fpos++;
		int t=in.read(); fpos++;
		this.sectorSize=(t<<8) + this.sectorSize;
		
		while(true) {
			long dspos=fpos;
			int head=in.read(); fpos++;
			int track=in.read(); fpos++;
			int sector=in.read(); fpos++;
			if(debug)sim.writeToCurrentLog(String.format("OVR Reading HEAD=%d TRACK=%d SECTOR=%d", head,track,sector));
			
			if(sector==-1 || head==-1 || track==-1 || (sector==255 && head==255 && track==255))break;
			
			DiskSector ds=new DiskSector(head,track,sector);
			ds.deleted=in.read(); fpos++;
			ds.error=0;
			ds.data=new byte[this.sectorSize];
			int sz=in.read(ds.data); fpos+=ds.data.length;
			if(sz<ds.data.length) {
				sim.writeToCurrentLog(String.format("OVR ERROR Reading HEAD=%d TRACK=%d SECTOR=%d", head,track,sector));
				break;
			}		
			String sid=String.format("%d_%d_%d", ds.head,ds.track,ds.sector);
			sectorsMap.put(sid,ds);
			sectorsPosMap.put(sid, dspos);
		}
		
		in.close();
		
		sim.writeToCurrentLog("OVR Finished reading disk image ["+imageFile.getName()+"]");
		sim.writeToCurrentLog(String.format("OVR HEADS=[%d] TRACKS=[%d] SECTORS=[%d] SECTOR_SIZE=[%d]", this.heads,this.tracksPerHead,this.sectorsPerTrack,this.sectorSize));
	}

	@Override
	public HashMap<String,DiskSector> getSectorsMap(){
		return sectorsMap;
	}
	
	@Override
	public void setSectorsMap(HashMap<String,DiskSector> map) {
		this.sectorsMap=map;
		if(this.sectorsPosMap!=null)this.sectorsPosMap.clear();
	}
	
	@Override
	public void deleteImage() {
		try {
			Files.delete(imageFile.toPath());
			if(this.sectorsMap!=null)this.sectorsMap.clear();
			if(this.sectorsPosMap!=null)this.sectorsPosMap.clear();
			sim.writeToCurrentLog("OVR Delete image complete");
		} catch (IOException e) {
			sim.writeToCurrentLog("OVR ERROR Deleting image: "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
}
