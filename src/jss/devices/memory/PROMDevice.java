package jss.devices.memory;

public interface PROMDevice extends MemoryDevice {

	public void setWriteEnable(boolean enable);
	public boolean getWriteEnable();
	public void attachPROMController(PROMController controller);
	
}
