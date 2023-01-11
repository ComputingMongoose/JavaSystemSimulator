package jss.devices.memory;

@SuppressWarnings("serial")
public class MemoryAccessException extends Exception {

	private long address;
	private MemoryOperation op;
	
	public MemoryAccessException(long address,MemoryOperation op) {
		this.address=address;
		this.op=op;
	}
	
	public long getAddress() {
		return this.address;
	}
	
	public MemoryOperation getMemoryOperation() {
		return this.op;
	}
	
}
