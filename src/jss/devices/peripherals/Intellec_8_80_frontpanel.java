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
import jss.devices.cpu.impl.Intel8080;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class Intellec_8_80_frontpanel implements GenericControlDevice, GenericExecutionDevice, GenericDataAccessDevice, GenericConnectionBus {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	BufferedImage imgSwOn;
	BufferedImage imgSwOff;
	BufferedImage imgLedOn;
	BufferedImage imgLedOff;
	
	DataBus memoryBus;
	DataBus ioBus;
	ControlBus controlBus;
	
	boolean needsUpdate;
	
	long address;
	long data;
	
	long saved_address;
	long saved_pass;
	boolean dosearch;
	int pass_count;
	
	boolean flag_reset;
	boolean flag_halt;
	boolean flag_intreq;
	boolean flag_clear_int;
	boolean flag_search_complete;
	
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
			if(key.contentEquals("Load")) {
				synchronized(lock) {
					saved_address=
							(switches.get("A15").on?1:0)<<15 |
							(switches.get("A14").on?1:0)<<14 |
							(switches.get("A13").on?1:0)<<13 |
							(switches.get("A12").on?1:0)<<12 |
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
					
					/*saved_pass=
							(switches.get("Pass3").on?1:0)<<3 |
							(switches.get("Pass2").on?1:0)<<2 |
							(switches.get("Pass1").on?1:0)<<1 |
							(switches.get("Pass0").on?1:0) ;*/
					
					/*if(saved_pass!=0 && !switches.get("MemAccess").on) {
						dosearch=true;
					}else dosearch=false;*/
					
					if(switches.get("MemAccess").on) {
						address=saved_address;
						try {
							data=memoryBus.read(saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				new ThreadResetSwitch("Load").start();
			}else if(key.contentEquals("LoadPass")) {
				synchronized(lock) {
					saved_pass=
							(switches.get("A3" ).on?1:0)<< 3 |
							(switches.get("A2" ).on?1:0)<< 2 |
							(switches.get("A1" ).on?1:0)<< 1 |
							(switches.get("A0" ).on?1:0) ;
				}
				new ThreadResetSwitch("LoadPass").start();
			}else if(key.contentEquals("Reset")) {
				synchronized(lock) {flag_reset=true;flag_intreq=true;}
				new ThreadResetSwitch("Reset").start();
			}else if(key.contentEquals("Dep")) {
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
						memoryBus.write(saved_address, data);
					} catch (MemoryAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				new ThreadResetSwitch("Dep").start();
			}else if(key.contentEquals("Incr")) {
				synchronized(lock) {
					saved_address++;
					if(saved_address>0xFFFF)saved_address=0;
					if(switches.get("MemAccess").on) {
						address=saved_address;
						try {
							data=memoryBus.read(saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					needsUpdate=true;
				}
				new ThreadResetSwitch("Incr").start();
				
			}else if(key.contentEquals("Decr")) {
				synchronized(lock) {
					saved_address--;
					if(saved_address<0)saved_address=0xFFFF;
					if(switches.get("MemAccess").on) {
						address=saved_address;
						try {
							data=memoryBus.read(saved_address);
						} catch (MemoryAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					needsUpdate=true;
				}
				new ThreadResetSwitch("Decr").start();
			}else if(key.contentEquals("Single")) {
				synchronized(lock) { do_step=true;}
				new ThreadResetSwitch("Single").start();
			}else if(key.contentEquals("SearchWait")) {
				synchronized(lock) { 
					if(switches.get("SearchWait").on) {
						dosearch=true;
						flag_search_complete=false;
						pass_count=0;
					}else {
						dosearch=false;
						flag_search_complete=false;
					}
				}
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
			super("Intellec 8/80 FrontPanel");
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
			boolean cflag_halt;
			boolean cflag_intreq;
			boolean cflag_search_complete;
			synchronized(lock) {
				caddress=address;
				cdata=data;
				cflag_halt=flag_halt;
				cflag_intreq=flag_intreq;
				cflag_search_complete=flag_search_complete;
			}
			
			for(Switch s:switches.values()) {
				g2.drawImage(s.on?s.swOn:s.swOff,s.x,s.y,null);
			}
			
			// Status
			g2.drawImage((switches.get("Wait").on || flag_halt)?(imgLedOff):(imgLedOn),315,280,null);
			g2.drawImage((switches.get("Wait").on==false)?(imgLedOff):(imgLedOn),400,280,null);
			g2.drawImage((!cflag_halt)?(imgLedOff):(imgLedOn),485,280,null);
			g2.drawImage((!cflag_halt && !switches.get("Wait").on)?(imgLedOff):(imgLedOn),570,280,null);
			g2.drawImage((!cflag_search_complete)?(imgLedOff):(imgLedOn),655,280,null);
			// - Access Req
			g2.drawImage((!cflag_intreq)?(imgLedOff):(imgLedOn),825,280,null);
			// - Int Disable
			
			// Cycle
			// - Fetch
			// - Mem
			// - I/O
			// - DA
			// - READ/INPUT
			// - WRITE/OUTPUT
			// - INT
			// - STACK
			
			// address
			g2.drawImage(((caddress&0x8000)==0)?(imgLedOff):(imgLedOn),315,405,null);
			g2.drawImage(((caddress&0x4000)==0)?(imgLedOff):(imgLedOn),400,405,null);
			g2.drawImage(((caddress&0x2000)==0)?(imgLedOff):(imgLedOn),485,405,null);
			g2.drawImage(((caddress&0x1000)==0)?(imgLedOff):(imgLedOn),570,405,null);
			g2.drawImage(((caddress&0x0800)==0)?(imgLedOff):(imgLedOn),655,405,null);
			g2.drawImage(((caddress&0x0400)==0)?(imgLedOff):(imgLedOn),740,405,null);
			g2.drawImage(((caddress&0x0200)==0)?(imgLedOff):(imgLedOn),825,405,null);
			g2.drawImage(((caddress&0x0100)==0)?(imgLedOff):(imgLedOn),910,405,null);
			
			g2.drawImage(((caddress&0x0080)==0)?(imgLedOff):(imgLedOn),1080,405,null);
			g2.drawImage(((caddress&0x0040)==0)?(imgLedOff):(imgLedOn),1165,405,null);
			g2.drawImage(((caddress&0x0020)==0)?(imgLedOff):(imgLedOn),1250,405,null);
			g2.drawImage(((caddress&0x0010)==0)?(imgLedOff):(imgLedOn),1335,405,null);
			g2.drawImage(((caddress&0x0008)==0)?(imgLedOff):(imgLedOn),1420,405,null);
			g2.drawImage(((caddress&0x0004)==0)?(imgLedOff):(imgLedOn),1505,405,null);
			g2.drawImage(((caddress&0x0002)==0)?(imgLedOff):(imgLedOn),1590,405,null);
			g2.drawImage(((caddress&0x0001)==0)?(imgLedOff):(imgLedOn),1675,405,null);
			
			// Instruction/Data
			g2.drawImage(((cdata&0x80)==0)?(imgLedOff):(imgLedOn),315,530,null);
			g2.drawImage(((cdata&0x40)==0)?(imgLedOff):(imgLedOn),400,530,null);
			g2.drawImage(((cdata&0x20)==0)?(imgLedOff):(imgLedOn),485,530,null);
			g2.drawImage(((cdata&0x10)==0)?(imgLedOff):(imgLedOn),570,530,null);
			g2.drawImage(((cdata&0x08)==0)?(imgLedOff):(imgLedOn),655,530,null);
			g2.drawImage(((cdata&0x04)==0)?(imgLedOff):(imgLedOn),740,530,null);
			g2.drawImage(((cdata&0x02)==0)?(imgLedOff):(imgLedOn),825,530,null);
			g2.drawImage(((cdata&0x01)==0)?(imgLedOff):(imgLedOn),910,530,null);
			
			// Register/Flag data
			// -  7-0
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		
		imgSwOff = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/sw_off.png"));
		imgSwOn = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/sw_on.png"));
		imgLedOff = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/led_off.png"));
		imgLedOn = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/led_on.png"));
		imgFrontPanel = ImageIO.read(getClass().getResource("/res/Intellec_8_frontpanel/frontpanel2.png"));
		
		lock=new Object();
		
		needsUpdate=false;
		
		switches=new HashMap<>(100);
		// Address/Data
		switches.put("A15", new Switch( 300,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A14", new Switch( 385,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A13", new Switch( 470,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A12", new Switch( 555,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A11", new Switch( 640,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A10", new Switch( 725,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A9",  new Switch( 810,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A8",  new Switch( 895,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A7",  new Switch(1065,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A6",  new Switch(1150,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A5",  new Switch(1235,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A4",  new Switch(1320,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A3",  new Switch(1405,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A2",  new Switch(1490,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A1",  new Switch(1575,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("A0",  new Switch(1660,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Address control
		switches.put("LoadPass", new Switch(300,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Decr",     new Switch(385,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Incr",     new Switch(470,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Load",     new Switch(555,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));

		// Mode
		switches.put("Sense",      new Switch(725,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("IOAccess",   new Switch(810,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("MemAccess",  new Switch(895,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("SearchWait", new Switch(980,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Wait",       new Switch(1065,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Control
		switches.put("Single", new Switch(1320,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Dep",    new Switch(1405,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("DepHlt", new Switch(1490,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Int",    new Switch(1575,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("Reset",  new Switch(1660,905,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// PROM PWR
		switches.put("PromPwr", new Switch(2085,695,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		/*// Stop / Single step
		switches.put("Stop", new Switch(982,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// CMA Enable/Write
		switches.put("CMAWrite", new Switch(1300,700,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		
		// Reset control
		switches.put("Reset", new Switch(1421,698,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		switches.put("ResetMode", new Switch(1485,698,imgSwOn.getWidth(),imgSwOn.getHeight(),false,imgSwOn,imgSwOff));
		*/
		flag_reset=false;
		
		win=new FrontWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		memoryBus=null;
		ioBus=null;
		controlBus=null;
		address=0;
		data=0;
		dosearch=false;
		saved_address=0;
		saved_pass=0;
		flag_halt=false;
		flag_intreq=false;
		flag_clear_int=false;
		flag_search_complete=false;
		pass_count=0;
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		if(memoryBus==null)memoryBus=bus;
		else ioBus=bus;
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		//DataBusNoError mbus=(DataBusNoError)memoryBus;
		synchronized(lock) {
			if(flag_clear_int) {
				controlBus.clearSignal("INT");
				this.flag_intreq=false;
			}
			
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
				//controlBus.setSignalData("INT", new byte[] {0x05});
				controlBus.setSignalData("INT", new byte[] {(byte)0xC3,0x00,0x38}); // JMP 3800

				flag_clear_int=true;
				this.flag_intreq=true;
			}
			
			boolean getAddressData=!switches.get("MemAccess").on;
			if(switches.get("Wait").on || switches.get("SearchWait").on && flag_search_complete) {
				if(!do_step) {
					sim.setSkipClock(cpu);
					getAddressData=false;
				}
				else do_step=false;
			}
			if(getAddressData) {
				address=cpu.getLast_pc();
				data=cpu.getInstr();
				
				if(dosearch) {
					if(address==saved_address)pass_count++;
					if(pass_count>=saved_pass)flag_search_complete=true;
				}
			}
			
			flag_halt=cpu.isFlag_halt();
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
