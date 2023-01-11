package jss.devices.memory.impl;

import jss.devices.memory.AbstractMemoryDevice;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class MemoryW8D32 extends AbstractMemoryDevice {

	public long read(long address) throws MemoryAccessException {
		if(address>=mem.length-3)throw new MemoryAccessException(address,MemoryOperation.READ);
		return (long)(mem[(int) address]&0xFF) + 
				(((long)mem[(int) address+1])<<8)  |
				(((long)mem[(int) address+2])<<16) |
				(((long)mem[(int) address+3])<<24)
				;
	}
	
	public void write(long address, long data) throws MemoryAccessException {
		if(address>=mem.length-3)throw new MemoryAccessException(address,MemoryOperation.READ);
		mem[(int) address]=(byte) (data & 0xFF);
		mem[(int) address+1]=(byte) ((data>>8) & 0xFF);
		mem[(int) address+2]=(byte) ((data>>16) & 0xFF);
		mem[(int) address+3]=(byte) ((data>>24) & 0xFF);
	}
	
}
