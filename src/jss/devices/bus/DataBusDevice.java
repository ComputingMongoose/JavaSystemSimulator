package jss.devices.bus;

import jss.devices.GenericDataDevice;
import jss.devices.memory.MemoryAccessException;

public class DataBusDevice {

	long start;
	long end;
	long offset;
	String name;
	boolean enabled;
	
	GenericDataDevice device;
	
	public DataBusDevice(GenericDataDevice device, long start, long end, long offset, String name, boolean enabled) {
		this.device=device;
		this.start=start;
		this.end=end;
		this.offset=offset;
		this.name=name;
		this.enabled=enabled;
	}
	
	public boolean isValidAddress(long address) {
		return enabled && address>=start && address<=end;
	}
	
	public boolean isEnabled() {return enabled;}
	
	public void setEnabled(boolean en) {enabled=en;}
	
	public String getName() {return name;}
	
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
