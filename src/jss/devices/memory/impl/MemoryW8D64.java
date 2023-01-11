package jss.devices.memory.impl;

import jss.devices.memory.AbstractMemoryDevice;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class MemoryW8D64 extends AbstractMemoryDevice {

	public long read(long address) throws MemoryAccessException {
		if(address>=mem.length-7)throw new MemoryAccessException(address,MemoryOperation.READ);
		return (long)(mem[(int) address]&0xFF) + 
				(((long)mem[(int) address+1])<<8)  |
				(((long)mem[(int) address+2])<<16) |
				(((long)mem[(int) address+3])<<24) |
				(((long)mem[(int) address+4])<<32) |
				(((long)mem[(int) address+5])<<40) |
				(((long)mem[(int) address+6])<<48) |
				(((long)mem[(int) address+7])<<56)
				;
	}
	
	public void write(long address, long data) throws MemoryAccessException {
		if(address>=mem.length-7)throw new MemoryAccessException(address,MemoryOperation.READ);
		mem[(int) address]=(byte) (data & 0xFF);
		mem[(int) address+1]=(byte) ((data>>8) & 0xFF);
		mem[(int) address+2]=(byte) ((data>>16) & 0xFF);
		mem[(int) address+3]=(byte) ((data>>24) & 0xFF);
		mem[(int) address+4]=(byte) ((data>>32) & 0xFF);
		mem[(int) address+5]=(byte) ((data>>40) & 0xFF);
		mem[(int) address+6]=(byte) ((data>>48) & 0xFF);
		mem[(int) address+7]=(byte) ((data>>56) & 0xFF);
	}
	
}
