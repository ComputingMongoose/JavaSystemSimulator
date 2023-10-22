package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.peripherals.TerminalUtils.AbstractTerminal;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequenceADM;
import jss.devices.peripherals.TerminalUtils.TerminalFont;
import jss.devices.peripherals.TerminalUtils.TerminalKeyListener;
import jss.devices.peripherals.TerminalUtils.TerminalStatus;
import jss.devices.peripherals.TerminalUtils.TerminalTextRenderer;
import jss.simulation.Simulation;

/* 
 * ADM-3A "Dumb" terminal
 * Operator manual: https://vt100.net/lsi/adm3a-om.pdf
 */
public class ADM3ATerminal extends AbstractTerminal {
	
	class FrontWindowKeyListener extends TerminalKeyListener {
		public FrontWindowKeyListener(TerminalStatus status) {
			super(status);
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			if(status.isKbLock())return ;
			
			char c=arg0.getKeyChar();
			if(!lc_characters && c>='a' && c<='z')c+=('A'-'a');
			
			if(c==10)c=13;
			
			status.transmitChar(c);
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

		this.setStartWidth(1000);
		this.setStartHeight(1000);
		this.setTitle("ADM-3A Terminal");

		imgFrontPanel = ImageIO.read(getClass().getResource("/res/ADM3A/adm3a.png"));

		// Keyboard characters => maybe not really needed ?
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
		
		TerminalFont font=new TerminalFont("/res/common/CGA_D.F16", 256, 8, 16, Color.GREEN.getRGB(), Color.BLACK.getRGB());
		TerminalTextRenderer renderer=new TerminalTextRenderer(font,80,num_lines);
		status.setRenderer(renderer);
		
		this.terminalControlSeqBasic=new TerminalControlSequenceADM(status);
	}
	
	@Override
	public int makeDisplayChar(int data) {
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
	public Rectangle getTextRect() {
		return new Rectangle(350,240,1470-350,1020-240);
	}

	@Override
	public Rectangle getSwitchSendFileRect() {
		return new Rectangle(100,50,80,80);
	}

	@Override
	public Rectangle getSwitchSaveFileRect() {
		return new Rectangle(190,50,80,80);
	}

}
