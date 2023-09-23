package jss.devices.cpu;

import jss.devices.GenericDataDevice;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.memory.MemoryAccessException;

public abstract class AbstractDisassembler implements Disassembler {

	protected long PC;
	protected long last_PC;
	
	protected long startAddress;
	protected long memorySize;
	protected GenericDataDevice memoryBus;
	
	StringBuffer outputData;

    protected void write(String s) throws MemoryAccessException{
        outputData.append(String.format("%06X    ",last_PC));
        for(long i=last_PC;i<PC;i++){
            outputData.append(String.format("%02X",(memoryBus.read(i)&0xFF)));
        }
        int n=(int)(6-(PC-last_PC));
        if(n>0)
            outputData.append(new String(new char[n*2]).replace("\0", " "));
        outputData.append(s);
        outputData.append("\n");
    }

	
	@Override
	public String disassemble(long startAddress, long size, GenericDataDevice mem) {
		this.startAddress=startAddress;
		this.memorySize=size;
		this.memoryBus=mem;
		
		last_PC=startAddress;
		PC=startAddress;
		
		if(outputData==null)outputData=new StringBuffer();
		outputData.setLength(0);
		
		if(mem==null) {
			outputData.append("*** Disassembler: Memory device is null");
			return outputData.toString();
		}
		
		while(true) {
			try {
				step();
			}catch(MemoryAccessException ex1) {break;
			}catch(ControlBusUnknownSignalException ex2) {;
			}catch(CPUInvalidOpcodeException ex3) {
				try {
					write(String.format("DB %02X", memoryBus.read(last_PC)));
				}catch(MemoryAccessException ex4) {;}
				PC=last_PC+1;
			}
			if(PC>=startAddress+size)break;
		}
		
		return outputData.toString();
	}

	public abstract void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException;
}
