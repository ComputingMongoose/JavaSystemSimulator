package jss.devices.cpu;

import java.util.List;

import jss.devices.GenericExecutionDevice;
import jss.devices.bus.DataBus;

public interface CPUDevice extends GenericExecutionDevice {

	public CPUState getCPUState();
	public DataBus getMemoryBus();
	
	public void addBreakpoint(long address);
	public void clearBreakpoints();
	public List<Long> getBreakpoints();
}
