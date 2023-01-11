package jss.devices.peripherals;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDevice;
import jss.simulation.Simulation;

public class SimulationControl implements GenericDevice {

	Simulation sim;
	
	boolean do_step;
	
	Object lock;
	
	JButton resumeButton;
	JButton stopButton;
	JButton suspendButton;
	JTextArea textArea;
	long lastLogTS;
	
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame implements ActionListener{
		
		private void setInitialText() {
			String name=sim.getSimulationName();
			
			textArea.setText(
				"Simulation: "+name+"\n"
				+"Maximum steps: "+sim.getMaxSteps()+"\n"
				+"Delay between steps: "+sim.getDelayBetweenSteps_ms()+"ms, "+sim.getDelayBetweenSteps_ns()+" ns\n"
				//+"Number of devices: "+sim.getNumberOfDevices()+"\n"
				+"Log:\n"+sim.getCurrentLog()
			);
		}
		
		public FrontWindow() {
			super("Simulation Control");
			setSize(1000,500);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			//Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			//this.setCursor(cur);
			
			JPanel mainPanel=new JPanel();
			this.add(mainPanel);
			BoxLayout boxLayout=new BoxLayout(mainPanel,BoxLayout.Y_AXIS);
			mainPanel.setLayout(boxLayout);
			mainPanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
			
			JPanel topPanel=new JPanel();
			FlowLayout flowLayout=new FlowLayout();
			topPanel.setLayout(flowLayout);
			mainPanel.add(topPanel);
			
			resumeButton=new JButton("Resume");
			resumeButton.setActionCommand("resume");
			resumeButton.addActionListener(this);
			topPanel.add(resumeButton);
			if(!sim.getSuspended())resumeButton.setVisible(false);
			
			suspendButton=new JButton("Suspend");
			suspendButton.setActionCommand("suspend");
			suspendButton.addActionListener(this);
			topPanel.add(suspendButton);
			if(sim.getSuspended())suspendButton.setVisible(false);
			topPanel.setMaximumSize(new Dimension(1600,200));
			
			
			textArea = new JTextArea();
			textArea.setSize(400,400);    

			textArea.setLineWrap(true);
			textArea.setEditable(false);
			textArea.setVisible(true);

			JScrollPane scroll = new JScrollPane (textArea);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			mainPanel.add(scroll);
			
			setInitialText();
			
			setVisible(true);
			
			Timer timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	long ts=sim.getLastLogTS();
                	if(ts>lastLogTS) {
                		lastLogTS=ts;
                		setInitialText();
                	}
                }
            });	
			timer.start();
			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String cmd=arg0.getActionCommand();
			if(cmd.contentEquals("resume")) {
				sim.setSuspended(false);
				resumeButton.setVisible(false);
				suspendButton.setVisible(true);
			}else if(cmd.contentEquals("suspend")) {
					sim.setSuspended(true);
					resumeButton.setVisible(true);
					suspendButton.setVisible(false);
			}
			
		}
		
	}
	
	FrontWindow win;
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		lock=new Object();
		win=new FrontWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
	}

}
