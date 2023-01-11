package jss.devices;

import jss.devices.memory.MemoryAccessException;

public interface GenericDataDevice extends GenericDevice {

	public long read(long address) throws MemoryAccessException;
	public void write(long address,long value) throws MemoryAccessException;

}
