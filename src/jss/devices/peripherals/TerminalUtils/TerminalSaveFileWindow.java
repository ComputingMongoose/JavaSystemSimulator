package jss.devices.peripherals.TerminalUtils;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class TerminalSaveFileWindow extends JFrame implements ActionListener{
	
	private JButton browseButton;
	private JButton sendButton;
	private JTextArea inspectArea;
	private boolean saving;
	
	private TerminalStatus status;
	
	private File saveFile;
	private OutputStream saveOut;
	
	public TerminalSaveFileWindow(TerminalStatus status) {
		super("Save File");
		
		this.status=status;
		this.saveFile=null;
		
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
	public synchronized void actionPerformed(ActionEvent arg0) {
		String cmd=arg0.getActionCommand();

		if(cmd.contentEquals("browse")) {
			FileDialog fd=new FileDialog(this,"Select file",FileDialog.LOAD);
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

	public synchronized File getSaveFile() {
		return saveFile;
	}

	public synchronized void writeData(int data) {
		if(saveOut!=null) {
			try {
				saveOut.write(data);
			} catch (IOException e) {
				e.printStackTrace();
				status.sim.writeToCurrentLog(String.format("Exception writing to terminal save file: %s",e.getMessage()));
			}
		}
	}
	
}
