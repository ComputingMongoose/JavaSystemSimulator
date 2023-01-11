package jss.devices.peripherals;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JToggleButton;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryDevice;
import jss.devices.memory.MemoryOperation;
import jss.simulation.Simulation;

public class Switches4 implements MemoryDevice {

	long data;
	
	Object lock;
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame{
		public FrontWindow(String name, String []buttons,int x, int y) {
			super(name);
			
			setLayout(new FlowLayout());
			
			Font f=new Font("Dialog", Font.BOLD, 16);
			
			for(int i=0;i<4;i++) {
				JToggleButton b=new JToggleButton(buttons[i]);
				Integer bnum=Integer.valueOf(i);
				b.setFont(f);
				this.add(b);
				b.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(b.isSelected())synchronized(lock) {data|=(1<<(3-bnum));}
						else synchronized(lock) {data&=(~(1<<(3-bnum)));}
					}
				});
			}
			
			this.pack();
			this.setLocation(x,y);
			setSize(300,100);
			setVisible(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		lock=new Object();
		data=0;
		
		String sbuttons=config.getString("buttons");
		String []buttons=sbuttons.split("[,]");
		String name=config.getString("name");
		
		long x=config.getOptLong("x",0);
		long y=config.getOptLong("y", 0);
		
		win=new FrontWindow(name,buttons,(int)x,(int)y);
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		data=0;
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		synchronized(lock) {return data;}
	}

	@Override
	public void write(long address, long value) throws MemoryAccessException {
		throw new MemoryAccessException(address,MemoryOperation.WRITE);
	}

}
