package jss.devices.memory.impl;

import jss.devices.memory.AbstractMemoryDevice;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class MemoryW4D4 extends AbstractMemoryDevice {

	public long read(long address) throws MemoryAccessException {
		long byteAddress=address>>1;
		
		if(byteAddress>=mem.length)throw new MemoryAccessException(address,MemoryOperation.READ);
		
		long data=mem[(int) byteAddress] & 0xFF;
		if((address&0x01)==0x01)data=data>>4;
		else data=data&0x0F;

		return data;
	}
	
	public void write(long address, long rdata) throws MemoryAccessException {
		long byteAddress=address>>1;
		
		if(byteAddress>=mem.length)throw new MemoryAccessException(address,MemoryOperation.WRITE);

		long data=rdata&0x0F;
		long d=mem[(int) byteAddress] & 0xFF;
		if((address&0x01)==0x01)d=(d & 0x0F) | (data<<4);
		else d=(d&0xF0)|data;

		mem[(int) byteAddress]=(byte) d;
	}
	
}
