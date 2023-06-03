package jss.devices.display;

import java.awt.image.BufferedImage;

import jss.devices.GenericDevice;

public interface GenericDisplayDevice extends GenericDevice {
	public void display(BufferedImage img);
}
