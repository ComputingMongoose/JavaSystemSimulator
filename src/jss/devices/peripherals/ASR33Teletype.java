package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class ASR33Teletype extends AbstractSerialDevice {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	
	StringBuffer[] received_text;
	int received_text_pointer;
	
	boolean needsUpdate;
	
	HashMap<String,PeripheralSwitch> switches;
	
	boolean tape_active;
	String tape_in;
	BufferedReader tape_in_reader;
	
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
					PeripheralSwitchClicked(key,s);
					//win.repaint();
					break;
				}
			}
		}
		
		public void PeripheralSwitchClicked(String key, PeripheralSwitch sw) {
			if(key.contentEquals("1")) {
				synchronized(lock) {transmit.add("1");}
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
		public void keyTyped(KeyEvent arg0) {
			char c=arg0.getKeyChar();
			if(c>='a' && c<='z')c+=('A'-'a');
			
			if(
					c>='0' && c<='9' || 
					c>='A' && c<='Z' ||
					c=='!' || c=='"' || c=='#' || c=='$' || c=='%' || c=='&' ||
					c=='\'' || c=='(' || c==')' || c==' ' || c=='*' || c==':' ||
					c=='=' || c=='-' || c=='@' || c=='[' || c=='\\' || c=='+' ||
					c==']' || c=='<' || c=='>' || c==',' || c=='.' || c=='?' ||
					c=='/'
			) {
				synchronized(lock) {transmit.add(""+c);}
			}else if(c==13 || c==10) {
				synchronized(lock) {transmit.add("\r");}
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("ASR 33 Teletype");
			
			this.setLayout(null);
			
			setSize(1600,992);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			this.addMouseListener(new FrontWindowMouseListener());
			this.addKeyListener(new FrontWindowKeyListener());
			
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
			//super.paint(g);
			
			ColorModel cm = imgFrontPanel.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = imgFrontPanel.copyData(null);
			BufferedImage back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
			
			Graphics2D g2=(Graphics2D)back.getGraphics();

			int x=440;
			int y=40;
			g2.setFont(new Font("Consolas",Font.BOLD,12));
			g2.setColor(Color.BLACK);
			int height = g2.getFontMetrics().getHeight();

			for ( int i=received_text_pointer+1;i<received_text.length;i++ ) {
				String ctext=null;
				synchronized(lock) {
					ctext=received_text[i].toString();
				}
				g2.drawString( ctext, x, y + height );
				y += height;
			}			
			for ( int i=0;i<=received_text_pointer;i++ ) {
				String ctext=null;
				synchronized(lock) {
					ctext=received_text[i].toString();
				}
				g2.drawString( ctext, x, y + height );
				y += height;
			}			
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);
		}
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);

		imgFrontPanel = ImageIO.read(getClass().getResource("/res/ASR33Teletype/asr33.png"));

		received_text=new StringBuffer[14];
		for(int i=0;i<received_text.length;i++) {
			received_text[i]=new StringBuffer();
			//received_text[i].append("12345678901234567890123456789012345678901234567890123456789012345678901234");
		}
		received_text_pointer=received_text.length-1;
		
		switches=new HashMap<>(100);
		// Address/Data
		switches.put("1", new PeripheralSwitch(360,647,45,52,false,null,null));
		switches.put("2", new PeripheralSwitch(415,647,45,52,false,null,null));
		switches.put("3", new PeripheralSwitch(471,647,45,52,false,null,null));
		switches.put("4", new PeripheralSwitch(526,647,45,52,false,null,null));
		switches.put("5", new PeripheralSwitch(583,647,45,52,false,null,null));
		switches.put("6", new PeripheralSwitch(639,647,45,52,false,null,null));
		switches.put("7", new PeripheralSwitch(694,647,45,52,false,null,null));
		switches.put("8", new PeripheralSwitch(750,647,45,52,false,null,null));
		switches.put("9", new PeripheralSwitch(805,647,45,52,false,null,null));
		switches.put("0", new PeripheralSwitch(862,647,45,52,false,null,null));
		switches.put(":", new PeripheralSwitch(919,647,45,52,false,null,null));
		switches.put("_", new PeripheralSwitch(975,647,45,52,false,null,null));
		
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
		if(data==13) {
			received_text_pointer++;
			if(received_text_pointer>=received_text.length)received_text_pointer=0;
			received_text[received_text_pointer].setLength(0);
			synchronized(lock) {needsUpdate=true;}
			
		}else if(data==10) {
			;
		}else {
			char c=(char)data;
			if(
					c>='0' && c<='9' || 
					c>='A' && c<='Z' ||
					c=='!' || c=='"' || c=='#' || c=='$' || c=='%' || c=='&' ||
					c=='\'' || c=='(' || c==')' || c==' ' || c=='*' || c==':' ||
					c=='=' || c=='-' || c=='@' || c=='[' || c=='\\' || c=='+' ||
					c==']' || c=='<' || c=='>' || c==',' || c=='.' || c=='?' ||
					c=='/'
			) {
				received_text[received_text_pointer].append((char)data);
				synchronized(lock) {needsUpdate=true;}
			}
		}
		//win.repaint(420, 0, 560, 300);
		
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
		if(uart==0) {
			if(value==0)tape_active=false;
			else {
				if(tape_in_reader!=null)tape_active=true;
			}
		}else {
			if(value==9) { // TTYGO
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
			int c=tape_in_reader.read();
			transmit.add(""+(char)c);
		}catch(IOException ex) {;}
	}

}
