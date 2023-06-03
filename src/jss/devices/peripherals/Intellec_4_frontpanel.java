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
import jss.devices.GenericDataAccessDevice;
import jss.devices.GenericDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBus;
import jss.devices.bus.GenericConnectionBus;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.impl.Intel4004;
import jss.devices.cpu.impl.Intel4040;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class Intellec_4_frontpanel implements GenericControlDevice, GenericExecutionDevice, GenericDataAccessDevice, GenericConnectionBus {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	BufferedImage imgSwOn;
	BufferedImage imgSwOff;
	BufferedImage imgLedOn;
	BufferedImage imgLedOff;
	
	DataBus memoryBus;
	ControlBus controlBus;
	
	boolean needsUpdate;
	
	long address;
	long data;
	long cmram;
	long mode;
	long set_mode;
	int pointer_valid;
	long src_pointer;
	long x2;
	long x3;
	
	long saved_address;
	long saved_pass;
	boolean dosearch;
	
	boolean flag_reset;
	
	boolean do_step;
	
	Object lock;
	
	HashMap<String,Switch> switches;
	
	Intel4004 cpu;
	
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
			if(key.contentEquals("ModeMon")) {
				synchronized(lock) {set_mode=Intel4040.MODE_ROM;}
				new ThreadResetSwitch("ModeMon").start();
			}else if(key.contentEquals("ModeRam")) {
				synchronized(lock) {set_mode=Intel4040.MODE_RAM;}
				new ThreadResetSwitch("ModeRam").start();
			}else if(key.contentEquals("ModeProm")) {
				synchronized(lock) {set_mode=Intel4040.MODE_PROM;}
				new ThreadResetSwitch("ModeProm").start();
			}else if(key.contentEquals("Load")) {
				synchronized(lock) {
					saved_address=
							(switches.get("A11").on?1:0)<<11 |
							(switches.get("A10").on?1:0)<<10 |
							(switches.get("A9" ).on?1:0)<< 9 |
							(switches.get("A8" ).on?1:0)<< 8 |
							(switches.get("A7" ).on?1:0)<< 7 |
							(switches.get("A6" ).on?1:0)<< 6 |
							(switches.get("A5" ).on?1:0)<< 5 |
							(switches.get("A4" ).on?1:0)<< 4 |
							(switches.get("A3" ).on?1:0)<< 3 |
							(switches.get("A2" ).on?1:0)<< 2 |
							(switches.get("A1" ).on?1:0)<< 1 |
							(switches.get("A0" ).on?1:0) ;
					
					saved_pass=
							(switches.get("Pass3").on?1:0)<<3 |
							(switches.get("Pass2").on?1:0)<<2 |
							(switches.get("Pass1").on?1:0)<<1 |
							(switches.get("Pass0").on?1:0) ;
					
					if(saved_pass!=0 && !switches.get("CMAEnable").on) {
						dosearch=true;
					}else dosearch=false;
					
					if(switches.get("CMAEnable").on) {
						address=saved_address;
						try {
							data=memoryBus.read(mode|saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				new ThreadResetSwitch("Load").start();
			}else if(key.contentEquals("Reset")) {
				synchronized(lock) {flag_reset=true;}
				new ThreadResetSwitch("Reset").start();
			}else if(key.contentEquals("CMAWrite")) {
				synchronized(lock) {
					address=saved_address;
					data=
							(switches.get("A7" ).on?1:0)<< 7 |
							(switches.get("A6" ).on?1:0)<< 6 |
							(switches.get("A5" ).on?1:0)<< 5 |
							(switches.get("A4" ).on?1:0)<< 4 |
							(switches.get("A3" ).on?1:0)<< 3 |
							(switches.get("A2" ).on?1:0)<< 2 |
							(switches.get("A1" ).on?1:0)<< 1 |
							(switches.get("A0" ).on?1:0) ;
					try {
						memoryBus.write(Intel4040.MODE_RAM|saved_address, data);
					} catch (MemoryAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				new ThreadResetSwitch("CMAWrite").start();
			}else if(key.contentEquals("Incr")) {
				synchronized(lock) {
					saved_address++;
					if(saved_address>0xFFF)saved_address=0;
					if(switches.get("CMAEnable").on) {
						address=saved_address;
						try {
							data=memoryBus.read(Intel4040.MODE_RAM|saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				new ThreadResetSwitch("Incr").start();
				
			}else if(key.contentEquals("Decr")) {
				synchronized(lock) {
					saved_address--;
					if(saved_address<0)saved_address=0xFFF;
					if(switches.get("CMAEnable").on) {
						address=saved_address;
						try {
							data=memoryBus.read(Intel4040.MODE_RAM|saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				new ThreadResetSwitch("Decr").start();
			}else if(key.contentEquals("Single")) {
				synchronized(lock) { do_step=true;}
				new ThreadResetSwitch("Single").start();
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
			super("Intellec 4 FrontPanel");
			setSize(1800,763);
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
			
			long caddress=0;
			long cdata=0;
			long ccmram=0;
			long cmode=0;
			int cpointer_valid=0;
			long csrc=0;
			long cx2=0;
			long cx3=0;
			synchronized(lock) {
				caddress=address;
				cdata=data;
				ccmram=cmram;
				cmode=mode;
				cpointer_valid=pointer_valid;
				csrc=src_pointer;
				cx2=x2;
				cx3=x3;
			}
			
			for(Switch s:switches.values()) {
				g2.drawImage(s.on?s.swOn:s.swOff,s.x,s.y,null);
			}
			
			// address
			BufferedImage imgLed=imgLedOff;
			g2.drawImage(((caddress&0x800)==0)?(imgLedOff):(imgLedOn),237,207,null);
			g2.drawImage(((caddress&0x400)==0)?(imgLedOff):(imgLedOn),298,207,null);
			g2.drawImage(((caddress&0x200)==0)?(imgLedOff):(imgLedOn),360,207,null);
			g2.drawImage(((caddress&0x100)==0)?(imgLedOff):(imgLedOn),423,207,null);
			
			g2.drawImage(((caddress&0x080)==0)?(imgLedOff):(imgLedOn),550,204,null);
			g2.drawImage(((caddress&0x040)==0)?(imgLedOff):(imgLedOn),610,204,null);
			g2.drawImage(((caddress&0x020)==0)?(imgLedOff):(imgLedOn),675,204,null);
			g2.drawImage(((caddress&0x010)==0)?(imgLedOff):(imgLedOn),735,204,null);

			g2.drawImage(((caddress&0x008)==0)?(imgLedOff):(imgLedOn),865,204,null);
			g2.drawImage(((caddress&0x004)==0)?(imgLedOff):(imgLedOn),930,204,null);
			g2.drawImage(((caddress&0x002)==0)?(imgLedOff):(imgLedOn),990,204,null);
			g2.drawImage(((caddress&0x001)==0)?(imgLedOff):(imgLedOn),1055,204,null);
			
			// Status
			g2.drawImage(imgLed,1245,204,null);
			g2.drawImage(imgLed,1310,204,null);
			g2.drawImage((cpointer_valid==1)?(imgLedOn):(imgLedOff),1370,204,null);
			
			
			// Instruction
			g2.drawImage(((cdata&0x80)==0)?(imgLedOff):(imgLedOn),237,312,null);
			g2.drawImage(((cdata&0x40)==0)?(imgLedOff):(imgLedOn),298,312,null);
			g2.drawImage(((cdata&0x20)==0)?(imgLedOff):(imgLedOn),360,312,null);
			g2.drawImage(((cdata&0x10)==0)?(imgLedOff):(imgLedOn),423,312,null);
			
			g2.drawImage(((cdata&0x08)==0)?(imgLedOff):(imgLedOn),550,309,null);
			g2.drawImage(((cdata&0x04)==0)?(imgLedOff):(imgLedOn),610,309,null);
			g2.drawImage(((cdata&0x02)==0)?(imgLedOff):(imgLedOn),675,309,null);
			g2.drawImage(((cdata&0x01)==0)?(imgLedOff):(imgLedOn),735,309,null);

			// Active Bank / CM-RAM
			g2.drawImage(((ccmram&0x08)==0)?(imgLedOff):(imgLedOn),865,309,null);
			g2.drawImage(((ccmram&0x04)==0)?(imgLedOff):(imgLedOn),930,309,null);
			g2.drawImage(((ccmram&0x02)==0)?(imgLedOff):(imgLedOn),990,309,null);
			g2.drawImage(((ccmram&0x01)==0)?(imgLedOff):(imgLedOn),1055,309,null);
			
			// Mode
			g2.drawImage((cmode==Intel4040.MODE_ROM)?(imgLedOn):(imgLedOff),1245,307,null);
			g2.drawImage((cmode==Intel4040.MODE_RAM)?(imgLedOn):(imgLedOff),1310,307,null);
			g2.drawImage((cmode==Intel4040.MODE_PROM)?(imgLedOn):(imgLedOff),1370,307,null);

			// Execution
			g2.drawImage(((cx2&0x08)==0)?(imgLedOff):(imgLedOn),237,407,null);
			g2.drawImage(((cx2&0x04)==0)?(imgLedOff):(imgLedOn),298,407,null);
			g2.drawImage(((cx2&0x02)==0)?(imgLedOff):(imgLedOn),360,407,null);
			g2.drawImage(((cx2&0x01)==0)?(imgLedOff):(imgLedOn),423,407,null);
			
			g2.drawImage(((cx3&0x08)==0)?(imgLedOff):(imgLedOn),550,404,null);
			g2.drawImage(((cx3&0x04)==0)?(imgLedOff):(imgLedOn),610,404,null);
			g2.drawImage(((cx3&0x02)==0)?(imgLedOff):(imgLedOn),675,404,null);
			g2.drawImage(((cx3&0x01)==0)?(imgLedOff):(imgLedOn),735,404,null);

			// Last RAM/ROM Pointer
			g2.drawImage(((csrc&0x80)==0)?(imgLedOff):(imgLedOn),865,404,null);
			g2.drawImage(((csrc&0x40)==0)?(imgLedOff):(imgLedOn),930,404,null);
			g2.drawImage(((csrc&0x20)==0)?(imgLedOff):(imgLedOn),990,404,null);
			g2.drawImage(((csrc&0x10)==0)?(imgLedOff):(imgLedOn),1055,404,null);
			
			g2.drawImage(((csrc&0x08)==0)?(imgLedOff):(imgLedOn),1180,404,null);
			g2.drawImage(((csrc&0x04)==0)?(imgLedOff):(imgLedOn),1245,404,null);
			g2.drawImage(((csrc&0x02)==0)?(imgLedOff):(imgLedOn),1310,404,null);
			g2.drawImage(((csrc&0x01)==0)?(imgLedOff):(imgLedOn),1370,404,null);
			
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		
		imgSwOff = ImageIO.read(getClass().getResource("/res/Intellec_4_frontpanel/sw_off.png"));
		imgSwOn = ImageIO.read(getClass().getResource("/res/Intellec_4_frontpanel/sw_on.png"));
		imgLedOff = ImageIO.read(getClass().getResource("/res/Intellec_4_frontpanel/led_off.png"));
		imgLedOn = ImageIO.read(getClass().getResource("/res/Intellec_4_frontpanel/led_on.png"));
		imgFrontPanel = ImageIO.read(getClass().getResource("/res/Intellec_4_frontpanel/frontpanel2.png"));
		
		lock=new Object();
		
		needsUpdate=false;
		
		switches=new HashMap<>(100);
		// Address/Data
		switches.put("A11", new Switch(225,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A10", new Switch(290,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A9", new Switch(354,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A8", new Switch(415,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A7", new Switch(540,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A6", new Switch(601,534,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A5", new Switch(668,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A4", new Switch(731,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A3", new Switch(857,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A2", new Switch(919,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A1", new Switch(982,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A0", new Switch(1041,530,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Mode control
		switches.put("ModeMon", new Switch(1236,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("ModeRam", new Switch(1300,532,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("ModeProm", new Switch(1365,530,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// PROM PWR
		switches.put("PromPwr", new Switch(1546,530,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Pass counter
		switches.put("Pass3", new Switch(225,702,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Pass2", new Switch(290,702,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Pass1", new Switch(354,702,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Pass0", new Switch(415,702,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Search address control
		switches.put("Run", new Switch(540,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Next", new Switch(601,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Decr", new Switch(668,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Incr", new Switch(731,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Load", new Switch(794,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));

		// Stop / Single step
		switches.put("Stop", new Switch(982,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Single", new Switch(1041,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// CMA Enable/Write
		switches.put("CMAEnable", new Switch(1236,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("CMAWrite", new Switch(1300,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Reset control
		switches.put("Reset", new Switch(1421,698,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("ResetMode", new Switch(1485,698,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		flag_reset=false;
		
		win=new FrontWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		memoryBus=null;
		controlBus=null;
		address=0;
		data=0;
		set_mode=0;
		dosearch=false;
		saved_address=0;
		saved_pass=0;
		
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		if(memoryBus==null)memoryBus=bus;

	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		//DataBusNoError mbus=(DataBusNoError)memoryBus;
		synchronized(lock) {
			if(set_mode!=0) {
				cpu.setMode(set_mode);
				set_mode=0;
			}

			if(!switches.get("CMAEnable").on) {
				address=cpu.getLast_pc();
				data=(cpu.getOpr() << 4) | cpu.getOpa();
			}
			cmram=cpu.getCMRAM_decoded();
			mode=cpu.getMode();
			src_pointer=cpu.getReg_src();
			pointer_valid=cpu.getReg_src_valid();
			x2=cpu.getX2();
			x3=cpu.getX3();
			
			if(flag_reset) {
				flag_reset=false;
				try {
					cpu.initialize();
				} catch (DeviceConfigurationException | ConfigurationValueTypeException
						| ConfigurationValueOptionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!switches.get("ResetMode").on) {
					cpu.setMode(this.mode);
				}
			}
			
			if(switches.get("Stop").on) {
				if(!do_step)sim.setSkipClock(cpu);
				else do_step=false;
			}
		}
		synchronized(lock) {needsUpdate=true;}
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attachGenericDevice(GenericDevice device) {
		cpu=(Intel4004)device;
	}

}
