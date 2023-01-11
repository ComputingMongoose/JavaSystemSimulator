package jss.devices.memory;

public interface PROMController extends MemoryDevice {

	public void attachPROM(PROMDevice device);
	public void setLastWriteData(long data);
	
}
