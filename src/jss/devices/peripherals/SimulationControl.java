package jss.devices.peripherals;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataDevice;
import jss.devices.GenericDevice;
import jss.devices.cpu.CPUDevice;
import jss.devices.cpu.CPUState;
import jss.devices.cpu.Disassembler;
import jss.devices.memory.MemoryAccessException;
import jss.disk.Disk;
import jss.disk.DiskController;
import jss.disk.DiskDrive;
import jss.disk.DiskSector;
import jss.simulation.Simulation;

public class SimulationControl implements GenericDevice {

	Simulation sim;
	
	boolean do_step;
	
	Object lock;
	
	JButton resumeButton;
	JButton stopButton;
	JButton suspendButton;
	JButton inspectMemButton;
	JButton inspectCPUButton;
	JButton inspectDiskButton;
	JButton inspectCodeButton;
	JTextArea textArea;
	long lastLogTS;

	FrontWindow win;
	InspectMemWindow inspectMemWin;
	InspectCPUWindow inspectCPUWin;
	InspectDiskWindow inspectDiskWin;
	InspectCodeWindow inspectCodeWin;
	
	private JComboBox<String> memDevices;
	private JComboBox<String> cpuDevices;
	private JComboBox<String> diskDevices;
	private JComboBox<String> codeMemDevices;
	private JComboBox<String> codeCpuDevices;
	
	@SuppressWarnings("serial")
	class FrontWindow extends JFrame implements ActionListener{
		
		private void setInitialText() {
			String name=sim.getSimulationName();
			
			textArea.setText(
				"Java System Simulator version: "+sim.getSystemSettings().getVersion()+"\n"+
				"Check latest version at: https://github.com/ComputingMongoose/JavaSystemSimulator\n\n"+
				"Simulation: "+name+"\n"
				+"Maximum steps: "+sim.getMaxSteps()+"\n"
				+"Delay between steps: "+sim.getDelayBetweenSteps_ms()+"ms, "+sim.getDelayBetweenSteps_ns()+" ns\n"
				//+"Number of devices: "+sim.getNumberOfDevices()+"\n"
				+"\nSimulation log:\n"+sim.getCurrentLog()
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

			inspectMemButton=new JButton("Inspect MEM");
			inspectMemButton.setActionCommand("inspectmem");
			inspectMemButton.addActionListener(this);
			topPanel.add(inspectMemButton);

			inspectCPUButton=new JButton("Inspect CPU");
			inspectCPUButton.setActionCommand("inspectcpu");
			inspectCPUButton.addActionListener(this);
			topPanel.add(inspectCPUButton);
			
			inspectDiskButton=new JButton("Inspect Disk");
			inspectDiskButton.setActionCommand("inspectdisk");
			inspectDiskButton.addActionListener(this);
			topPanel.add(inspectDiskButton);

			inspectCodeButton=new JButton("Disassemble");
			inspectCodeButton.setActionCommand("inspectcode");
			inspectCodeButton.addActionListener(this);
			topPanel.add(inspectCodeButton);

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
                	
                	if(sim.getSuspended()) {
                		if(!resumeButton.isVisible()) {
        					resumeButton.setVisible(true);
        					suspendButton.setVisible(false);
                		}
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
			}else if(cmd.contentEquals("inspectmem")) {
				memDevices.removeAllItems();
				for(String d:sim.getDevicesByType("GenericDataDevice")) {
					memDevices.addItem(d);
				}
				inspectMemWin.setVisible(true);
			}else if(cmd.contentEquals("inspectcpu")) {
				cpuDevices.removeAllItems();
				for(String d:sim.getDevicesByType("CPUDevice")) {
					cpuDevices.addItem(d);
				}
				inspectCPUWin.setVisible(true);				
			}else if(cmd.contentEquals("inspectdisk")) {
				diskDevices.removeAllItems();
				for(String d:sim.getDevicesByType("DiskController")) {
					DiskController dc=(DiskController)sim.getDevice(d);
					int num=0;
					for(DiskDrive drv:dc.getDrives()) {
						Disk dsk=drv.getDisk();
						if(dsk!=null) {
							diskDevices.addItem(d+":"+num);
						}
						num++;
					}
					
				}
				inspectDiskWin.setVisible(true);				
			}else if(cmd.contentEquals("inspectcode")) {
				codeMemDevices.removeAllItems();
				for(String d:sim.getDevicesByType("GenericDataDevice")) {
					codeMemDevices.addItem(d);
				}
				codeCpuDevices.removeAllItems();
				for(String d:sim.getDevicesByType("CPUDevice")) {
					codeCpuDevices.addItem(d);
				}
				inspectCodeWin.setVisible(true);				
				
			}
			
		}
		
	}
	
