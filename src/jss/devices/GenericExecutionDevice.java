package jss.devices;

import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;

public interface GenericExecutionDevice extends GenericDevice {

	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException;
	
}
