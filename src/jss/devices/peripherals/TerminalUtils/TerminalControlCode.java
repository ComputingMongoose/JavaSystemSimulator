package jss.devices.peripherals.TerminalUtils;

public class TerminalControlCode {

	public String code;
	public TerminalControlCodeExecutor executor;
	
	public TerminalControlCode(String code, TerminalControlCodeExecutor executor) {
		this.code=code;
		this.executor=executor;
	}
	
}