	@SuppressWarnings("serial")
	class InspectMemWindow extends JFrame implements ActionListener{
		
		private JButton viewButton;
		private JTextArea inspectArea;
		private JTextField textStart;
		private JTextField textSize;
		
		public InspectMemWindow() {
			super("Memory Inspector");
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
			
			memDevices=new JComboBox<>();
			topPanel.add(memDevices);
			
			JLabel label=new JLabel("Start (hex):");
			topPanel.add(label);
			textStart=new JTextField(5);
			textStart.setText("0000");
			topPanel.add(textStart);
			
			label=new JLabel("Size (hex):");
			topPanel.add(label);
			textSize=new JTextField(5);
			textSize.setText("100");
			topPanel.add(textSize);
			
			viewButton=new JButton("View");
			viewButton.setActionCommand("view");
			viewButton.addActionListener(this);
			topPanel.add(viewButton);
			
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
			if(cmd.contentEquals("view")) {
				String tStart=textStart.getText();
				String tSize=textSize.getText();
				
				long start=Long.parseLong(tStart, 16);
				long size=Long.parseLong(tSize, 16);
				
				String dev=(String) memDevices.getSelectedItem();
				
				GenericDataDevice d=(GenericDataDevice)sim.getDevice(dev);
				
				StringBuffer buff=new StringBuffer();
				StringBuffer buffAscii=new StringBuffer();
				long addr;
				for(addr=0;addr<size;addr++) {
					if(addr%16==0) {
						buff.append(String.format("%06X",addr+start));
						buff.append(" | ");
					}
					
					try {
						long data=d.read(addr+start);
						buff.append(String.format("%02X ", data));
						if(data>=32 && data<=255)buffAscii.append(String.valueOf((char)data));
						else buffAscii.append(".");
					} catch (MemoryAccessException e) {
						buff.append("-- ");
					}
					
					if(((addr+1)%16) == 0) {
						buff.append("  | ");
						buff.append(buffAscii.toString());
						buffAscii.setLength(0);
						buff.append("\n");
					}
				}
				
				if(buffAscii.length()>0) {
					int n = (int)(16 - (addr%16));
					buff.append(new String(new char[n*3]).replace("\0", " "));
					buff.append("  | ");
					buff.append(buffAscii.toString());
					buffAscii.setLength(0);
					buff.append("\n");
					
				}
				
				inspectArea.setText(buff.toString());
				inspectArea.setCaretPosition(0);
				
			}
		}
		
	}
	
	@SuppressWarnings("serial")
	class InspectCPUWindow extends JFrame implements ActionListener{
		
		private JButton viewButton;
		private JButton stepButton;
		private JButton steptoButton;
		private JButton backButton;
		private JTextField steptoText;
		private JTextArea inspectArea;
		
		private ArrayList<CPUState> stepHistory;
		private int currentStepHistory;
		
