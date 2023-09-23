package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

/* 
 * Based on the ADM3ATerminal class
 * 
 * Nice resources about ANSI codes:
 * https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
 * https://www.real-world-systems.com/docs/ANSIcode.html
 * https://vt100.net/docs/vt100-ug/chapter3.html
 * 
 */
public class GenericTerminal extends AbstractSerialDevice {

	boolean needsUpdate;
	boolean lc_characters;
	boolean space_adv;
	boolean clr_scrn;
	boolean enable_kb_lock;
	boolean kb_lock;
	boolean cur_ctl;
	
	boolean enable_ansi;
	
	HashMap<String,PeripheralSwitch> switches;
	
	char [][]text;
	int cur_x,cur_y;

	FrontWindow win;
	SendFileWindow winSendFile;
	SaveFileWindow winSaveFile;
	File sendFile;
	File saveFile;
	BufferedOutputStream saveOut;
	
	class FrontWindowKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent event) {
			synchronized(lock) {
				switch(event.getKeyCode()) {
				case KeyEvent.VK_UP:
					System.out.println("KEY UP");
					transmit.add(""+(char)27);
					//transmit.add("[");
					transmit.add("A");
					break;
				case KeyEvent.VK_DOWN:
					transmit.add(""+(char)27);
					//transmit.add("[");
					transmit.add("B");
					break;
				case KeyEvent.VK_RIGHT:
					transmit.add(""+(char)27);
					//transmit.add("[");
					transmit.add("C");
					break;
				case KeyEvent.VK_LEFT:
					transmit.add(""+(char)27);
					//transmit.add("[");
					transmit.add("D");
					break;
				case KeyEvent.VK_F1:
					transmit.add(""+(char)27);
					//transmit.add("O");
					transmit.add("P");
					break;
				case KeyEvent.VK_F2:
					transmit.add(""+(char)27);
					//transmit.add("O");
					transmit.add("Q");
					break;
				case KeyEvent.VK_F3:
					transmit.add(""+(char)27);
					//transmit.add("O");
					transmit.add("R");
					break;
				case KeyEvent.VK_F4:
					transmit.add(""+(char)27);
					//transmit.add("O");
					transmit.add("S");
					break;
				default:
					break;
				}			
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {;}

		@Override
		public void keyTyped(KeyEvent event) {
			if(kb_lock)return ;
			
			synchronized(lock) {
					char c=event.getKeyChar();
					if(c==10)c=13; // do we still need this ?
					transmit.add(""+c);
			
			}
		}
	}
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		Font consoleFont;
		
		public FrontWindow() {
			super("Generic Terminal");
			
			InputStream is = getClass().getResourceAsStream("/res/common/Px437_IBM_CGA.ttf");
			try {
				consoleFont = Font.createFont(Font.TRUETYPE_FONT, is);
				consoleFont = consoleFont.deriveFont(Font.BOLD, 12f); // 12f=>1200x500...
			} catch (Exception e) {
				e.printStackTrace();
				consoleFont=new Font("Consolas",Font.BOLD,25);
			}			
			consoleFont=new Font("Consolas",Font.BOLD,25);
			
			this.setLayout(null);
			
			
			
			setSize(1066,796);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			this.addKeyListener(new FrontWindowKeyListener());
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
			//super.paint(g);
			
			BufferedImage back=new BufferedImage(1200,900,BufferedImage.TYPE_INT_RGB); // 1200X500 for font...
			
			Graphics2D g2=(Graphics2D)back.getGraphics();

			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, 1200, 900);
			
			int x=30;
			int y=100;
			
			g2.setFont(consoleFont);//new Font("Consolas",Font.BOLD,25));
			
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
			
			for(Entry <String,PeripheralSwitch> e:switches.entrySet()) {
				PeripheralSwitch s=e.getValue();
				g2.drawImage(s.swOn, s.x, s.y, s.w, s.h,null);
			}
			
			Image img=back.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH);
			g.drawImage(img,0,0,null);


		}
	}
	
	class FrontWindowMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			int x=arg0.getX() * 1200/win.getWidth();
			int y=arg0.getY() * 900/win.getHeight();
			
			for(Entry <String,PeripheralSwitch> e:switches.entrySet()) {
				PeripheralSwitch s=e.getValue();
				String key=e.getKey();
				if(x>=s.x && x<=s.x+s.w && y>=s.y && y<=s.y+s.h) {
					s.on=!s.on;
					switchClicked(key,s);
					synchronized(lock) {needsUpdate=true;}
					break;
				}
			}
		}
		
		public void switchClicked(String key, PeripheralSwitch sw) {
			if(key.contentEquals("sendfile")) {
				winSendFile.setVisible(true);
			}else if(key.contentEquals("savefile")) {
				winSaveFile.setVisible(true);
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
	class SendFileWindow extends JFrame implements ActionListener{
		
		private JButton browseButton;
		private JComboBox<String> sendOption;
		private JCheckBox sendEOF;
		private JTextArea inspectArea;
		private JTextField hexStart;
		private JLabel labelHexStart;
		private JButton sendButton;
		
		public SendFileWindow() {
			super("Send File");
			setSize(1000,500);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			
			JPanel mainPanel=new JPanel();
			this.add(mainPanel);
			BoxLayout boxLayout=new BoxLayout(mainPanel,BoxLayout.Y_AXIS);
			mainPanel.setLayout(boxLayout);
			mainPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
			
			JPanel topPanel=new JPanel();
			FlowLayout flowLayout=new FlowLayout();
			topPanel.setLayout(flowLayout);
			mainPanel.add(topPanel);
			
			browseButton=new JButton("Browse");
			browseButton.setActionCommand("browse");
			browseButton.addActionListener(this);
			topPanel.add(browseButton);
			
			JLabel label=new JLabel("Encoding:");
			topPanel.add(label);
			sendOption=new JComboBox<>();
			sendOption.addItem("No Encoding");
			sendOption.addItem("HEX");
			sendOption.setActionCommand("encoding");
			sendOption.addActionListener(this);
			topPanel.add(sendOption);

			labelHexStart=new JLabel("HEX Start Address (hex):");
			labelHexStart.setVisible(false);
			topPanel.add(labelHexStart);
			hexStart=new JTextField(10);
			hexStart.setText("0100");
			hexStart.setVisible(false);
			topPanel.add(hexStart);
			
			label=new JLabel("Send EOF:");
			topPanel.add(label);
			sendEOF=new JCheckBox();
			sendEOF.setSelected(true);
			topPanel.add(sendEOF);	
			
			sendButton=new JButton("SEND");
			sendButton.setActionCommand("send");
			sendButton.addActionListener(this);
			topPanel.add(sendButton);
			
			topPanel.setMaximumSize(new Dimension(1600,200));
			
			
			inspectArea = new JTextArea();
			inspectArea.setFont(new Font("Consolas",Font.BOLD,14));
			//inspectArea.setColor(Color.BLACK);
			inspectArea.setSize(400,400);    

			inspectArea.setLineWrap(true);
			inspectArea.setEditable(false);
			inspectArea.setVisible(true);

			JScrollPane scroll = new JScrollPane (inspectArea);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			mainPanel.add(scroll);
			
			/*Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });	
			timer.start();*/
			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd=arg0.getActionCommand();

			if(cmd.contentEquals("browse")) {
				FileDialog fd=new FileDialog(winSendFile,"Select file",FileDialog.LOAD);
				fd.setVisible(true);
				File[] files=fd.getFiles();
				if(files!=null && files.length>0) {
					sendFile=files[0];
					inspectArea.setText("Selected file: "+sendFile.getName());
				}
			}else if(cmd.contentEquals("encoding")) {
				if(sendOption.getSelectedItem().toString().contentEquals("HEX")) {
					this.labelHexStart.setVisible(true);
					this.hexStart.setVisible(true);
				}else {
					this.labelHexStart.setVisible(false);
					this.hexStart.setVisible(false);
				}
			}else if(cmd.contentEquals("send")) {
				inspectArea.setText("Starting to send file ["+sendFile.getName()+"]\n");
				
				String encoding=sendOption.getSelectedItem().toString();
				boolean sendEOF=this.sendEOF.isSelected();
				long hexAddress=Long.parseLong(this.hexStart.getText(),16);
				
				new Thread(new Runnable() {
				    @Override
				    public void run() {
				    	try {
					    	if(encoding.contentEquals("No Encoding")) {
					    		inspectArea.setText(inspectArea.getText()+"\nThe file will be send without applying an encoding. This is suitable for text files.\n");
					    		BufferedInputStream in=new BufferedInputStream(new FileInputStream(sendFile));
					    		while(in.available()>0) {
					    			int b=in.read();
					    			synchronized(lock) {transmit.add(""+(char)b);}
					    		}
					    		in.close();
					    	}else if(encoding.contentEquals("HEX")) {
					    		inspectArea.setText(inspectArea.getText()+String.format("\nThe file will be encoded as HEX file with starting address [%04Xh]=[%d]. This is suitable for executable files.\n",hexAddress,hexAddress));

					    		int bc=0;
					    		int chk=0;
					    		StringBuffer dataS=new StringBuffer();		
					    		long startAddr=hexAddress;
					    		long currentAddr=startAddr;
					    		String dataToSend=null;
					    		
					    		BufferedInputStream in=new BufferedInputStream(new FileInputStream(sendFile));
					    		while(in.available()>0) {
						    		while(in.available()>0 && bc<16) {
						    			int b=in.read();
						    			b=b&0xFF;
										dataS.append(String.format("%02X",b));
										bc++;
										chk=(chk+b)&0xFF;
										currentAddr++;
						    		}

						    		// finish line
									chk=(chk+bc)&0xFF;
									chk=(chk+(int)startAddr&0xFF)&0xFF;
									chk=(chk+((int)startAddr>>8)&0xFF)&0xFF;
									chk^=0xFF;
									chk+=1;
									chk=chk&0xFF;
									dataToSend=String.format(":%02X%04X00%s%02X\n\r",bc,startAddr,dataS,chk);
									bc=0;
									dataS.setLength(0);
									chk=0;
									startAddr=currentAddr;

									synchronized(lock) {
										for(int i=0;i<dataToSend.length();i++) {
											char c=dataToSend.charAt(i);
											transmit.add(""+c);										
										}
									}
					    		}
					    		in.close();
					    		
						    	inspectArea.setText(inspectArea.getText()+
						    			String.format("Last HEX record: [%s]\nLast address [%04Xh]=[%d]",dataToSend,currentAddr,currentAddr
						    	));
					    		
					    	}
					    	
					    	inspectArea.setText(inspectArea.getText()+"\nFinished sending file content.\n");
					    	
					    	if(sendEOF) {
					    		inspectArea.setText(inspectArea.getText()+"Sending EOF.\n");
					    		int b=26; // ctrl+z 0x1A
				    			synchronized(lock) {transmit.add(""+(char)b);}
					    	}

					    	inspectArea.setText(inspectArea.getText()+"\n"+"Waiting for buffer to become empty...\n");
					    	while(true) {
					    		boolean empty=false;
					    		synchronized(lock) {empty=(transmit.size()==0);}
					    		if(empty)break;
					    		Thread.sleep(10);
					    	}
					    	
					    	inspectArea.setText(inspectArea.getText()+"\n"+"DONE\n");				    	
				    	}catch(Exception ex) {
				    		inspectArea.setText(inspectArea.getText()+"EXCEPTION: "+ex.getMessage()+"\n");
				    	}
				    }
				}).start();				
			}
		}
		
	}
	

	@SuppressWarnings("serial")
	class SaveFileWindow extends JFrame implements ActionListener{
		
		private JButton browseButton;
		private JButton sendButton;
		private JTextArea inspectArea;
		boolean saving;
		
		public SaveFileWindow() {
			super("Save File");
			
			saving=false;
			
			setSize(1000,500);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			
			JPanel mainPanel=new JPanel();
			this.add(mainPanel);
			BoxLayout boxLayout=new BoxLayout(mainPanel,BoxLayout.Y_AXIS);
			mainPanel.setLayout(boxLayout);
			mainPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
			
			JPanel topPanel=new JPanel();
			FlowLayout flowLayout=new FlowLayout();
			topPanel.setLayout(flowLayout);
			mainPanel.add(topPanel);
			
			browseButton=new JButton("Browse");
			browseButton.setActionCommand("browse");
			browseButton.addActionListener(this);
			topPanel.add(browseButton);
			
			sendButton=new JButton("SAVE");
			sendButton.setActionCommand("save");
			sendButton.addActionListener(this);
			topPanel.add(sendButton);
			
			topPanel.setMaximumSize(new Dimension(1600,200));
			
			
			inspectArea = new JTextArea();
			inspectArea.setFont(new Font("Consolas",Font.BOLD,14));
			//inspectArea.setColor(Color.BLACK);
			inspectArea.setSize(400,400);    

			inspectArea.setLineWrap(true);
			inspectArea.setEditable(false);
			inspectArea.setVisible(true);

			JScrollPane scroll = new JScrollPane (inspectArea);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			mainPanel.add(scroll);
			
			/*Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });	
			timer.start();*/
			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd=arg0.getActionCommand();

			if(cmd.contentEquals("browse")) {
				FileDialog fd=new FileDialog(winSendFile,"Select file",FileDialog.LOAD);
				fd.setVisible(true);
				File[] files=fd.getFiles();
				if(files!=null && files.length>0) {
					saveFile=files[0];
					inspectArea.setText("Selected file: "+saveFile.getName());
				}
			}else if(cmd.contentEquals("save")) {
				if(saving==false) {
					try {
						saveOut=new BufferedOutputStream(new FileOutputStream(saveFile,true));
						inspectArea.setText(inspectArea.getText()+"\nStarting to save data to file ["+saveFile.getName()+"]\n");
						sendButton.setText("STOP");
						saving=true;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						inspectArea.setText(inspectArea.getText()+"\nException opening file "+e.getMessage()+"\n");
					}
					
				}else {
					inspectArea.setText("Stopped. Data was written to file ["+saveFile.getName()+"]\n");
					sendButton.setText("SAVE");
					saving=false;
					try {
						saveOut.close();
					} catch (IOException e) {
						inspectArea.setText(inspectArea.getText()+"\nException closing file "+e.getMessage()+"\n");
						e.printStackTrace();
					}
					saveOut=null;
				}
				
			}
		}
		
	}
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);

		lc_characters=(int)config.getOptLong("lc_characters", 0)==1;
		space_adv=(int)config.getOptLong("space_adv", 0)==1;
		clr_scrn=(int)config.getOptLong("clr_scrn", 1)==1;
		enable_kb_lock=(int)config.getOptLong("enable_kb_lock", 1)==1;
		kb_lock=false;
		cur_ctl=(int)config.getOptLong("cur_ctl", 1)==1;
		
		enable_ansi=(config.getOptLong("enable_ansi", 0)==1);
		
		int num_lines=(int)config.getOptLong("num_lines", 24);
		text=new char[num_lines][];
		for(int i=0;i<text.length;i++) {
			text[i]=new char[80];
			for(int j=0;j<text[i].length;j++)text[i][j]=' ';
		}
		cur_x=0;
		cur_y=0;
		if(!cur_ctl)cur_y=num_lines-1;
		
		BufferedImage imgLoad = ImageIO.read(getClass().getResource("/res/common/load.png"));
		BufferedImage imgSave = ImageIO.read(getClass().getResource("/res/common/save.png"));

		switches=new HashMap<>(100);
		switches.put("sendfile", new PeripheralSwitch(100,50,50,50,false,imgLoad,imgLoad));
		switches.put("savefile", new PeripheralSwitch(160,50,50,50,false,imgSave,imgSave));
		
		needsUpdate=false;
		saveOut=null;
		
		win=new FrontWindow();
		win.repaint();
		winSendFile=new SendFileWindow();
		winSaveFile=new SaveFileWindow();
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
		if(c>=' ')return c;
		return 0;
	}

	// https://www.real-world-systems.com/docs/ANSIcode.html
	
	static final String[][]ansiCodes= {
	    new String[] {"[H","home"},
	    new String[] {"[##;##H","set"},
	    new String[] {"[##;##f","set"},
	    new String[] {"[#A","up"},
	    new String[] {"[##A","up"},
	    new String[] {"[#B","down"},
	    new String[] {"[##B","down"},
	    new String[] {"[#C","right"},
	    new String[] {"[##C","right"},
	    new String[] {"[#D","left"},
	    new String[] {"[##D","left"},
	    new String[] {"[#E","begin_next"},
	    new String[] {"[##E","begin_next"},
	    new String[] {"[#F","begin_prev"},
	    new String[] {"[##F","begin_prev"},
	    new String[] {"[#G","set_column"},
	    new String[] {"[##G","set_column"},
	    //new String[] {"[6n","request_cursor_position"},
	    new String[] {" M","line_up"},
	    new String[] {"[J","erase_to_end"},
	    new String[] {"[0J","erase_to_end"},
	    new String[] {"[1J","erase_to_begin"},
	    new String[] {"[2J","erase_all"},
	    //new String[] {"[3J","erase_saved_lines"}, // ????????????
	    new String[] {"[K","erase_to_end_line"},
	    new String[] {"[0K","erase_to_end_line"},
	    new String[] {"[1K","erase_to_begin_line"},
	    new String[] {"[2K","erase_all_line"},

	    new String[] {"[A","up1"},
	    new String[] {"[B","down1"},
	    new String[] {"[C","right1"},
	    new String[] {"[D","left1"},
	    
	    // ANSI Mode and Cursor Key Mode Set
	    new String[] {"OA","up1"},
	    new String[] {"OB","down1"},
	    new String[] {"OC","right1"},
	    new String[] {"OD","left1"},

	    // VT-52
	    new String[] {"A","up1"},
	    new String[] {"B","down1"},
	    new String[] {"C","right1"},
	    new String[] {"D","left1"},
	    
	    //VT-102
	    new String[] {"[1@","insert_char"},
	    new String[] {"[1P","delete_char"},
	    new String[] {"[1L","insert_line"},
	    new String[] {"[1M","delete_line"},
	    
	};
	
	private String ansi_current;
	private String ansi_current_pattern;
	private boolean ansi_parsing;
	
	private void ansi_apply_sequence(String seq) {
		synchronized(lock) {
			if(seq.contentEquals("home")) {
				cur_x=0;cur_y=0;
			}else if(seq.contentEquals("set")) {
				try {
					cur_y=Integer.parseInt(ansi_current.substring(1,3))-1;
					cur_x=Integer.parseInt(ansi_current.substring(4,6))-1;
				}catch(Exception ex) {;}
			}else if(seq.contentEquals("up")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_y-=up;
			}else if(seq.contentEquals("down")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_y+=up;
			}else if(seq.contentEquals("left")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_x-=up;
			}else if(seq.contentEquals("right")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_x+=up;
			}else if(seq.contentEquals("up1")) {
				cur_y--;
			}else if(seq.contentEquals("down1")) {
				cur_y++;
			}else if(seq.contentEquals("left1")) {
				cur_x--;
			}else if(seq.contentEquals("right1")) {
				cur_x++;
			}else if(seq.contentEquals("begin_next")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_y+=up;
				cur_y++;
				cur_x=0;
			}else if(seq.contentEquals("begin_prev")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3));
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2));
					}catch(Exception ex1) {;}
				}
				cur_y-=up;
				cur_y--;
				cur_x=0;
			}else if(seq.contentEquals("set_column")) {
				int up=0;
				try {
					up=Integer.parseInt(ansi_current.substring(1,3))-1;
				}catch(Exception ex) {
					try {
						up=Integer.parseInt(ansi_current.substring(1,2))-1;
					}catch(Exception ex1) {;}
				}
				cur_x=up;
			}else if(seq.contentEquals("line_up")) {
				cur_y--;
			}else if(seq.contentEquals("erase_to_end")) {
				for(int i=cur_x;i<text[cur_y].length;i++)text[cur_y][i]=' ';
				for(int i=cur_y+1;i<text.length;i++) {
					for(int j=0;j<text[i].length;j++)text[i][j]=' ';
				}
			}else if(seq.contentEquals("erase_to_begin")) {
				for(int i=cur_x-1;i>0;i--)text[cur_y][i]=' ';
				for(int i=cur_y-1;i>0;i--) {
					for(int j=0;j<text[i].length;j++)text[i][j]=' ';
				}
			}else if(seq.contentEquals("erase_all")) {
				for(int i=0;i<text.length;i++) {
					for(int j=0;j<text[i].length;j++)text[i][j]=' ';
				}
			}else if(seq.contentEquals("erase_to_end_line")) {
				for(int i=cur_x;i<text[cur_y].length;i++)text[cur_y][i]=' ';
			}else if(seq.contentEquals("erase_to_begin_line")) {
				for(int i=cur_x-1;i>0;i--)text[cur_y][i]=' ';
			}else if(seq.contentEquals("erase_all_line")) {
				for(int i=0;i<text[cur_y].length;i++) text[cur_y][i]=' ';
			}else if(seq.contentEquals("insert_char")) {
				for(int i=text[cur_y].length-1;i>cur_x;i--) text[cur_y][i]=text[cur_y][i-1];
				text[cur_y][cur_x]=' ';
			}else if(seq.contentEquals("delete_char")) {
				for(int i=cur_x;i<text[cur_y].length-1;i++) text[cur_y][i]=text[cur_y][i+1];
				text[cur_y][text[cur_y].length-1]=' ';
			}else if(seq.contentEquals("insert_line")) {
				for(int i=text.length-1;i>cur_y;i--) {
					for(int j=0;j<text[i].length;j++) {
						text[i][j]=text[i-1][j];
					}
				}
				for(int j=0;j<text[cur_y].length;j++) {
					text[cur_y][j]=' ';
				}
			}else if(seq.contentEquals("delete_line")) {
				for(int i=cur_y;i<text.length-1;i++) {
					for(int j=0;j<text[i].length;j++) {
						text[i][j]=text[i+1][j];
					}
				}
				for(int j=0;j<text[cur_y].length;j++) {
					text[text.length-1][j]=' ';
				}
			}
			
			if(cur_x<0)cur_x=0;
			if(cur_y<0)cur_y=0;
			if(cur_y>=text.length)cur_y=text.length-1;
			if(cur_x>=text[cur_y].length)cur_x=text[cur_y].length-1;
			
			needsUpdate=true;
		}
	}
	
	// checks and executes the current sequence
	private boolean ansi_run() {
		boolean possible=false;
		for(int i=0;i<ansiCodes.length;i++) {
			if(ansiCodes[i][0].contentEquals(ansi_current) || ansiCodes[i][0].contentEquals(ansi_current_pattern)) {
				ansi_apply_sequence(ansiCodes[i][1]);
				ansi_parsing=false;
				ansi_current="";
				ansi_current_pattern="";
				return true;
			}else if(ansiCodes[i][0].startsWith(ansi_current_pattern) || ansiCodes[i][0].startsWith(ansi_current))
				possible=true;
		}
		
		return possible;
	}
	
	@Override
	public void writeData(int data) throws MemoryAccessException {
		if(saveOut!=null) {
			try {
				saveOut.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(ansi_parsing) {
			ansi_current+=(char)data;
			if(data>='0' && data<='9')ansi_current_pattern+="#";
			else ansi_current_pattern+=(char)data;
			
			if(!ansi_run()) {
				sim.writeToCurrentLog(String.format("Unknown terminal escape sequence: [%s]",ansi_current));
				
				for(int i=0;i<ansi_current.length();i++) {
					int d=ansi_current.charAt(i);
					// this is the same as the default branch
					int c=makeDisplayChar(d);
					if(c>0) {
						synchronized(lock) {
							if(c!=' ' || !space_adv)text[cur_y][cur_x]=(char)c;
							advanceCursorX();
							needsUpdate=true;
						}
					}
					
				}
				ansi_parsing=false;
				ansi_current="";
				ansi_current_pattern="";
			}
			return ;
		}
		
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
			if(enable_ansi) {
				ansi_current="";
				ansi_current_pattern="";
				ansi_parsing=true;
			}
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
