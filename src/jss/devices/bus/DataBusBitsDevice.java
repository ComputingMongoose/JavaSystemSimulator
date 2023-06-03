package jss.devices.bus;

import jss.devices.GenericDataDevice;
import jss.devices.memory.MemoryAccessException;

public class DataBusBitsDevice {

	long start;
	long end;
	long offset;
	
	
	
	GenericDataDevice device;
	
	public DataBusBitsDevice(GenericDataDevice device, long start, long end, long offset) {
		this.device=device;
		this.start=start;
		this.end=end;
		this.offset=offset;
	}
	
	public boolean isValidAddress(long address) {
		return address>=start && address<=end;
	}
	
	public long getMappedAddress(long address) {
		return address-offset;
	}
	
	public long read(long address) throws MemoryAccessException {
		return device.read(address-offset);
	}

	public void write(long address,long data) throws MemoryAccessException {
		device.write(address-offset,data);
	}
}
