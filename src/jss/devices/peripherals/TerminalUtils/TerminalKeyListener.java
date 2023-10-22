package jss.devices.peripherals.TerminalUtils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TerminalKeyListener implements KeyListener {

	protected TerminalStatus status;
	
	public TerminalKeyListener(TerminalStatus status) {
		this.status=status;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {;}

	@Override
	public void keyReleased(KeyEvent arg0) {;}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if(status.isKbLock())return ;
		
		char c=arg0.getKeyChar();
		
		if(c==10)c=13; // do we still need this ?
		
		status.transmitChar(c);
	}

}
