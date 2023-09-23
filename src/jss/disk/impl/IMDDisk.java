package jss.disk.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.HashMap;

import jss.disk.AbstractDisk;
import jss.disk.DiskSector;

public class IMDDisk extends AbstractDisk {
	HashMap<String,DiskSector> sectorsMap=new HashMap<>(1000);
	HashMap<String,Long> sectorsPosMap=new HashMap<>(1000);
	
	private static int[] sector_size_map=new int[] {128,256,512,1024,2048,4096,8192};


	public IMDDisk() {
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
				sim.writeToCurrentLog(String.format("IMD: Sector not found HEAD=[%d] TRACK=[%d] SECTOR=[%d] SID=[%s]",ch,ct,cs,sid));
				return null;
			}
			
			ret[s]=sectorsMap.get(sid);
			
			cs++;
			if(cs>=this.sectorsPerTrack) {
				cs=0;
				ct++;
				if(ct>=this.tracksPerHead) {
					ct=0;
					ch++;
					if(ch>=this.heads)ch=0;
				}
			}
		}
		return ret;
	}

	@Override
	public int writeSector(DiskSector[] diskSectors) {
		if(isReadOnly())return -1;

		// Assume we can write in the existing image
		boolean writtenOK=true;
		try {
			RandomAccessFile f=new RandomAccessFile(imageFile,"rw");
			
			for(DiskSector ds:diskSectors) {
				String sid=String.format("%d_%d_%d", ds.head,ds.track,ds.sector);
				long pos=0;
				if(sectorsPosMap.containsKey(sid)) {
					pos=sectorsPosMap.get(sid);
				}else {
					writtenOK=false;
					break;
				}
				
				sectorsMap.put(sid, ds);
				f.seek(pos);
				int type=1; // normal
				if(ds.deleted!=0)type=3; // normal,deleted
				if(ds.error!=0)type=5; // normal,error
				f.write(type);
				f.write(ds.data);
			}
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			sim.writeToCurrentLog("IMD ERROR Exception writing data: "+e.getMessage());
			return -1;
		}
		
		if(!writtenOK) { // Cannot write in the existing IMD => create new IMD
			sim.writeToCurrentLog("IMD WRITE Cannot re-use existing image. Will write a complete image.");
			writeCompleteImage();
		}
		
		return 0;
	}
	
	@Override
	public int writeCompleteImage() {
		sim.writeToCurrentLog("IMD Starting to write a complete image");

		int sizeID=-1;
		for(int i=0;i<sector_size_map.length;i++)
			if(this.sectorSize==sector_size_map[i]) {sizeID=i;break;}
		if(sizeID==-1) {
			sim.writeToCurrentLog("IMD ERROR Cannot determine sector size ID from the sector_size_map");
			return -1;
		}
		
		
		// Any existing positions are now obsolete
		if(sectorsPosMap==null)sectorsPosMap=new HashMap<>(1000);
		else sectorsPosMap.clear();
		
		byte[] sector_numbering_map=new byte[this.sectorsPerTrack];
		for(int i=0;i<this.sectorsPerTrack;i++)
			sector_numbering_map[i]=(byte)(i+1);
		
		BufferedOutputStream out;
		long fpos=0;
		try {
			out = new BufferedOutputStream(new FileOutputStream(imageFile));
			
			out.write(0x1A); fpos++; // no comment
			
			for(int head=0;head<this.heads;head++) {
				for(int track=0;track<this.tracksPerHead;track++) {
					out.write(0); fpos++; // mode
					out.write(track); fpos++; 
					out.write(head); fpos++;
					out.write(this.sectorsPerTrack); fpos++;
					out.write(sizeID); fpos++;
					out.write(sector_numbering_map); fpos+=sector_numbering_map.length;
					for(int sector=0;sector<this.sectorsPerTrack;sector++) {
						String sid=String.format("%d_%d_%d", head,track,sector);
						long current_fpos=fpos;
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
						int type=1; // normal
						if(ds.deleted!=0)type=3; // normal,deleted
						if(ds.error!=0)type=5; // normal,error
						out.write(type); fpos++;
						out.write(ds.data); fpos+=ds.data.length;

						sectorsPosMap.put(sid, current_fpos);
					}
				}
			}
			
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			sim.writeToCurrentLog("IMD ERROR Exception while writing complete image: "+e.getMessage());
			return -1;
		}
		
		
		sim.writeToCurrentLog("IMD Write complete image done");
		return 0;
	}
	
	@Override
	public void loadImage(File imageFile) throws IOException {
		this.imageFile=imageFile;

		sim.writeToCurrentLog("IMD Reading disk image ["+imageFile.getName()+"]");
		
		if(sectorsMap==null)sectorsMap=new HashMap<>(1000);
		else sectorsMap.clear();
		if(sectorsPosMap==null)sectorsPosMap=new HashMap<>(1000);
		else sectorsPosMap.clear();
		
		int max_head=-1;
		int max_track=-1;
		int max_sector=-1;
		
		BufferedInputStream in=new BufferedInputStream(new FileInputStream(imageFile));
		
		long fpos=0;
		
		int b;
		for(b=in.read();b!=-1 && b!=0x1A;b=in.read())fpos++;
		if(b==-1) {in.close();return ;}
		fpos++;
		
		
		while(true) {
			int mode=in.read(); fpos++;
			int track=in.read(); fpos++;
			int head=in.read(); fpos++;
			int sectors=in.read(); fpos++;
			int size=in.read(); fpos++;
			int sector_size=-1; 
			if(size!=-1 && size<sector_size_map.length)sector_size=sector_size_map[size];
			if(debug)sim.writeToCurrentLog(String.format("IMD MODE=%d TRACK=%d HEAD=%d SECTORS=%d SIZE=%d SECTOR_SIZE=%d", mode,track,head,sectors,size,sector_size));
			
			if(size==-1 || sectors==-1 || head==-1 || track==-1 || mode==-1 || sector_size==-1)break;
			
			sectorSize=sector_size;
			
			byte[] sector_numbering_map=new byte[sectors];
			int sz=in.read(sector_numbering_map); fpos+=sz;
			if(sz<sector_numbering_map.length) {
				sim.writeToCurrentLog("IMD ERROR reading sector numbering map");
				break;
			}
			byte[] sector_cylinder_map=null;
			if((head & 0x80)!=0) {  // sector cylinder map present
				sector_cylinder_map=new byte[sectors];
				sz=in.read(sector_cylinder_map); fpos+=sz;
				if(sz<sector_cylinder_map.length) {
					sim.writeToCurrentLog("IMD ERROR reading sector cylinder map");
					break;
				}
				
			}
			byte[] sector_head_map=null;
			if((head & 0x40)!=0) {  // sector head map present
				sector_head_map=new byte[sectors];
				sz=in.read(sector_head_map); fpos+=sz;
				if(sz<sector_head_map.length) {
					sim.writeToCurrentLog("IMD ERROR reading sector head map");
					break;
				}
				
			}
			
			for(int i=0;i<sectors;i++) {
				
				long current_fpos=fpos;
				boolean regular_data=false;
				
				int type=in.read(); fpos++;
				if(type==-1)break;
				
				int h=head;
				if(sector_head_map!=null)h=sector_head_map[i];
				int t=track;
				if(sector_cylinder_map!=null)t=sector_cylinder_map[i];
				int sid=sector_numbering_map[i]-1; // 1 to sectors
				if(debug)sim.writeToCurrentLog(String.format("IMD   SECTOR DATA [%d] H=%d T=%d S=%d",i,h,t,sid));
				DiskSector ds=new DiskSector(h,t,sid);
				switch(type) {
				case 0: // error
					break; 
				case 1: // normal data
					regular_data=true;
					ds.data=new byte[sector_size];
					sz=in.read(ds.data); fpos+=sz;
					if(sz<ds.data.length) {
						sim.writeToCurrentLog("IMD ERROR reading sector data");
					}else ds.error=0;
					break;
				case 2: // normal, compressed
					b=in.read(); fpos++;
					if(b!=-1) {
						ds.data=new byte[sector_size];
						for(int j=0;j<ds.data.length;j++)ds.data[j]=(byte)(b&0xFF);
						ds.error=0;
					}
					break;
				case 3: // normal, deleted
					regular_data=true;
					ds.data=new byte[sector_size];
					sz=in.read(ds.data); fpos+=sz;
					if(sz<ds.data.length) {
						sim.writeToCurrentLog("IMD ERROR reading sector data");
					}else ds.error=0;
					ds.deleted=1;
					break;
					
				case 4: // compressed, deleted
					b=in.read(); fpos++;
					if(b!=-1) {
						ds.data=new byte[sector_size];
						for(int j=0;j<ds.data.length;j++)ds.data[j]=(byte)(b&0xFF);
						ds.error=0;
					}
					ds.deleted=1;
					break;
					
				case 5: // normal, with error
					regular_data=true;
					ds.data=new byte[sector_size];
					sz=in.read(ds.data); fpos+=sz;
					if(sz<ds.data.length) {
						sim.writeToCurrentLog("Error reading sector data");
					}
					ds.error=1;
					break;
					
				case 6: // compressed, with error
					b=in.read(); fpos++;
					if(b!=-1) {
						ds.data=new byte[sector_size];
						for(int j=0;j<ds.data.length;j++)ds.data[j]=(byte)(b&0xFF);
					}
					ds.error=1;
					break;

				case 7: // normal, deleted, with error
					ds.data=new byte[sector_size];
					sz=in.read(ds.data); fpos+=sz;
					if(sz<ds.data.length) {
						sim.writeToCurrentLog("Error reading sector data");
					}
					ds.error=1;
					ds.deleted=1;
					break;
					
				case 8: // compressed, deleted, with error
					b=in.read(); fpos++;
					if(b!=-1) {
						ds.data=new byte[sector_size];
						for(int j=0;j<ds.data.length;j++)ds.data[j]=(byte)(b&0xFF);
					}
					ds.error=1;
					ds.deleted=1;
					break;
					
				default:
					sim.writeToCurrentLog("Unknown data type: "+type);
				}
				
				String sidString=String.format("%d_%d_%d", ds.head,ds.track,ds.sector);
				sectorsMap.put(sidString,ds);
				if(regular_data) {
					sectorsPosMap.put(sidString, current_fpos);
				}
				if(ds.head>max_head)max_head=ds.head;
				if(ds.track>max_track)max_track=ds.track;
				if(ds.sector>max_sector)max_sector=ds.sector;
			}
			
		}
		
		in.close();
		
		this.heads=max_head+1;
		this.tracksPerHead=max_track+1;
		this.sectorsPerTrack=max_sector+1;
		
		
		sim.writeToCurrentLog("IMD Finished reading disk image ["+imageFile.getName()+"]");
		sim.writeToCurrentLog(String.format("IMD HEADS=[%d] TRACKS=[%d] SECTORS=[%d] SECTOR_SIZE=[%d]", this.heads,this.tracksPerHead,this.sectorsPerTrack,this.sectorSize));
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
			sim.writeToCurrentLog("IMD Delete image complete");
		} catch (IOException e) {
			sim.writeToCurrentLog("IMD ERROR Deleting image: "+e.getMessage());
			e.printStackTrace();
		}
		
	}

}
