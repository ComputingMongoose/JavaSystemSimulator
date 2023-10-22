package jss.devices.peripherals.TerminalUtils;

public class TerminalControlSequenceHeathkit implements TerminalControlSequence {
	
	int save_cur_x, save_cur_y;
	boolean ansiMode;
	TerminalControlSequence controlSeqANSI;
	String terminalId;
	
	final TerminalControlCode[]controlCodes= {
		new TerminalControlCode("H", new TerminalControlCodeExecutor () { // HCUH - Cursor Home
			public void execute(TerminalStatus status, String sequence) {
				status.controlHome();
			}
		}),
		new TerminalControlCode("C", new TerminalControlCodeExecutor () { // HCUF - Cursor Forward 
			public void execute(TerminalStatus status, String sequence) {
				status.advanceCursorX(false);
			}
		}),
		new TerminalControlCode("D", new TerminalControlCodeExecutor () { // HCUB - Cursor Backward
			public void execute(TerminalStatus status, String sequence) {
				status.controlBackSpace(false);
			}
		}),
		new TerminalControlCode("B", new TerminalControlCodeExecutor () { // HCUD - Cursor Down
			public void execute(TerminalStatus status, String sequence) {
				int cur_y=status.getCur_y();
				if(cur_y<23) {cur_y++;status.setCur_y(cur_y);}
			}
		}),
		new TerminalControlCode("A", new TerminalControlCodeExecutor () { // HCUA - Cursor Up
			public void execute(TerminalStatus status, String sequence) {
				status.controlVerticalBackSpace();
			}
		}),
		new TerminalControlCode("I", new TerminalControlCodeExecutor () { // HRI - Reverse Index - cu scroll
			public void execute(TerminalStatus status, String sequence) {
				status.controlVerticalBackSpace();
				// TODO: Scroll
			}
		}),
		new TerminalControlCode("n", new TerminalControlCodeExecutor () { // HCPR - Cursor Position Report
			public void execute(TerminalStatus status, String sequence) {
				status.transmitChars(new char[] {27, 'Y', (char)status.getCur_y(), (char)status.getCur_x()});
			}
		}),
		new TerminalControlCode("j", new TerminalControlCodeExecutor () { // HSCP - Save Cursor Position
			public void execute(TerminalStatus status, String sequence) {
				save_cur_x=status.getCur_x();
				save_cur_y=status.getCur_y();
			}
		}),
		new TerminalControlCode("k", new TerminalControlCodeExecutor () { // HRCP - Set Cursot to Previously Saved Position
			public void execute(TerminalStatus status, String sequence) {
				status.setCur_x(save_cur_x);
				status.setCur_y(save_cur_y);
			}
		}),
		new TerminalControlCode("Y%%", new TerminalControlCodeExecutor () { // HDCA - Direct cursor addressing
			public void execute(TerminalStatus status, String sequence) {
				int line=control_current.charAt(1)-32;
				int col=control_current.charAt(2)-32;
				if(line>=0 && line<25)status.setCur_y(line);
				if(col>=0 && col<80)status.setCur_x(col);
			}
		}),
		new TerminalControlCode("E", new TerminalControlCodeExecutor () { // HCD - Clear Display
			public void execute(TerminalStatus status, String sequence) {
				status.controlClearScreen();
				status.controlHome();
			}
		}),
		new TerminalControlCode("b", new TerminalControlCodeExecutor () { // HBD - Erase Beginning of Display
			public void execute(TerminalStatus status, String sequence) {
				status.eraseFromBegin();
			}
		}),
		new TerminalControlCode("J", new TerminalControlCodeExecutor () { // HEOP - Erase End of Page
			public void execute(TerminalStatus status, String sequence) {
				status.eraseToEnd();
			}
		}),
		new TerminalControlCode("l", new TerminalControlCodeExecutor () { // HEL - Erase Entire Line
			public void execute(TerminalStatus status, String sequence) {
				status.eraseLine();
			}
		}),
		new TerminalControlCode("o", new TerminalControlCodeExecutor () { // HEBL - Erase Beginning of Line
			public void execute(TerminalStatus status, String sequence) {
				status.eraseLineFromBegin();
			}
		}),
		new TerminalControlCode("K", new TerminalControlCodeExecutor () { // HEOL - Erase to End of Line
			public void execute(TerminalStatus status, String sequence) {
				status.eraseLineToEnd();
			}
		}),
		new TerminalControlCode("L", new TerminalControlCodeExecutor () { // HIL - Insert Line
			public void execute(TerminalStatus status, String sequence) {
				status.controlInsertLine();
			}
		}),
		new TerminalControlCode("M", new TerminalControlCodeExecutor () { // HDL - Delete Line
			public void execute(TerminalStatus status, String sequence) {
				status.controlDeleteLine();
			}
		}),
		new TerminalControlCode("N", new TerminalControlCodeExecutor () { // HDCH - Delete Character
			public void execute(TerminalStatus status, String sequence) {
				status.controlDeleteCharacter();
			}
		}),
		new TerminalControlCode("@", new TerminalControlCodeExecutor () { // HEIM - Enter Insert Character Mode
			public void execute(TerminalStatus status, String sequence) {
				status.setInsertCharacterMode(true);
			}
		}),
		new TerminalControlCode("O", new TerminalControlCodeExecutor () { // HERM - Exit Insert Character Mode
			public void execute(TerminalStatus status, String sequence) {
				status.setInsertCharacterMode(false);
			}
		}),
		new TerminalControlCode("z", new TerminalControlCodeExecutor () { // HRAM - Reset to Power-Up Configuration
			public void execute(TerminalStatus status, String sequence) {
				status.setInsertCharacterMode(false);
			}
		}),
		new TerminalControlCode("r%", new TerminalControlCodeExecutor () { // HMBR - Modify Baud Rate
			public void execute(TerminalStatus status, String sequence) {
				;
			}
		}),
		new TerminalControlCode("x1", new TerminalControlCodeExecutor () { // HSM - Set Mode - Enable 25th line
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enable 25th line");
			}
		}),
		new TerminalControlCode("y1", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Disable 25th line
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Disable 25th line");
			}
		}),
		new TerminalControlCode("x2", new TerminalControlCodeExecutor () { // HSM - Set Mode - No key click
			public void execute(TerminalStatus status, String sequence) {
			}
		}),
		new TerminalControlCode("y2", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Key click
			public void execute(TerminalStatus status, String sequence) {
			}
		}),
		new TerminalControlCode("x3", new TerminalControlCodeExecutor () { // HSM - Set Mode - Hold screen mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Hold screen mode");
			}
		}),
		new TerminalControlCode("y3", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Exit hold screen mode
			public void execute(TerminalStatus status, String sequence) {
			}
		}),
		new TerminalControlCode("x4", new TerminalControlCodeExecutor () { // HSM - Set Mode - Block cursor
			public void execute(TerminalStatus status, String sequence) {
				status.setCursorCharacter(27);
			}
		}),
		new TerminalControlCode("y4", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Underscore cursor
			public void execute(TerminalStatus status, String sequence) {
				status.setCursorCharacter(17);
			}
		}),
		new TerminalControlCode("x5", new TerminalControlCodeExecutor () { // HSM - Set Mode - Cursor off
			public void execute(TerminalStatus status, String sequence) {
				status.setCursorCharacter(0); // 32 ?
			}
		}),
		new TerminalControlCode("y5", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Cursor on
			public void execute(TerminalStatus status, String sequence) {
				status.setCursorCharacter(17); 
			}
		}),
		new TerminalControlCode("x6", new TerminalControlCodeExecutor () { // HSM - Set Mode - Keypad shifted
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Keypad shifted : Not implemented");
			}
		}),
		new TerminalControlCode("y6", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Keypad unshifted
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Keypad unshifted");
			}
		}),
		new TerminalControlCode("x7", new TerminalControlCodeExecutor () { // HSM - Set Mode - Alternate keypad mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Alternate keypad mode: not implemented");
			}
		}),
		new TerminalControlCode("y7", new TerminalControlCodeExecutor () { // HRM - Reset Mode - Exit alternate keypad mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Exit alternate keypad mode");
			}
		}),
		new TerminalControlCode("x8", new TerminalControlCodeExecutor () { // HSM - Set Mode - Auto line feed on receipt of CR
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Auto LF on CR");
				status.setAutoLF(true);
			}
		}),
		new TerminalControlCode("y8", new TerminalControlCodeExecutor () { // HRM - Reset Mode - No auto LF
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Disable auto LF on CR");
				status.setAutoLF(false);
			}
		}),
		new TerminalControlCode("x9", new TerminalControlCodeExecutor () { // HSM - Set Mode - Auto CR on LF
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Auto CR on LF");
				status.setAutoCR(true);
			}
		}),
		new TerminalControlCode("y9", new TerminalControlCodeExecutor () { // HRM - Reset Mode - No auto CR
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Disable auto CR on LF");
				status.setAutoCR(false);
			}
		}),
		new TerminalControlCode("<", new TerminalControlCodeExecutor () { // HEAM - Enter ANSI mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enter ANSI mode");
				ansiMode=true;
			}
		}),
		new TerminalControlCode("[", new TerminalControlCodeExecutor () { // HEHS - Enter hold screen mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enter hold screen mode: Not implemented");
			}
		}),
		new TerminalControlCode("\\", new TerminalControlCodeExecutor () { // HXHS - Exit hold screen mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Exit hold screen mode");
			}
		}),
		new TerminalControlCode("p", new TerminalControlCodeExecutor () { // HERV - Enter reverse video mode
			public void execute(TerminalStatus status, String sequence) {
				status.controlReverseVideo();
				status.sim.writeToCurrentLog("Heatkit control seq: Enter reverse video mode");
			}
		}),
		new TerminalControlCode("q", new TerminalControlCodeExecutor () { // HXRV - Exit reverse video mode
			public void execute(TerminalStatus status, String sequence) {
				status.controlReverseVideo();
				status.sim.writeToCurrentLog("Heatkit control seq: Exit reverse video mode");
			}
		}),
		new TerminalControlCode("F", new TerminalControlCodeExecutor () { // HEGM - Enter graphics mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enter graphics mode");
				status.setCharacterSet(1);
			}
		}),
		new TerminalControlCode("G", new TerminalControlCodeExecutor () { // HXGM - Exit graphics mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Exit graphics mode");
				status.setCharacterSet(0);
			}
		}),
		new TerminalControlCode("t", new TerminalControlCodeExecutor () { // HEKS - Enter keypad shifted mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enter keypad shifted mode: Not implemented");
			}
		}),
		new TerminalControlCode("u", new TerminalControlCodeExecutor () { // HXKS - Exit keypad shifted mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Exit keypad shifted mode");
			}
		}),
		new TerminalControlCode("=", new TerminalControlCodeExecutor () { // HAKM - Enter alternate keypad mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Enter alternate keypad mode: Not implemented");
			}
		}),
		new TerminalControlCode(">", new TerminalControlCodeExecutor () { // HXAM - Exit alternate keypad mode
			public void execute(TerminalStatus status, String sequence) {
				status.sim.writeToCurrentLog("Heatkit control seq: Exit alternate keypad mode");
			}
		}),
		new TerminalControlCode("}", new TerminalControlCodeExecutor () { // HDK - Keyboard disabled
			public void execute(TerminalStatus status, String sequence) {
				if(status.isEnableKbLock())status.setKbLock(true);
			}
		}),
		new TerminalControlCode("{", new TerminalControlCodeExecutor () { // HEK - Keyboard enabled
			public void execute(TerminalStatus status, String sequence) {
				status.setKbLock(false);
			}
		}),
		new TerminalControlCode("v", new TerminalControlCodeExecutor () { // HEWA - Wrap around at end of line
			public void execute(TerminalStatus status, String sequence) {
				status.setEolBehavior(TerminalStatus.EOL_NEXT);
			}
		}),
		new TerminalControlCode("w", new TerminalControlCodeExecutor () { // HXWA - Discard at end of line
			public void execute(TerminalStatus status, String sequence) {
				status.setEolBehavior(TerminalStatus.EOL_DISCARD);
			}
		}),
		new TerminalControlCode("Z", new TerminalControlCodeExecutor () { // HID - Identify as VT52
			public void execute(TerminalStatus status, String sequence) {
				terminalId="VT52";
			}
		}),
		new TerminalControlCode("/K", new TerminalControlCodeExecutor () { // ??? - terminal identification
			public void execute(TerminalStatus status, String sequence) {
				status.transmitChars(terminalId.toCharArray());
			}
		}),
		new TerminalControlCode("]", new TerminalControlCodeExecutor () { // HX25 - transmit 25th line
			public void execute(TerminalStatus status, String sequence) {
				TextWithAttributes[][]text=status.getText();
				for(int j=0;j<80;j++) {
					status.transmitChar(text[24][j].getText());
				}
			}
		}),
		new TerminalControlCode("#", new TerminalControlCodeExecutor () { // HXMP - transmit page
			public void execute(TerminalStatus status, String sequence) {
				TextWithAttributes[][]text=status.getText();
				for(int i=0;i<24;i++) {
					for(int j=0;j<80;j++) {
						status.transmitChar(text[i][j].getText());
					}
				}
					
			}
		}),
		new TerminalControlCode("S", null), // HF1
		new TerminalControlCode("T", null), // HF2
		new TerminalControlCode("U", null), // HF3
		new TerminalControlCode("V", null), // HF4
		new TerminalControlCode("W", null), // HF5
		new TerminalControlCode("P", null), // HF7
		new TerminalControlCode("Q", null), // HF8
		new TerminalControlCode("R", null), // HF9
		
		
		
	};
	
	private TerminalStatus status;
	
	public TerminalControlSequenceHeathkit(TerminalStatus status) {
		this.status=status;
		this.save_cur_x=0;
		this.save_cur_y=0;
		this.ansiMode=false;
		controlSeqANSI=new TerminalControlSequenceANSI(status);
		terminalId="Heatkit";
	}
	
	private String control_current;
	private String control_current_pattern;
	private boolean control_parsing;
	
	// checks and executes the current sequence
	private boolean control_run() {
		boolean possible=false;
		for(int i=0;i<controlCodes.length;i++) {
			if(controlCodes[i].code.contentEquals(control_current) || controlCodes[i].code.contentEquals(control_current_pattern)) {
				if(controlCodes[i].executor!=null)
					controlCodes[i].executor.execute(status, control_current);
				control_parsing=false;
				control_current="";
				control_current_pattern="";
				return true;
			}else if(controlCodes[i].code.startsWith(control_current_pattern) || controlCodes[i].code.startsWith(control_current)) {
				possible=true;
			}else {
				String possible_pattern="";
				if(control_current_pattern.length()>0)possible_pattern=control_current_pattern.substring(0,control_current_pattern.length()-1);
				possible_pattern+="%";
				if(controlCodes[i].code.startsWith(possible_pattern)) {
					control_current_pattern=possible_pattern;
					possible=true;
				}

				if(controlCodes[i].code.contentEquals(control_current) || controlCodes[i].code.contentEquals(control_current_pattern)) {
					if(controlCodes[i].executor!=null)
						controlCodes[i].executor.execute(status, control_current);
					control_parsing=false;
					control_current="";
					control_current_pattern="";
					return true;
				}			
			}
		}
		
		return possible;
	}

	@Override
	public int processCharacter(int data) {
		
		if(ansiMode) {
			return controlSeqANSI.processCharacter(data);
		}
		
		if(control_parsing) {
			control_current+=(char)data;
			if(data>='0' && data<='9')control_current_pattern+="#";
			else control_current_pattern+=(char)data;
			
			if(!control_run()) {
				status.sim.writeToCurrentLog(String.format("Heathkit: Unknown terminal escape sequence: [%s]",control_current));
				
				/*for(int i=0;i<control_current.length();i++) {
					int d=control_current.charAt(i);
					// this is the same as the default branch => display the sequence
					int c=makeDisplayChar(d);
					if(c>0) {
						synchronized(lock) {
							if(c!=' ' || !space_adv)text[cur_y][cur_x]=(char)c;
							advanceCursorX();
							needsUpdate=true;
						}
					}
					
				}*/
				control_parsing=false;
				control_current="";
				control_current_pattern="";
			}
			return 1;
		}

		if(data==27) {
			control_current="";
			control_current_pattern="";
			control_parsing=true;
			return 1;
		}
		
		return 0;
	}

}
