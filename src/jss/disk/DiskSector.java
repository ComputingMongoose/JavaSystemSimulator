package jss.disk;

public class DiskSector {

	public int head;
	public int track;
	public int sector;

	public byte[] data;
	
	public int error;
	public int deleted;
	
	public DiskSector(int h, int t, int sid) {
		head=h;
		track=t;
		sector=sid;
		data=null;
		error=1;
		deleted=0;
	}
	
	
}
