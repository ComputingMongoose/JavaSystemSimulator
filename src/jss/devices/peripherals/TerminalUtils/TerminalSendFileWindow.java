package jss.devices.peripherals.TerminalUtils;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

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
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class TerminalSendFileWindow extends JFrame implements ActionListener{
	
	private JButton browseButton;
	private JComboBox<String> sendOption;
	private JCheckBox sendEOF;
	private JTextArea inspectArea;
	private JTextField hexStart;
	private JLabel labelHexStart;
	private JButton sendButton;
	
	private File sendFile;
	private TerminalStatus status;
	
	public TerminalSendFileWindow(TerminalStatus status) {
		super("Send File");
		
		this.status=status;
		this.sendFile=null;
		
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
			FileDialog fd=new FileDialog(this,"Select file",FileDialog.LOAD);
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
				    		byte[] buff=new byte[4096];
				    		while(in.available()>0) {
				    			int len=in.read(buff);
				    			if(len>0)status.transmitChars(buff,len);
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

								for(int i=0;i<dataToSend.length();i++) {
									status.transmitChar(dataToSend.charAt(i));
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
				    		status.transmitChar((char)26); // ctrl+z 0x1A
				    	}

				    	inspectArea.setText(inspectArea.getText()+"\n"+"Waiting for buffer to become empty...\n");
				    	while(true) {
				    		if(!status.isTransmitDataAvailable())break;
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
