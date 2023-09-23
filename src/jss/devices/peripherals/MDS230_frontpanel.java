package jss.devices.peripherals;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.GenericConnectionBus;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.impl.Intel8080;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class MDS230_frontpanel implements GenericControlDevice, GenericExecutionDevice, GenericConnectionBus {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	//BufferedImage imgSwOn;
	//BufferedImage imgSwOff;
	//BufferedImage imgLedOn;
	//BufferedImage imgLedOff;
	
	ControlBus controlBus;
	
	boolean needsUpdate;
	
	boolean flag_reset;
	
	boolean do_step;
	
	Object lock;
	
	HashMap<String,Switch> switches;
	
	Intel8080 cpu;
	
	class Switch {
		int x,y,w,h;
		boolean on;
		BufferedImage swOn,swOff;
		
		Switch(int x, int y, int w, int h, boolean on, BufferedImage swOn, BufferedImage swOff){
			this.x=x;
			this.y=y;
			this.w=w;
			this.h=h;
			this.on=on;
			this.swOn=swOn;
			this.swOff=swOff;
		}
	}
	
	class ThreadResetSwitch extends Thread {
		private String swName;
		
		public ThreadResetSwitch(String swName) {
			this.swName=swName;
		}
		
		public void run() {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switches.get(swName).on=false;
			synchronized(lock) {needsUpdate=true;}
		}
	}
	
	class FrontWindowMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			int x=arg0.getX() * imgFrontPanel.getWidth()/win.getWidth();
			int y=arg0.getY() * imgFrontPanel.getHeight()/win.getHeight();
			
			for(Entry <String,Switch> e:switches.entrySet()) {
				Switch s=e.getValue();
				String key=e.getKey();
				if(x>=s.x && x<=s.x+s.w && y>=s.y && y<=s.y+s.h) {
					s.on=!s.on;
					switchClicked(key,s);
					synchronized(lock) {needsUpdate=true;}
					break;
				}
			}
		}
		
		public void switchClicked(String key, Switch sw) {
			if(key.contentEquals("Reset")) {
				synchronized(lock) {flag_reset=true;}
				new ThreadResetSwitch("Reset").start();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("Intellec Series II FrontPanel");
			setSize(1500,390);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			this.addMouseListener(new FrontWindowMouseListener());
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
			ColorModel cm = imgFrontPanel.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = imgFrontPanel.copyData(null);
			BufferedImage back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			Graphics2D g2=(Graphics2D)back.getGraphics();
			
			/*boolean cflag_intreq;
			synchronized(lock) {
				cflag_intreq=flag_intreq;
			}*/
			
			for(Switch s:switches.values()) {
				if(s.swOn!=null)g2.drawImage(s.on?s.swOn:s.swOff,s.x,s.y,null);
			}
			
			// Status
			//g2.drawImage((!cflag_intreq)?(imgLedOff):(imgLedOn),825,280,null);
			
			Image img=back.getScaledInstance(this.getWidth()-this.getInsets().left-this.getInsets().right, 
					this.getHeight()-this.getInsets().top-this.getInsets().bottom, Image.SCALE_SMOOTH);
			g.drawImage(img,this.getInsets().left,this.getInsets().top,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		
		//imgSwOff = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/sw_off.png"));
		//imgSwOn = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/sw_on.png"));
		//imgLedOff = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/led_off.png"));
		//imgLedOn = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/led_on.png"));
		imgFrontPanel = ImageIO.read(getClass().getResource("/res/MDS230_frontpanel/frontpanel.jpg"));
		
		lock=new Object();
		
		needsUpdate=false;
		
		switches=new HashMap<>(100);
		switches.put("Reset", new Switch(915,19,57,84,false,null,null));

		flag_reset=false;
		
		win=new FrontWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		controlBus=null;
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		//DataBusNoError mbus=(DataBusNoError)memoryBus;
		synchronized(lock) {
			if(flag_reset) {
				flag_reset=false;
				try {
					cpu.initialize();
				} catch (DeviceConfigurationException | ConfigurationValueTypeException
						| ConfigurationValueOptionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				controlBus.setSignal("INT");
				controlBus.setSignalData("INT", new byte[] {(byte)0xC3,0x00,(byte)0xE8}); // JMP E800
			}
			
		}
		synchronized(lock) {needsUpdate=true;}
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		controlBus=bus;
	}

	@Override
	public void attachGenericDevice(GenericDevice device) {
		cpu=(Intel8080)device;
	}

}
