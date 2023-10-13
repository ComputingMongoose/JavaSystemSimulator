package jss.disk.impl;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import jss.configuration.ConfigurationValue;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDevice;
import jss.devices.GenericMultiDevice;
import jss.devices.peripherals.PeripheralSwitch;
import jss.simulation.Simulation;

public class MDS230_x2_floppydrives implements GenericMultiDevice {
	BufferedImage imgFrontPanel;
	BufferedImage imgLoad;
	//BufferedImage imgSave;
	
	Simulation sim;
	DeviceConfiguration config;
	
	boolean debug;
	
	int enable_load_disk;
	
	boolean needsUpdate;
	
	HashMap<String,PeripheralSwitch> switches;
	
	InternalDiskDrive[] drives;
	
	class FrontWindowMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			int x=arg0.getX() * imgFrontPanel.getWidth()/win.getWidth();
			int y=arg0.getY() * imgFrontPanel.getHeight()/win.getHeight();
			
			for(Entry <String,PeripheralSwitch> e:switches.entrySet()) {
				PeripheralSwitch s=e.getValue();
				String key=e.getKey();
				if(x>=s.x && x<=s.x+s.w && y>=s.y && y<=s.y+s.h) {
					//s.on=!s.on;
					try {
						PeripheralSwitchClicked(key,s);
					} catch (IOException | DeviceConfigurationException | ConfigurationValueTypeException | ConfigurationValueOptionException e1) {
						e1.printStackTrace();
						throw new RuntimeException(e1);
					}
					//win.repaint();
					break;
				}
			}
		}
		
		public void PeripheralSwitchClicked(String key, PeripheralSwitch sw) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
			if(key.contentEquals("LOAD1")) {
				FileDialog fd=new FileDialog(win,"Load Disk in Drive 1",FileDialog.LOAD);
				fd.setVisible(true);
				File[] files=fd.getFiles();
				if(files!=null && files.length>0) {
					loadDisk(0,files[0]);
				}
			}else if(key.contentEquals("LOAD2")) {
				FileDialog fd=new FileDialog(win,"Load Disk in Drive 2",FileDialog.LOAD);
				fd.setVisible(true);
				File[] files=fd.getFiles();
				if(files!=null && files.length>0) {
					loadDisk(1,files[0]);
				}
			}
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
	
	class FrontWindowKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {;}

		@Override
		public void keyReleased(KeyEvent arg0) {;}

		@Override
		public void keyTyped(KeyEvent arg0) {;}
	}
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("Floppy Disk Drives");
			this.setLayout(null);

			setSize(1127,351);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			this.addMouseListener(new FrontWindowMouseListener());
			this.addKeyListener(new FrontWindowKeyListener());
			
			setVisible(true);
			
			/*Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	boolean doit=false;
                	synchronized(lock) {
                		if(needsUpdate) {doit=true; needsUpdate=false;}
                	}
                	if(doit)repaint();
                }
            });	
			timer.start();*/
			
		}
		
		@Override
		public void paint(Graphics g) {
			//super.paint(g);
			
			ColorModel cm = imgFrontPanel.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = imgFrontPanel.copyData(null);
			BufferedImage back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			Graphics2D g2=(Graphics2D)back.getGraphics();

			//int x=440;
			//int y=40;
			g2.setFont(new Font("Consolas",Font.BOLD,12));
			g2.setColor(Color.BLACK);
			//int height = g2.getFontMetrics().getHeight();

			if(enable_load_disk==1) {
				g2.drawImage(imgLoad.getScaledInstance(100, 100, Image.SCALE_SMOOTH), 85,100,null);
				g2.drawImage(imgLoad.getScaledInstance(100, 100, Image.SCALE_SMOOTH), 600,100,null);
			}
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
			
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException, ConfigurationValueOptionException {
		
		this.sim=sim;
		this.config=config;
		debug=config.getOptLong("debug", 0)==1;
		
		drives=new InternalDiskDrive[2];
		drives[0]=new InternalDiskDrive();
		drives[1]=new InternalDiskDrive();

		// re-use the same config => debug is already set + other values if present
		DeviceConfiguration driveCfg=config.clone();
		driveCfg.set("load_disk",new ConfigurationValue(config.getOptString("load_disk_0", "")));
		driveCfg.set("use_overlay",new ConfigurationValue(config.getOptLong("use_overlay_0", 0)));
		drives[0].configure(driveCfg, sim);
		driveCfg=config.clone();
		driveCfg.set("load_disk",new ConfigurationValue(config.getOptString("load_disk_1", "")));
		driveCfg.set("use_overlay",new ConfigurationValue(config.getOptLong("use_overlay_1", 0)));
		drives[1].configure(driveCfg, sim);
		
		imgFrontPanel = ImageIO.read(getClass().getResource("/res/MDS230_x2_floppydrives/frontpanel.jpg"));
		imgLoad = ImageIO.read(getClass().getResource("/res/common/load.png"));
		
		enable_load_disk=(int)config.getOptLong("enable_load_disk", 1);

		switches=new HashMap<>(100);
		
		if(enable_load_disk==1) {
			switches.put("LOAD1", new PeripheralSwitch(85,100,100,100,false,null,null));
			switches.put("LOAD2", new PeripheralSwitch(600,100,100,100,false,null,null));
		}
		
		needsUpdate=false;;
		
		win=new FrontWindow();
		win.repaint();
	}
	
	public void loadDisk(int driveNum, File diskFile) throws IOException, DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		drives[driveNum].loadDisk(diskFile);
	}

	@Override
	public GenericDevice getDevice(int id) {
		if(id<0 || id>1)return null;
		return drives[id];
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		drives[0].initialize();
		drives[1].initialize();
	}

	

}
