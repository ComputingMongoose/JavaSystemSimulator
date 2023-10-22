package jss.devices.peripherals.TerminalUtils;

public class TerminalControlSequenceADM implements TerminalControlSequence {
	
	private TerminalStatus status;
	
	public TerminalControlSequenceADM(TerminalStatus status) {
		this.status=status;
	}
	

	@Override
	public int processCharacter(int data) {
		switch(data) {
		case 8: // BS
			status.controlBackSpace();
			break;
		case 9: // TAB
			status.controlTab();
			break;
		case 10: // LF
			status.advanceCursorY();
			if(status.isAutoCR())status.setCur_x(0);
			break;
		case 11: // VT
			status.controlVerticalBackSpace();
			break;
		case 12: // FF
			status.advanceCursorX(false);
			break;
		case 13: // CR
			status.setCur_x(0);
			if(status.isAutoLF())status.advanceCursorY();
			break;
		case 14: // SO
			if(status.isEnableKbLock())status.setKbLock(false);
			break;
		case 15: // SI
			if(status.isEnableKbLock()) status.setKbLock(true);
			break;
		case 26: // SUB
			status.controlClearScreen();
			break;

		// case 27: // ESC - handled by a TerminalControlSequence
			
		case 30: // RS - HOME cursor
			status.controlHome();
			break;

		default:
			return 0; // unknown
		}
		
		return 1;
	}

}
