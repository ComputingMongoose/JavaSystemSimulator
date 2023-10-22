package jss.devices.peripherals.TerminalUtils;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.peripherals.AbstractSerialDevice;
import jss.devices.peripherals.PeripheralSwitch;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequence;
import jss.devices.peripherals.TerminalUtils.TerminalMouseListener;
import jss.devices.peripherals.TerminalUtils.TerminalSaveFileWindow;
import jss.devices.peripherals.TerminalUtils.TerminalSendFileWindow;
import jss.devices.peripherals.TerminalUtils.TerminalStatus;
import jss.simulation.Simulation;

public abstract class AbstractTerminal extends AbstractSerialDevice {

	protected BufferedImage imgFrontPanel;
	
	protected TerminalControlSequence terminalControlSeq;
	protected TerminalControlSequence terminalControlSeqBasic;
	protected TerminalSendFileWindow winSendFile;
	protected TerminalSaveFileWindow winSaveFile;
	
	protected boolean lc_characters;
	protected boolean space_adv;
	protected boolean clr_scrn;
	
	protected HashMap<String,PeripheralSwitch> switches;
	protected TerminalFrontWindow win;
	
	protected String title;
	protected int startWidth, startHeight;
	
	protected int num_lines;


	public int getBaseWidth() {
		if(imgFrontPanel!=null)return imgFrontPanel.getWidth();
		return status.getRenderer().getWidth()+60;
	}
	
	public int getBaseHeight() {
		if(imgFrontPanel!=null)return imgFrontPanel.getHeight();
		return status.getRenderer().getHeight()+160;
	}
	
	public abstract Rectangle getTextRect() ; 
	public abstract Rectangle getSwitchSendFileRect() ; 
	public abstract Rectangle getSwitchSaveFileRect() ; 
	
	public MouseListener getMouseListener() { 
		return new TerminalMouseListener(status); 
	}
	
	public KeyListener getKeyListener() {
		return new TerminalKeyListener(status);		
	}
	
	public BufferedImage getTerminalImage() {
		return imgFrontPanel;
	}
	
	public void testTerminal() {
		
		int oldB=status.getEolBehavior();
		int oldC=status.getCursorCharacter();
		
		status.setEolBehavior(TerminalStatus.EOL_NEXT);
		
		status.setCursorCharacter(0);
		for(int i=0;i<128;i++) {
			status.setCurrentChar(i);
			status.advanceCursorX();
		}
		
		status.advanceCursorY();
		status.advanceCursorY();
		status.setCur_x(0);
		status.controlReverseVideo();
		for(int i=0;i<128;i++) {
			status.setCurrentChar(i);
			status.advanceCursorX();
		}
		
		status.setEolBehavior(oldB);
		status.controlReverseVideo();
		status.advanceCursorY();
		status.setCur_x(0);
		status.setCursorCharacter(oldC);
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		status=new TerminalStatus(config,sim);

		lc_characters=(int)config.getOptLong("lc_characters", 0)==1;
		space_adv=(int)config.getOptLong("space_adv", 0)==1;
		clr_scrn=(int)config.getOptLong("clr_scrn", 1)==1;
		status.setEnableKbLock((int)config.getOptLong("enable_kb_lock", 1)==1);
		status.setCur_ctl((int)config.getOptLong("cur_ctl", 1)==1);
		
		num_lines=(int)config.getOptLong("num_lines", 24);

		if(!status.isCur_ctl())status.setCur_y(num_lines-1);
		
		BufferedImage imgLoad = ImageIO.read(getClass().getResource("/res/common/load.png"));
		BufferedImage imgSave = ImageIO.read(getClass().getResource("/res/common/save.png"));

		switches=new HashMap<>(100);
		
		Rectangle r=getSwitchSendFileRect();
		PeripheralSwitch sw=new PeripheralSwitch(r.x,r.y,r.width,r.height,false,imgLoad,imgLoad);
		sw.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				winSendFile.setVisible(true);
			}
		});
		switches.put("sendfile", sw);

		r=getSwitchSaveFileRect();
		sw=new PeripheralSwitch(r.x,r.y,r.width,r.height,false,imgSave,imgSave);
		sw.setActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				winSaveFile.setVisible(true);
			}
		});
		switches.put("savefile", sw);
				
		status.setSwitches(switches);
		status.setEolBehavior(TerminalStatus.EOL_DISCARD);
		
		winSendFile=new TerminalSendFileWindow(status);
		winSaveFile=new TerminalSaveFileWindow(status);
		
	}
	
	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException {
		super.initialize();
		
		win=new TerminalFrontWindow(status,this);
		win.repaint();
	}
	
	public int makeDisplayChar(int data) {
		return data;
	}

	@Override
	public void writeData(int data) throws MemoryAccessException {
		winSaveFile.writeData(data);

		if(this.terminalControlSeq!=null && this.terminalControlSeq.processCharacter(data)==1) return ;

		if(this.terminalControlSeqBasic!=null && this.terminalControlSeqBasic.processCharacter(data)==1) return;
		
		int c=makeDisplayChar(data);
		if(c!=' ' || !space_adv)status.setCurrentChar(c);
		status.advanceCursorX();
		
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
	}

	@Override
	public void getTransmitData() throws MemoryAccessException {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStartWidth() {
		return startWidth;
	}

	public void setStartWidth(int startWidth) {
		this.startWidth = startWidth;
	}

	public int getStartHeight() {
		return startHeight;
	}

	public void setStartHeight(int startHeight) {
		this.startHeight = startHeight;
	}

	public HashMap<String, PeripheralSwitch> getSwitches() {
		return switches;
	}

	public int getNum_lines() {
		return num_lines;
	}

	public void setNum_lines(int num_lines) {
		this.num_lines = num_lines;
	}

}
