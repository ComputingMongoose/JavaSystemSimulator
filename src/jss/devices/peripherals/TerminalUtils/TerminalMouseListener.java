package jss.devices.peripherals.TerminalUtils;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map.Entry;

import jss.devices.peripherals.PeripheralSwitch;

public class TerminalMouseListener implements MouseListener {
	TerminalStatus status;
	
	public TerminalMouseListener(TerminalStatus status) {
		this.status=status;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		Insets in=status.getWin().getInsets();
		
		int x=(arg0.getX()-in.left) * status.getBaseWidth()/(status.getWin().getWidth()-in.left-in.right);
		int y=(arg0.getY()-in.top) * status.getBaseHeight()/(status.getWin().getHeight()-in.top-in.bottom);
		
		for(Entry <String,PeripheralSwitch> e:status.getSwitches().entrySet()) {
			PeripheralSwitch s=e.getValue();
			String key=e.getKey();
			if(x>=s.x && x<=s.x+s.w && y>=s.y && y<=s.y+s.h) {
				if(s.actionListener!=null) {
					s.actionListener.actionPerformed(new ActionEvent(s,0,key));
				}else {
					//s.on=!s.on;
					peripheralSwitchClicked(key,s);
					//win.repaint();
				}
				break;
			}
		}
	}
	
	public void peripheralSwitchClicked(String key, PeripheralSwitch sw) {
		if(key.length()==1)
			status.transmitChar(key.charAt(0));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {;}

	@Override
	public void mouseExited(MouseEvent arg0) {;}

	@Override
	public void mousePressed(MouseEvent arg0) {;}

	@Override
	public void mouseReleased(MouseEvent arg0) {;}
	
}
