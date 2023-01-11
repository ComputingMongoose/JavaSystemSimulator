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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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

/* 
 * ADM-3A "Dumb" terminal
 * Operator manual: https://vt100.net/lsi/adm3a-om.pdf
 */
public class ADM3ATerminal extends AbstractSerialDevice {

	Simulation sim;
	
	BufferedImage imgFrontPanel;
	
	boolean needsUpdate;
	boolean lc_characters;
	boolean space_adv;
	boolean clr_scrn;
	boolean enable_kb_lock;
	boolean kb_lock;
	boolean cur_ctl;
	
	HashMap<String,PeripheralSwitch> switches;
	
	char [][]text;
	int cur_x,cur_y;
	
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
			if(kb_lock)return ;
			
			char c=arg0.getKeyChar();
			if(!lc_characters && c>='a' && c<='z')c+=('A'-'a');
			
			if(c==10)c=13;
			
			synchronized(lock) {transmit.add(""+c);}
		}
	}
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow() {
			super("ADM-3A Terminal");
			
			this.setLayout(null);
			
			setSize(1000,1000);
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

			int x=350;
			int y=240;
			g2.setFont(new Font("Consolas",Font.BOLD,26));
			g2.setColor(Color.GREEN);
			int height = g2.getFontMetrics().getHeight();

			Charset oem = Charset.forName("Cp437");			
			ByteBuffer buff = ByteBuffer.allocate(80);
			for(int i=0;i<text.length;i++) {
				buff.clear();
				synchronized(lock) {
					for(int j=0;j<text[i].length;j++) {
						if(cur_y==i && cur_x==j)buff.put(cur_ctl?(byte)219:(byte)220);
						else buff.put((byte)text[i][j]);
					}
				}
				buff.flip();
				CharBuffer cbuff = oem.decode(buff);
				g2.drawString( cbuff.toString(), x, y + height );
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

		imgFrontPanel = ImageIO.read(getClass().getResource("/res/ADM3A/adm3a.png"));

		lc_characters=(int)config.getOptLong("lc_characters", 0)==1;
		space_adv=(int)config.getOptLong("space_adv", 0)==1;
		clr_scrn=(int)config.getOptLong("clr_scrn", 1)==1;
		enable_kb_lock=(int)config.getOptLong("enable_kb_lock", 1)==1;
		kb_lock=false;
		cur_ctl=(int)config.getOptLong("cur_ctl", 1)==1;
		
		
		int num_lines=(int)config.getOptLong("num_lines", 24);
		text=new char[num_lines][];
		for(int i=0;i<text.length;i++) {
			text[i]=new char[80];
			for(int j=0;j<text[i].length;j++)text[i][j]=' ';
		}
		cur_x=0;
		cur_y=0;
		if(!cur_ctl)cur_y=num_lines-1;
		
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
		
		win=new FrontWindow();
		win.repaint();
	}
	
	private void advanceCursorY() {
		cur_y++;
		if(cur_y>=text.length) {
			cur_y--;
			for(int i=0;i<text.length-1;i++) {
				for(int j=0;j<text[i].length;j++) {
					text[i][j]=text[i+1][j];
				}
			}
			for(int i=0;i<text[0].length;i++) {
				text[text.length-1][i]=' ';
			}
		}
	}
	
	private void advanceCursorX() {
		cur_x++; 
		if(cur_x>=text[cur_y].length) {
			cur_x=0;
			advanceCursorY();
		}
	}
	
	private int makeDisplayChar(int data) {
		char c=(char)data;
		if(
				c==10 || c==13 || c==' ' ||
				c>='0' && c<='9' || 
				c>='A' && c<='Z' ||
				c=='!' || c=='"' || c=='#' || c=='$' || c=='%' || c=='&' ||
				c=='(' || c==')' || c=='*' || c=='+' || c==',' || c=='-' ||
				c=='.' || c=='/' || c==':' || c==';' || c=='<' || c=='=' || 
				c=='>' || c=='?' || c=='[' || c=='\\' || c==']' || c=='@' ||
				c=='_' || c=='^'
		) return c; // Standard displayable characters
		
		if(c>='a' && c<='z') {
			if(!this.lc_characters)c+='A'-'a';
			return c;
		}
		
		if(this.lc_characters) {
			if(
				c=='`' || c=='{' || c=='|' || c=='}' || c=='~'
			)return c;
		}
		
		return 0;
				
		
	}

	@Override
	public void writeData(int data) throws MemoryAccessException {
		switch(data) {
		case 8: // BS
			synchronized(lock) {
				if(cur_x>0)cur_x--;
				else if(this.cur_ctl && cur_y>0) {cur_x=text[0].length-1;cur_y--;}
				needsUpdate=true;
			}	
			break;
		case 10: // LF
			synchronized(lock) {
				advanceCursorY();
				needsUpdate=true;
			}
			break;
		case 11: // VT
			synchronized(lock) {
				if(cur_ctl && cur_y>0) {
					cur_y--;
					needsUpdate=true;
				}
			}
			break;
		case 12: // FF
			synchronized(lock) {
				if(cur_ctl && cur_x<text[0].length-1) {
					cur_x++;
					needsUpdate=true;
				}
			}
			break;
		case 13: // CR
			synchronized(lock) {
				cur_x=0;
				needsUpdate=true;
			}
			break;
		case 14: // SO
			synchronized(lock) {
				if(enable_kb_lock) {
					kb_lock=false;
				}
			}
			break;
		case 15: // SI
			synchronized(lock) {
				if(enable_kb_lock) {
					kb_lock=true;
				}
			}
			break;
		case 26: // SUB
			synchronized(lock) {
				if(this.clr_scrn) {
					for(int i=0;i<text.length;i++) {
						for(int j=0;j<text[i].length;j++) {
							text[i][j]=' ';
						}
					}
					needsUpdate=true;
				}
			}
			break;
		case 27: // ESC - initiate load cursor
			break;
		case 30: // RS - HOME cursor
			synchronized(lock) {
				if(cur_ctl) {cur_x=0;cur_y=0;}
				else cur_x=0;
				needsUpdate=true;
			}
			break;
		default:
			int c=makeDisplayChar(data);
			if(c>0) {
				synchronized(lock) {
					if(c!=' ' || !space_adv)text[cur_y][cur_x]=(char)c;
					advanceCursorX();
					needsUpdate=true;
				}
			}
		}
		//win.repaint(420, 0, 560, 300);
		
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
	}

	@Override
	public void getTransmitData() throws MemoryAccessException {
	}

}
