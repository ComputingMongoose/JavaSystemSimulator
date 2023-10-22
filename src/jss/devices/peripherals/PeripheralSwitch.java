package jss.devices.peripherals;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class PeripheralSwitch {
	public int x,y,w,h;
	public boolean on;
	public Image swOn,swOff;
	public ActionListener actionListener;
	
	public PeripheralSwitch(int x, int y, int w, int h, boolean on, BufferedImage swOn, BufferedImage swOff){
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
		this.on=on;
		
		if(swOn==null || swOn.getWidth()==w && swOn.getHeight()==h) {
			this.swOn=swOn;
		}else {
			this.swOn=swOn.getScaledInstance(w, h,Image.SCALE_SMOOTH);
		}
		
		if(swOff==null || swOff.getWidth()==w && swOff.getHeight()==h) {
			this.swOff=swOff;
		}else {
			this.swOff=swOff.getScaledInstance(w, h,Image.SCALE_SMOOTH);
		}
	}
	
	public void setActionListener(ActionListener aListener) {
		this.actionListener=aListener;
	}

}
