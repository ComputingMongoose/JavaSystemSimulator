package jss.devices.bus;

@SuppressWarnings("serial")
public class ControlBusUnknownSignalException extends Exception {

	private String signal;
	
	public ControlBusUnknownSignalException(String signal) {
		this.signal=signal;
	}
	
	public String getSignal() {
		return signal;
	}
	
}

