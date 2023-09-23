package jss.devices.display.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.display.GenericDisplayDevice;
import jss.simulation.Simulation;

public class IBM5151 implements GenericDisplayDevice {
	BufferedImage imgMonitor;
	boolean needsUpdate;
	Object lock;
	BufferedImage currentImage;

	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("IBM 5151 Display");
			setSize(1600,1143);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setVisible(true);
			
			Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	boolean doit=false;
                	synchronized(lock) {
                		if(needsUpdate) {doit=true; needsUpdate=false;}
                	}
                	if(doit)repaint();
                }
            });	
			timer.start();
			
		}
		
		@Override
		public void paint(Graphics g) {
			ColorModel cm = imgMonitor.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = imgMonitor.copyData(null);
			BufferedImage back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			Graphics2D g2=(Graphics2D)back.getGraphics();
			
			int w=1000;
			int h=660;
			Image imgc=null;
			
			synchronized(lock) {
				if(currentImage!=null)
					imgc=currentImage.getScaledInstance(w,h,Image.SCALE_SMOOTH);
			}
			
			if(imgc!=null)
				g2.drawImage(imgc,210,250,null);
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		imgMonitor = ImageIO.read(getClass().getResource("/res/5151Monitor/5151.png"));
		lock=new Object();
		needsUpdate=false;
		currentImage=null;
		win=new FrontWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		currentImage=null;
	}

	@Override
	public void display(BufferedImage img) {
		synchronized(lock) {
			currentImage=img;
			needsUpdate=true;
		}
	}

}
