package jss.devices.peripherals.TerminalUtils;

public class TerminalControlSequenceANSI implements TerminalControlSequence {
	
	// https://www.real-world-systems.com/docs/ANSIcode.html
	
	static final String[][]ansiCodes= {
	    new String[] {"[H","home"},
	    new String[] {"[##;##H","set"},
	    new String[] {"[##;##f","set"},
	    new String[] {"[#A","up"},
	    new String[] {"[##A","up"},
	    new String[] {"[#B","down"},
	    new String[] {"[##B","down"},
	    new String[] {"[#C","right"},
	    new String[] {"[##C","right"},
	    new String[] {"[#D","left"},
	    new String[] {"[##D","left"},
	    new String[] {"[#E","begin_next"},
	    new String[] {"[##E","begin_next"},
	    new String[] {"[#F","begin_prev"},
	    new String[] {"[##F","begin_prev"},
	    new String[] {"[#G","set_column"},
	    new String[] {"[##G","set_column"},
	    //new String[] {"[6n","request_cursor_position"},
	    new String[] {" M","line_up"},
	    new String[] {"[J","erase_to_end"},
	    new String[] {"[0J","erase_to_end"},
	    new String[] {"[1J","erase_to_begin"},
	    new String[] {"[2J","erase_all"},
	    //new String[] {"[3J","erase_saved_lines"}, // ????????????
	    new String[] {"[K","erase_to_end_line"},
	    new String[] {"[0K","erase_to_end_line"},
	    new String[] {"[1K","erase_to_begin_line"},
	    new String[] {"[2K","erase_all_line"},

	    new String[] {"[A","up1"},
	    new String[] {"[B","down1"},
	    new String[] {"[C","right1"},
	    new String[] {"[D","left1"},
	    
	    // ANSI Mode and Cursor Key Mode Set
	    new String[] {"OA","up1"},
	    new String[] {"OB","down1"},
	    new String[] {"OC","right1"},
	    new String[] {"OD","left1"},

	    // VT-52
	    new String[] {"A","up1"},
	    new String[] {"B","down1"},
	    new String[] {"C","right1"},
	    new String[] {"D","left1"},
	    
	    //VT-102
	    new String[] {"[1@","insert_char"},
	    new String[] {"[1P","delete_char"},
	    new String[] {"[1L","insert_line"},
	    new String[] {"[1M","delete_line"},
	    
	};
	
	private TerminalStatus status;
	
	public TerminalControlSequenceANSI(TerminalStatus status) {
		this.status=status;
	}
	
	private String ansi_current;
	private String ansi_current_pattern;
	private boolean ansi_parsing;
	
	private void ansi_apply_sequence(String seq) {
			if(seq.contentEquals("home")) {
				status.setCur_x(0);status.setCur_y(0);
			}else if(seq.contentEquals("set")) {
				try {
					status.setCur_y(Integer.parseInt(ansi_current.substring(1,3))-1);
					status.setCur_x(Integer.parseInt(ansi_current.substring(4,6))-1);
				}catch(Exception ex) {;}
			}else if(seq.contentEquals("up")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_y(status.getCur_y() - up);
			}else if(seq.contentEquals("down")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_y(status.getCur_y() + up);
			}else if(seq.contentEquals("left")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_x(status.getCur_x() - up);
			}else if(seq.contentEquals("right")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_x(status.getCur_x() + up);
			}else if(seq.contentEquals("up1")) {
				status.setCur_y(status.getCur_y() - 1);
			}else if(seq.contentEquals("down1")) {
				status.setCur_y(status.getCur_y() + 1);
			}else if(seq.contentEquals("left1")) {
				status.setCur_x(status.getCur_x() - 1);
			}else if(seq.contentEquals("right1")) {
				status.setCur_x(status.getCur_x() + 1);
			}else if(seq.contentEquals("begin_next")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_y(status.getCur_y() + up);
				status.setCur_y(status.getCur_y() + 1);
				status.setCur_x(0);
			}else if(seq.contentEquals("begin_prev")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				status.setCur_y(status.getCur_y() - up);
				status.setCur_y(status.getCur_y() - 1);
				status.setCur_x(0);
			}else if(seq.contentEquals("set_column")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3))-1;
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2))-1;
					}catch(Exception ex1) {;}
				}
				status.setCur_x(up);
			}else if(seq.contentEquals("line_up")) {
				status.setCur_y(status.getCur_y() - 1);
			}else if(seq.contentEquals("erase_to_end")) {
				status.eraseToEnd();
			}else if(seq.contentEquals("erase_to_begin")) {
				status.eraseFromBegin();
			}else if(seq.contentEquals("erase_all")) {
				status.controlClearScreen();
			}else if(seq.contentEquals("erase_to_end_line")) {
				status.eraseLineToEnd();
			}else if(seq.contentEquals("erase_to_begin_line")) {
				status.eraseLineFromBegin();
			}else if(seq.contentEquals("erase_all_line")) {
				status.eraseLine();
			}else if(seq.contentEquals("insert_char")) {
				status.controlInsertCharacter();
			}else if(seq.contentEquals("delete_char")) {
				status.controlDeleteCharacter();
			}else if(seq.contentEquals("insert_line")) {
				status.controlInsertLine();
			}else if(seq.contentEquals("delete_line")) {
				status.controlDeleteLine();
			}
			
			if(status.getCur_x()<0)status.setCur_x(0);
			if(status.getCur_y()<0)status.setCur_y(0);
			if(status.getCur_y()>=status.getText().length)status.setCur_y(status.getText().length-1);
			if(status.getCur_x()>=status.getText()[status.getCur_y()].length)status.setCur_x(status.getText()[status.getCur_y()].length-1);
			
	}
	
	// checks and executes the current sequence
	private boolean ansi_run() {
		boolean possible=false;
		for(int i=0;i<ansiCodes.length;i++) {
			if(ansiCodes[i][0].contentEquals(ansi_current) || ansiCodes[i][0].contentEquals(ansi_current_pattern)) {
				ansi_apply_sequence(ansiCodes[i][1]);
				ansi_parsing=false;
				ansi_current="";
				ansi_current_pattern="";
				return true;
			}else if(ansiCodes[i][0].startsWith(ansi_current_pattern) || ansiCodes[i][0].startsWith(ansi_current))
				possible=true;
		}
		
		return possible;
	}

	@Override
	public int processCharacter(int data) {
		if(ansi_parsing) {
			ansi_current+=(char)data;
			if(data>='0' && data<='9')ansi_current_pattern+="#";
			else ansi_current_pattern+=(char)data;
			
			if(!ansi_run()) {
				status.sim.writeToCurrentLog(String.format("Unknown terminal escape sequence: [%s]",ansi_current));
				
				/*for(int i=0;i<ansi_current.length();i++) {
					int d=ansi_current.charAt(i);
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
				ansi_parsing=false;
				ansi_current="";
				ansi_current_pattern="";
			}
			return 1;
		}

		if(data==27) {
			ansi_current="";
			ansi_current_pattern="";
			ansi_parsing=true;
			return 1;
		}
		
		return 0;
	}

}
