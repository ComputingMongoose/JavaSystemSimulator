package jss.devices.cpu;

import jss.devices.GenericDataDevice;

public interface Disassembler {

	public String disassemble(long startAddress, long size, GenericDataDevice mem);
	
}