		public InspectCPUWindow() {
			super("CPU Inspector");
			stepHistory=new ArrayList<>(1000);
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
			
			cpuDevices=new JComboBox<>();
			topPanel.add(cpuDevices);
						
			viewButton=new JButton("View");
			viewButton.setActionCommand("view");
			viewButton.addActionListener(this);
			topPanel.add(viewButton);
			
			backButton=new JButton("Back");
			backButton.setActionCommand("back");
			backButton.addActionListener(this);
			topPanel.add(backButton);

			stepButton=new JButton("Step");
			stepButton.setActionCommand("step");
			stepButton.addActionListener(this);
			topPanel.add(stepButton);

			steptoText=new JTextField(10);
			steptoText.setText("RET");
			topPanel.add(steptoText);

			steptoButton=new JButton("Step To");
			steptoButton.setActionCommand("stepto");
			steptoButton.addActionListener(this);
			topPanel.add(steptoButton);

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

			String dev=(String) cpuDevices.getSelectedItem();
			CPUDevice d=(CPUDevice)sim.getDevice(dev);
			Disassembler dis=sim.getDisassembler(d);
			
			if(cmd.contentEquals("step")) {
				currentStepHistory=-1;
				sim.doManualStep();
				// wait for it
				while(true) {
					Thread.yield();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					if(sim.getManualStep()==false)break;
				}
			}else if(cmd.contentEquals("stepto")) {
				currentStepHistory=-1;
				String text=steptoText.getText().toUpperCase();
				
				if(dis!=null) {
					while(true) {
						sim.doManualStep();
						// wait for it
						while(true) {
							Thread.yield();
							/*try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}*/
							if(sim.getManualStep()==false)break;
						}
						
						String disText=dis.disassemble(d.getCPUState().getRegister("PC").getValue(), 10, d.getMemoryBus());
						int pos=disText.indexOf('\n');
						if(pos>0)disText=disText.substring(0,pos);
						pos=disText.indexOf(text);
						if(pos!=-1)break;
						
					}
				}
			}else if(cmd.contentEquals("view")) {
				currentStepHistory=-1;
			}else if(cmd.contentEquals("back")) {
				if(currentStepHistory==-1)currentStepHistory=stepHistory.size()-2;
				else currentStepHistory--;
				if(currentStepHistory<-1)currentStepHistory=-1;
			}
			
			StringBuffer buff=new StringBuffer();
			if(dis==null) {
				inspectArea.setText(d.getCPUState().getStateString());
			}else {
				CPUState state=d.getCPUState();
				long pc=state.getRegister("PC").getValue();
				if(currentStepHistory==-1) {
					stepHistory.add(state.clone());
				}else {
					buff.append(state.getStateString());
					buff.append("\n\n");
					buff.append("HISTORY:\n");
					state=stepHistory.get(currentStepHistory);
					pc=state.getRegister("PC").getValue();
				}
				
				buff.append(state.getStateString());
				buff.append("\n\n");
				buff.append(dis.disassemble(pc, 500, d.getMemoryBus() ));

				inspectArea.setText(buff.toString());
				inspectArea.setCaretPosition(0);
			}
		}
		
	}


	@SuppressWarnings("serial")
	class InspectDiskWindow extends JFrame implements ActionListener{
		
		private JButton viewButton;
		private JTextArea inspectArea;
		private JTextField textHead;
		private JTextField textTrack;
		private JTextField textSector;
		private JTextField textNumSectors;
		private JButton writeButton;
		
