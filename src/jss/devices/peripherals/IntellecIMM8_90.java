package jss.devices.peripherals;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class IntellecIMM8_90 extends AbstractSerialDevice {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	
	boolean needsUpdate;
	
	boolean tape_active;
	String tape_in;
	BufferedReader tape_in_reader;
	
	@Override
	public long read(long address) throws MemoryAccessException {
		if(address==1) {
			int istatus=0x00;
			if(status.isTransmitDataAvailable())istatus=0x20; // data available
			
			return istatus;
			
		}else
			return super.read(address);
	}	
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("IMM8-90 Punch Tape Reader");
			
			this.setLayout(null);
			
			setSize(700,250);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			//this.addMouseListener(new FrontWindowMouseListener());
			//this.addKeyListener(new FrontWindowKeyListener());
			
			setVisible(true);
			
			Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	if(status.getAndResetNeedsUpdate())repaint();
                }
            });	
			timer.start();
		}
		
		@Override
		public void paint(Graphics g) {
			//super.paint(g);
			
			ColorModel cm = imgFrontPanel.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = imgFrontPanel.copyData(null);
			BufferedImage back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);

		imgFrontPanel = ImageIO.read(getClass().getResource("/res/Intellec_imm8_90/frontpanel.png"));
		
		needsUpdate=false;;
		
		tape_active=false;
		tape_in=config.getOptString("tape_in", "");
		if(tape_in!=null && tape_in.length()>0) {
			try {
				tape_in_reader=new BufferedReader(
						new InputStreamReader(
								new FileInputStream(sim.getFilePath(tape_in).toFile()),Charset.forName("UTF8")));
			}catch(Exception ex) {tape_in_reader=null;}
		}
		
		win=new FrontWindow();
		win.repaint();
	}

	@Override
	public void writeData(int data) throws MemoryAccessException {
		;
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
		if(uart==0) {
			if(value==0)tape_active=false;
			else {
				if(tape_in_reader!=null)tape_active=true;
			}
		}else {
			if(value==12) { // TTYGO
				tape_active=true;
				getTransmitData();
			}else if(value==8) { // TTYNO
				tape_active=false;
			}
		}
	}

	@Override
	public void getTransmitData() throws MemoryAccessException {
		if(!tape_active || tape_in_reader==null)return;
		
		try {
			status.transmitChar(tape_in_reader.read());
		}catch(IOException ex) {;}
	}

}
