package jss.disk.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataAccessDevice;
import jss.devices.bus.DataBus;
import jss.devices.memory.MemoryAccessException;
import jss.disk.Disk;
import jss.disk.DiskController;
import jss.disk.DiskDrive;
import jss.disk.DiskSector;
import jss.simulation.Simulation;

public class FloppyDiskController implements DiskController, GenericDataAccessDevice {

	Simulation sim;
	
	ArrayList<DiskDrive> drives;
	
	DataBus memory;
	boolean suspendOnDiskAccess;
	long addressLow;
	int opcpl; // operation complete
	long dmaStatus;
	boolean debug;

	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		this.sim=sim;
		drives=new ArrayList<>();
		this.opcpl=0;
		this.dmaStatus=0;
		suspendOnDiskAccess=config.getOptLong("suspend_on_disk_access", 0)!=0;
		debug=config.getOptLong("debug", 0)==1;
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		this.opcpl=0;
		this.dmaStatus=0;
	}
	
	@Override
	public long read(long address) throws MemoryAccessException {
		if(debug)sim.writeToCurrentLog(String.format("FDC Read [%X]", address));
		switch((int)address) {
		case 0:  // DSTS
			int status=0x08; // FDC present
			status|=0x10; // Double density present
			if(drives.size()>0 && drives.get(0).isReady())status|=1;
			if(drives.size()>1 && drives.get(1).isReady())status|=2;
			if(this.opcpl==1) {
				status|=4; // OPCPL = operation complete
				this.opcpl=0;
			}
			return status;
			
		case 1: // Read result type
			return 0;
			//return 0x2; // Result byte contains diskette ready status => if drive status change was detected
			
		case 3: // DMA RESULT
			return dmaStatus; // 0=COMPLETE
		}
		
		return 0;
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		if(debug)sim.writeToCurrentLog(String.format("FDC Write [%X], [%X]", address,value));
		
		switch((int)address) {
		case 1: // DMA LOWW 
			addressLow=value & 0xFF;
			break;
		case 2: // DMA HI + execute
			long dmaAddress=((value&0xFF)<<8) | (addressLow&0xFF);
			if(debug)sim.writeToCurrentLog(String.format("Received DMA HI address %X",dmaAddress));
			/*   864/    EA34 :                     		IOPB:
			     865/    EA34 : 80                  	        DB      80H             ; IOCW, NO UPDATE BIT SET
			     866/    EA35 : 04                  	        DB      04H             ; I/O INSTRUCTION, READ DISK 0
			     867/    EA36 : 1A                  	        DB      26              ; READ 26 SECTORS
			     868/    EA37 : 00                  	        DB      0               ; TRACK 0
			     869/    EA38 : 01                  	        DB      1               ; SECTOR 1
			     870/    EA39 : 00 30                           DW      TRK0            ; LOAD ADDRESS
			*/			
			int ioCW=(int)memory.read(dmaAddress);
			int ioInstr=(int)memory.read(dmaAddress+1);
			int numSectors=(int)memory.read(dmaAddress+2);
			int track=(int)memory.read(dmaAddress+3);
			int sector=(int)memory.read(dmaAddress+4);
			long loadAddress=memory.read(dmaAddress+5) | (memory.read(dmaAddress+6)<<8);
			
			int disk=(ioInstr & 0xF0);
			if(disk>0)disk=1;
			
			this.dmaStatus=0;
			
			if(debug)sim.writeToCurrentLog(String.format("IOPB content: %X %X %X %X %X %X", ioCW,ioInstr,numSectors,track,sector,loadAddress));
			//if(numSectors==26)sector--; // it starts at 1 => or my disk has error
			if(drives.size()>disk && drives.get(disk).getDisk()!=null && drives.get(disk).getDisk().getSectorsPerTrack()<sector)
				sector&=0x1F;
			
			if(sector>0)sector--;
			
			//sim.writeToCurrentLog(String.format("Will process: %X %X %X %X %X %X", ioCW,ioInstr,numSectors,track,sector,loadAddress));
			
			ioInstr=ioInstr&0x07; // last 3 bits
			switch(ioInstr) {
			case 0: // No operation
				if(debug)sim.writeToCurrentLog(String.format("FDC No operation"));
				break;
			case 1: // Seek
				sim.writeToCurrentLog(String.format("** FDC Seek"));
				break;
			case 2: // Format track
				sim.writeToCurrentLog(String.format("** FDC Format track"));
				break;
			case 3: // Recalibrate
				sim.writeToCurrentLog(String.format("** FDC Recalibrate"));
				break;
			case 4: // Read data
				if(debug)sim.writeToCurrentLog(String.format("FDC Read data"));
				
				DiskSector[] sectors=null;
				if(drives.size()>disk && drives.get(disk).getDisk()!=null)
					sectors=(drives.get(disk).getDisk()).readSector(0, track, sector, numSectors);
				
				if(sectors==null) {
					sim.writeToCurrentLog(String.format("Error reading sector DISK=[%02Xh] HEAD=[00h], TRACK=[%04Xh], SECTOR=[%04Xh]. Sector is real sector, the system calls this with sector+1.",disk,track,sector));
					this.dmaStatus=0x0C; // address error=0x08 seek error=0x04
					/*long currentAddress=loadAddress;
					for(int i=0;i<128;i++) {
						memory.write(currentAddress, 0);
						currentAddress++;
					}*/
				}else {
					long currentAddress=loadAddress;
					for(DiskSector s:sectors) {
						for(byte b:s.data) {
							memory.write(currentAddress, b);
							currentAddress++;
						}
					}
					if(debug)sim.writeToCurrentLog(String.format("Data read OK. Current address: %X",currentAddress));
				}

				break;
				
			case 5: // Verify CRC
				sim.writeToCurrentLog(String.format("** FDC Verify CRC"));
				break;
				
			case 6: // Write data
				if(debug)sim.writeToCurrentLog(String.format("FDC Write data"));
				ArrayList<DiskSector> diskSectors=new ArrayList<>();
				long currentAddress=loadAddress;
				Disk currentDisk=null;
				if(drives.size()>disk && drives.get(disk).getDisk()!=null)
					currentDisk=drives.get(disk).getDisk();

				if(currentDisk==null) {
					sim.writeToCurrentLog(String.format("Error writing sectors. No disk in drive."));
					this.dmaStatus=0x0C; // address error=0x08 seek error=0x04
				}else {
					
					for(int i=0;i<numSectors;i++) {
						DiskSector ds=new DiskSector(0,track,sector);
						ds.error=0;
						ds.deleted=0;
						sector++; if(sector>=currentDisk.getSectorsPerTrack()) {
							track++;sector=0;
							if(track>=currentDisk.getTracksPerHead())track=0;
						}
						ds.data=new byte[currentDisk.getSectorSize()];
						for(int j=0;j<ds.data.length;j++) {
							ds.data[j]=(byte) memory.read(currentAddress++);
						}
						diskSectors.add(ds);
					}
					
					DiskSector[] dsData=new DiskSector[diskSectors.size()];
					currentDisk.writeSector(diskSectors.toArray(dsData));
					
					if(debug)sim.writeToCurrentLog(String.format("Data write OK. Current address: %X",currentAddress));					
				}
				break;
				
			case 7: // Write deleted data
				sim.writeToCurrentLog(String.format("** FDC Write deleted data"));
				break;
				
			}
			
			this.opcpl=1;
			
			if(suspendOnDiskAccess) {
				sim.setSuspended(true);
				sim.writeToCurrentLog("suspend_on_disk_access is true => SUSPEND");
			}
			
			break;
		}
		
	}

	@Override
	public void attachDiskDrive(DiskDrive dd) {
		drives.add(dd);
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		memory=bus;
	}

	@Override
	public List<DiskDrive> getDrives() {
		return drives;
	}

}
