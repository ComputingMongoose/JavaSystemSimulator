package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.ImageIO;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.peripherals.TerminalUtils.AbstractTerminal;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequenceADM;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequenceHeathkit;
import jss.devices.peripherals.TerminalUtils.TerminalFont;
import jss.devices.peripherals.TerminalUtils.TerminalStatus;
import jss.devices.peripherals.TerminalUtils.TerminalTextRenderer;
import jss.simulation.Simulation;

/* 
 * Implementation of a Heathkit H19 Terminal.
 * 
 */
public class HeathkitH19Terminal extends AbstractTerminal {

	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		
		this.setStartWidth(1400);
		this.setStartHeight(1040);
		this.setTitle("Heathkit-H19 Terminal");
		imgFrontPanel = ImageIO.read(getClass().getResource("/res/HeathkitH19/Heathkit-H19_2.png"));
		
		num_lines=25; 

		if(!status.isCur_ctl())status.setCur_y(num_lines-1);
		
		TerminalFont font=new TerminalFont("/res/HeathkitH19/2716_444-29_h19font.bin", 128, 8, 16, 0xFFFFBF00, Color.BLACK.getRGB());
		TerminalTextRenderer renderer=new TerminalTextRenderer(font,80,num_lines);

		this.terminalControlSeq=new TerminalControlSequenceHeathkit(status);
		this.terminalControlSeqBasic=new TerminalControlSequenceADM(status);
		
		status.setRenderer(renderer);
		status.setRows(24); // initially 24 rows are active
		status.setCursorCharacter(27);//17/27
		status.setEolBehavior(TerminalStatus.EOL_DISCARD);
		
		//testTerminal();
	}
	
	@Override
	public int makeDisplayChar(int data) {
		char c=(char)data;
		if(c>=' ' && c<=127) {
			if(status.getCharacterSet()==1) {
				if(c>=96 && c<=126)c-=96;
				else if(c==94)c=127;
				else if(c==95)c=31;
			}
			return c;
		}
		return 0;
	}


	@Override
	public Rectangle getTextRect() {
		return new Rectangle(65,75,730,490);
	}

	@Override
	public Rectangle getSwitchSendFileRect() {
		return new Rectangle(950,50,50,50);
	}

	@Override
	public Rectangle getSwitchSaveFileRect() {
		return new Rectangle(1000,50,50,50);
	}

}