		public InspectDiskWindow() {
			super("Disk Inspector");
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
			
			diskDevices=new JComboBox<>();
			topPanel.add(diskDevices);
			
			JLabel label=new JLabel("Head:");
			topPanel.add(label);
			textHead=new JTextField(5);
			textHead.setText("0");
			topPanel.add(textHead);

			label=new JLabel("Track:");
			topPanel.add(label);
			textTrack=new JTextField(5);
			textTrack.setText("0");
			topPanel.add(textTrack);

			label=new JLabel("Sector:");
			topPanel.add(label);
			textSector=new JTextField(5);
			textSector.setText("0");
			topPanel.add(textSector);
			
			label=new JLabel("Num Sectors:");
			topPanel.add(label);
			textNumSectors=new JTextField(5);
			textNumSectors.setText("1");
			topPanel.add(textNumSectors);
						
			viewButton=new JButton("View");
			viewButton.setActionCommand("view");
			viewButton.addActionListener(this);
			topPanel.add(viewButton);
			
			writeButton=new JButton("Re-Write Image");
			writeButton.setActionCommand("write");
			writeButton.addActionListener(this);
			topPanel.add(writeButton);

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
			String dev=(String) diskDevices.getSelectedItem();
			int pos=dev.lastIndexOf(":");
			String devName=dev.substring(0,pos);
			int devId=Integer.parseInt(dev.substring(pos+1));
			
			Disk dsk=((DiskController)sim.getDevice(devName)).getDrives().get(devId).getDisk();
			
			if(dsk==null) {
				inspectArea.setText("Disk is not loaded");
				return ;
			}
			
			if(cmd.contentEquals("view")) {
				StringBuffer buff=new StringBuffer();
				buff.append(String.format(
						"Heads: %d, Tracks/Head: %d, Sectors/Track: %d, Sector size: %d, Disk size: %d\n\n",
						dsk.getHeads(), dsk.getTracksPerHead(), dsk.getSectorsPerTrack(), dsk.getSectorSize(),
						dsk.getDiskSize()
				));
				
				int head=Integer.parseInt(textHead.getText());
				int track=Integer.parseInt(textTrack.getText());
				int sector=Integer.parseInt(textSector.getText());
				int numSectors=Integer.parseInt(textNumSectors.getText());
				
				DiskSector[] diskSectors=dsk.readSector(head, track, sector, numSectors);
				if(diskSectors!=null) {
					for(DiskSector ds:diskSectors) {
						buff.append(String.format("Head: %d, Track:%d, Sector:%d\n", ds.head,ds.track,ds.sector));
						StringBuffer buffAscii=new StringBuffer();
						int addr;
						int size=ds.data.length;
						for(addr=0;addr<size;addr++) {
							if(addr%16==0) {
								buff.append(String.format("%06X",addr));
								buff.append(" | ");
							}
							
							byte data=ds.data[addr];
							buff.append(String.format("%02X ", data));
							if(data>=32 && data<=255)buffAscii.append(String.valueOf((char)data));
							else buffAscii.append(".");
							
							if(((addr+1)%16) == 0) {
								buff.append("  | ");
								buff.append(buffAscii.toString());
								buffAscii.setLength(0);
								buff.append("\n");
							}
						}
						
						if(buffAscii.length()>0) {
							int n = (int)(16 - (addr%16));
							buff.append(new String(new char[n*3]).replace("\0", " "));
							buff.append("  | ");
							buff.append(buffAscii.toString());
							buffAscii.setLength(0);
							buff.append("\n");
							
						}
					
						buff.append("\n");
					}
				}
				
				
				inspectArea.setText(buff.toString());
				inspectArea.setCaretPosition(0);

			}else if(cmd.contentEquals("write")) {
				inspectArea.setText("Re-Write the disk image\n\nIf this is an overlay disk, the base image will contain all the changes and the OVR image will be deleted.\n");
				dsk.writeCompleteImage();
				inspectArea.setText(inspectArea.getText()+"\nCOMPLETE\n");
			}
		}
		
	}
	

	@SuppressWarnings("serial")
	class InspectCodeWindow extends JFrame implements ActionListener{
		
		private JButton viewButton;
		private JTextArea inspectArea;
		private JTextField textStart;
		private JTextField textSize;
		
		public InspectCodeWindow() {
			super("Disassembler");
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
			
			codeCpuDevices=new JComboBox<>();
			topPanel.add(codeCpuDevices);

			codeMemDevices=new JComboBox<>();
			topPanel.add(codeMemDevices);
			
			JLabel label=new JLabel("Start (hex):");
			topPanel.add(label);
			textStart=new JTextField(5);
			textStart.setText("0000");
			topPanel.add(textStart);
			
			label=new JLabel("Size (hex):");
			topPanel.add(label);
			textSize=new JTextField(5);
			textSize.setText("100");
			topPanel.add(textSize);
			
			viewButton=new JButton("View");
			viewButton.setActionCommand("view");
			viewButton.addActionListener(this);
			topPanel.add(viewButton);
			
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
			if(cmd.contentEquals("view")) {
				String tStart=textStart.getText();
				String tSize=textSize.getText();
				
				long start=Long.parseLong(tStart, 16);
				long size=Long.parseLong(tSize, 16);
				
				String memDevName=(String) codeMemDevices.getSelectedItem();
				GenericDataDevice memDev=(GenericDataDevice)sim.getDevice(memDevName);

				String cpuDevName=(String) codeCpuDevices.getSelectedItem();
				CPUDevice cpuDev=(CPUDevice)sim.getDevice(cpuDevName);
				Disassembler dis=sim.getDisassembler(cpuDev);
				
				StringBuffer buff=new StringBuffer();
				if(dis==null) {
					inspectArea.setText("There is no disassembler for the selected CPU");
				}else {
					buff.append(dis.disassemble(start, size, memDev ));
				}
				
				inspectArea.setText(buff.toString());
				inspectArea.setCaretPosition(0);
				
			}
		}
		
	}
	
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		lock=new Object();
		win=new FrontWindow();
		inspectMemWin=new InspectMemWindow();
		inspectCPUWin=new InspectCPUWindow();
		inspectCodeWin=new InspectCodeWindow();
		inspectDiskWin=new InspectDiskWindow();
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
	}

}
