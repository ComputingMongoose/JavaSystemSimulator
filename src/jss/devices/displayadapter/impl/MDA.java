package jss.devices.displayadapter.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBusDevice;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.display.GenericDisplayDevice;
import jss.devices.displayadapter.GenericDisplayAdapter;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class MDA implements GenericDisplayAdapter, GenericExecutionDevice {
	GenericDisplayDevice display;
	GenericDataDevice videoRam;
	long videoRamStart,videoRamEnd,videoRamOffset;
	
	BufferedImage img;
	BufferedImage imgFont_green;
	
	Object lock=new Object();
	boolean wasStep;
	
	class VideoThread extends Thread {
		public void run() {
			
			BufferedImage[] characters_green=new BufferedImage[256];
			
			for(int i=0;i<8;i++) {
				for(int j=0;j<32;j++) {
					characters_green[i*32+j]=imgFont_green.getSubimage(j*9, i*14, 9, 14);
				}
			}
			
			Graphics2D g=img.createGraphics();

			try {
				while(true) {
					boolean proceed=false;
					synchronized(lock) {
						if(wasStep) {
							wasStep=false;
							proceed=true;
						}
					}
					if(!proceed) {
						Thread.sleep(10);
						continue;
					}
					
					
					for(int row=0;row<25;row++) {
						for(int col=0;col<80;col++) {
							int chr=(int)videoRam.read(row*80*2+col*2);
							int attr=(int)videoRam.read(row*80*2+col*2+1);
							
							g.drawImage(characters_green[chr], col*9, row*14, null);
						}
					}
					
					display.display(img);
					Thread.sleep(20);
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public long read(long address) throws MemoryAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		display=null;
		videoRam=null;
		wasStep=false;
		img=new BufferedImage(720,350,BufferedImage.TYPE_INT_ARGB);
		
		imgFont_green = ImageIO.read(getClass().getResource("/res/MDA/mda9_green.png"));
		
		VideoThread vt=new VideoThread();
		vt.start();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {

		if(videoRam!=null)videoRam.initialize();
	}

	@Override
	public void attachDataDevice(GenericDataDevice device, long start, long end, long offset,String name,boolean enabled) {
		videoRam=device;
		videoRamStart=start;
		videoRamEnd=end;
		videoRamOffset=offset;
	}

	@Override
	public void attachDisplayDevice(GenericDisplayDevice disp) {
		this.display=disp;
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		synchronized(lock) {
			this.wasStep=true;
		}
	}

	@Override
	public DataBusDevice getDeviceByConnectionName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
