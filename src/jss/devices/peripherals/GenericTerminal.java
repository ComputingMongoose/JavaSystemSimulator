package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.peripherals.TerminalUtils.AbstractTerminal;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequenceADM;
import jss.devices.peripherals.TerminalUtils.TerminalControlSequenceANSI;
import jss.devices.peripherals.TerminalUtils.TerminalFont;
import jss.devices.peripherals.TerminalUtils.TerminalKeyListener;
import jss.devices.peripherals.TerminalUtils.TerminalTextRenderer;
import jss.simulation.Simulation;

/* 
 * Implementation of a Generic Terminal, supporting ANSI control codes.
 * 
 * Nice resources about ANSI codes:
 * https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
 * https://www.real-world-systems.com/docs/ANSIcode.html
 * https://vt100.net/docs/vt100-ug/chapter3.html
 * 
 */
public class GenericTerminal extends AbstractTerminal {

	boolean enable_ansi;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		
		this.setStartWidth(1066);
		this.setStartHeight(796);
		this.setTitle("Generic Terminal");
		
		enable_ansi=(config.getOptLong("enable_ansi", 0)==1);
		
		TerminalFont font=new TerminalFont("/res/common/CGA_D.F16", 256, 8, 16, Color.GREEN.getRGB(), Color.BLACK.getRGB());
		TerminalTextRenderer renderer=new TerminalTextRenderer(font,80,num_lines);
		status.setRenderer(renderer);
		
		this.terminalControlSeq=new TerminalControlSequenceANSI(status);
		this.terminalControlSeqBasic=new TerminalControlSequenceADM(status);
		
	}
	
	@Override
	public Rectangle getTextRect() {
		return new Rectangle(30,100,status.getRenderer().getWidth(),status.getRenderer().getHeight());
	}

	@Override
	public Rectangle getSwitchSendFileRect() {
		return new Rectangle(100,50,50,50);
	}

	@Override
	public Rectangle getSwitchSaveFileRect() {
		return new Rectangle(160,50,50,50);
	}

	@Override
	public KeyListener getKeyListener() {
		return new TerminalKeyListener(status) {
			@Override
			public void keyPressed(KeyEvent event) {
				switch(event.getKeyCode()) {
					case KeyEvent.VK_UP:
						System.out.println("KEY UP");
						status.transmitChars(new char[] {27,'A'}); // maybe 27 [ A ?
						break;
					case KeyEvent.VK_DOWN:
						status.transmitChars(new char[] {27,'B'}); 
						break;
					case KeyEvent.VK_RIGHT:
						status.transmitChars(new char[] {27,'C'}); 
						break;
					case KeyEvent.VK_LEFT:
						status.transmitChars(new char[] {27,'D'}); 
						break;
					case KeyEvent.VK_F1:
						status.transmitChars(new char[] {27,'P'}); // maybe 27 O P ? 
						break;
					case KeyEvent.VK_F2:
						status.transmitChars(new char[] {27,'Q'}); 
						break;
					case KeyEvent.VK_F3:
						status.transmitChars(new char[] {27,'R'}); 
						break;
					case KeyEvent.VK_F4:
						status.transmitChars(new char[] {27,'S'}); 
						break;
					default:
						break;
				}
			}
		};
	}
	
}
