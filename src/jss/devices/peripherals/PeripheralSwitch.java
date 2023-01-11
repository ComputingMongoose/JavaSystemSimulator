package jss.devices.peripherals;

import java.awt.image.BufferedImage;

public class PeripheralSwitch {
	int x,y,w,h;
	boolean on;
	BufferedImage swOn,swOff;
	
	PeripheralSwitch(int x, int y, int w, int h, boolean on, BufferedImage swOn, BufferedImage swOff){
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
		this.on=on;
		this.swOn=swOn;
		this.swOff=swOff;
	}

}
