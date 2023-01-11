package jss.devices.memory.impl;

import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;

public class ROMW8D32 extends MemoryW8D32 {

	@Override
	public void write(long address, long data) throws MemoryAccessException {
		throw new MemoryAccessException(address,MemoryOperation.READ);
	}
	
}
