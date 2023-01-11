package jss.devices.memory.impl;

import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.PROMController;
import jss.devices.memory.PROMDevice;

public class PROMW8D64 extends MemoryW8D64 implements PROMDevice {

	boolean writeEnable;
	PROMController controller;
	
	public void attachPROMController(PROMController controller) {
		this.controller=controller;
	}
	
	public PROMW8D64() {
		super();
		writeEnable=false;
		controller=null;
	}
	
	@Override
	public void setWriteEnable(boolean enable) {
		writeEnable=enable;
	}
	
	@Override
	public void write(long address, long data) throws MemoryAccessException {
		if(controller!=null) {
			controller.setLastWriteData(read(address));
		}
		if(!writeEnable)return ;
		super.write(address, data);
	}

	@Override
	public boolean getWriteEnable() {
		return writeEnable;
	}

}
