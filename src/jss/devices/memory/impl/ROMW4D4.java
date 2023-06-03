package jss.devices.memory.impl;

import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class ROMW4D4 extends MemoryW4D4 {

	@Override
	public void write(long address, long rdata) throws MemoryAccessException {
		throw new MemoryAccessException(address,MemoryOperation.WRITE);
	}
	
}
