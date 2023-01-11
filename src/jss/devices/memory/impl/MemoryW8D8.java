package jss.devices.memory.impl;

import jss.devices.memory.AbstractMemoryDevice;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class MemoryW8D8 extends AbstractMemoryDevice {

	public long read(long address) throws MemoryAccessException {
		if(address>=mem.length)throw new MemoryAccessException(address,MemoryOperation.READ);

		return mem[(int) address] & 0xFF;
	}
	
	public void write(long address, long data) throws MemoryAccessException {
		if(address>=mem.length)throw new MemoryAccessException(address,MemoryOperation.READ);
		mem[(int) address]=(byte) data;
	}
	
}
