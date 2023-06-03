package jss.devices.memory.impl;

import jss.devices.memory.AbstractMemoryDevice;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class MemoryW8D16 extends AbstractMemoryDevice {

	public long read(long address) throws MemoryAccessException {
		if(address>=mem.length-1)throw new MemoryAccessException(address,MemoryOperation.READ);
		return  (long)(mem[(int) address]&0xFF) | 
				(((long)mem[(int) address+1])<<8);
	}
	
	public void write(long address, long data) throws MemoryAccessException {
		if(address>=mem.length-1)throw new MemoryAccessException(address,MemoryOperation.WRITE);
		mem[(int) address]=(byte) (data & 0xFF);
		mem[(int) address+1]=(byte) ((data>>8) & 0xFF);
	}
	
}
