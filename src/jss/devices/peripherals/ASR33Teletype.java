package jss.devices.peripherals;

import java.awt.FileDialog;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.peripherals.TerminalUtils.AbstractTerminal;
import jss.devices.peripherals.TerminalUtils.TerminalFont;
import jss.devices.peripherals.TerminalUtils.TerminalKeyListener;
import jss.devices.peripherals.TerminalUtils.TerminalStatus;
import jss.devices.peripherals.TerminalUtils.TerminalTextRenderer;
import jss.simulation.Simulation;

public class ASR33Teletype extends AbstractTerminal {

	int enable_load_tape;
	
	boolean tape_active;
	String tape_in;
	BufferedReader tape_in_reader;
	
	int tape_in_compute_checksums;
	
	class FrontWindowKeyListener extends TerminalKeyListener {
		public FrontWindowKeyListener(TerminalStatus status) {
			super(status);
		}

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
				status.transmitChar(c);
			}else if(c==13 || c==10) {
				status.transmitChar('\r');
			}
		}
	}
	
	@Override
	public KeyListener getKeyListener() {
		return new FrontWindowKeyListener(status);
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		this.setStartWidth(1600);
		this.setStartHeight(992);
		this.setTitle("ASR 33 Teletype");
		
		status.setCursorCharacter(0);

		imgFrontPanel = ImageIO.read(getClass().getResource("/res/ASR33Teletype/asr33.png"));
		
		enable_load_tape=(int)config.getOptLong("enable_load_tape", 1);

		num_lines=15;
		
		// Keyboard => maybe this can be removed ?
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
		
		if(enable_load_tape==1) {
			BufferedImage imgLoad = ImageIO.read(getClass().getResource("/res/common/load.png"));
			PeripheralSwitch sw=new PeripheralSwitch(85,100,100,100,false,imgLoad,imgLoad);
			sw.setActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd=new FileDialog(win,"Load Tape",FileDialog.LOAD);
					fd.setVisible(true);
					File[] files=fd.getFiles();
					if(files!=null && files.length>0) {
						loadTape(files[0]);
					}
				}
			});
			switches.put("LOAD", sw);
		}
		
		tape_active=false;
		this.tape_in_compute_checksums=(int)config.getOptLong("tape_in_compute_checksums", 0);
		tape_in=config.getOptString("tape_in", "");
		tape_in_reader=null;
		if(tape_in!=null && tape_in.length()>0) {
			loadTape(sim.getFilePath(tape_in).toFile());
		}
		
		TerminalFont font=new TerminalFont("/res/common/CGA_D.F16", 256, 8, 16, 0xFF000000, 0x00FFFFFF);
		TerminalTextRenderer renderer=new TerminalTextRenderer(font,80,num_lines);
		renderer.setBgColor(0x00000000);
		status.setRenderer(renderer);

	}
	
	public void loadTape(File tapeFile) {
		if(tape_in_reader!=null) {
			try {
				tape_in_reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tape_in_reader=null;
		}
		
		try {
			tape_in_reader=new BufferedReader(
					new InputStreamReader(
							new FileInputStream(tapeFile),Charset.forName("UTF8")));
			
			if(tape_in_compute_checksums==1) {
				ByteArrayOutputStream baos=new ByteArrayOutputStream(64*1024);
				PrintWriter out=new PrintWriter(new OutputStreamWriter(baos));
				StringBuffer buff=new StringBuffer();
				int lnum=0;
				for(String line=tape_in_reader.readLine();line!=null;line=tape_in_reader.readLine()) {
					lnum++;
					buff.setLength(0);
					int state=0;
					int length=0;
					int current=0;
					int chk=0;
					int num=0;
					boolean first_skip=true;
					int oldchk=0;
					for(int i=0;i<line.length();i++) {
						char c=line.charAt(i);
						switch(state) {
						case 0: // wait for :
							buff.append(c);
							if(c==':')state=1;
							break;
							
						case 1: // length start
							buff.append(c);
							length=Integer.parseInt(""+c,16);
							state=2;
							break;
							
						case 2: // length second nibble
							buff.append(c);
							length=(length<<4) | Integer.parseInt(""+c,16);
							state=3;
							chk+=length;
							chk&=0xFF;
							break;
							
						case 3: // regular byte
							buff.append(c);
							current=Integer.parseInt(""+c,16);
							state=4;
							break;
							
						case 4: // regular byte, second nibble
							buff.append(c);
							current=(current<<4) | Integer.parseInt(""+c,16);
							state=4;
							chk+=current;
							chk&=0xFF;
							num++;
							if(first_skip) {
								if(num==3) {first_skip=false;num=0;}
								state=3;
							}else {
								if(num<length)state=3; // read next
								else {
									state=5;
									chk=((chk^0xFF)+1)&0xFF;
									String chks=Integer.toHexString(chk).toUpperCase();
									if(chks.length()<2)chks="0"+chks;
									buff.append(chks);
								}
							}
							break;
							
						case 5: // old chk
							oldchk=Integer.parseInt(""+c,16);
							state=6;
							break;
							
						case 6: // old chk, second nibble
							oldchk=(oldchk<<4) | Integer.parseInt(""+c,16);
							state=7;
							if(oldchk!=chk) {
								this.sim.writeToCurrentLog("loadTape Invalid Checksum in line "+lnum);
							}
							break;
							
						case 7: // copy the rest of the line
							buff.append(c);
							break;
						}
					}
					
					out.println(buff.toString());
				}
				
				out.flush();
				out.close();
				
				tape_in_reader.close();
				
				tape_in_reader=new BufferedReader(
						new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
			}
		}catch(Exception ex) {tape_in_reader=null;}

		tape_in="";
		if(tape_in_reader!=null)tape_in=tapeFile.getAbsolutePath();
			
	}

	@Override
	public void writeData(int data) throws MemoryAccessException {
		winSaveFile.writeData(data);

		if(data==13) {
			status.advanceCursorY();
			status.setCur_x(0);
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
				status.setCurrentChar(data);
				status.advanceCursorX();
			}else {
				this.sim.writeToCurrentLog(String.format("ASR33Teletype received invalid character [%2X]",data));
			}
		}
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
		if(uart==0) {
			if(value==0)tape_active=false;
			else {
				if(tape_in_reader!=null)tape_active=true;
			}
		}else {
			if(value==9 || value==0x27) { // TTYGO
				tape_active=true;
				getTransmitData();
			}else if(value==8 || value==0x25) { // TTYNO
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

	@Override
	public Rectangle getTextRect() {
		return new Rectangle(440,20,960-440,275-20);
	}

	@Override
	public Rectangle getSwitchSendFileRect() {
		return new Rectangle(1050,20,50,50);
	}

	@Override
	public Rectangle getSwitchSaveFileRect() {
		return new Rectangle(1100,20,50,50);
	}

}
