package jss.devices.cpu;

@SuppressWarnings("serial")
public class CPUInvalidOpcodeException extends Exception {

	private String opcode;

	public String getOpcode() {
		return opcode;
	}
	
	public CPUInvalidOpcodeException(long[] data) {
		opcode="";
		if(data!=null)
			for(int i=0;i<data.length;i++) {
				if(i>0)opcode+=" ";
				opcode+=String.format("%x", data[i]);
			}
	}
}
