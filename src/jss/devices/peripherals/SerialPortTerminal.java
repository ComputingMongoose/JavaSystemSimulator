package jss.devices.peripherals;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
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
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.fazecast.jSerialComm.SerialPort;

import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

/* 
 * Based on the GenericTerminal class
 * Implements a buffered connection to a real serial port.
 * 
 */
public class SerialPortTerminal extends AbstractSerialDevice {

	OutputStream saveInternal, saveExternal;
	String saveFileInternal, saveFileExternal;
	String externalPortName;
	SerialPort externalPort;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		super.configure(config, sim);
		
		saveFileInternal=config.getOptString("save_file_internal", "");
		if(saveFileInternal!=null && saveFileInternal.length()>0) {
			saveInternal=new FileOutputStream(saveFileInternal);
		}
		
		saveFileExternal=config.getOptString("save_file_external", "");
		if(saveFileExternal!=null && saveFileExternal.length()>0) {
			saveExternal=new FileOutputStream(saveFileExternal);
		}
		
		externalPortName=config.getString("external_port");
		externalPort=SerialPort.getCommPort(externalPortName);
		externalPort.openPort();
		
		int baudRate=(int)config.getOptLong("external_baud_rate",9600);
		int dataBits=(int)config.getOptLong("external_data_bits",8);
		
		int stopBits=SerialPort.ONE_STOP_BIT;
		String stopBitsString=config.getOptString("external_stop_bits","ONE");
		if(stopBitsString.contentEquals("TWO"))stopBits=SerialPort.TWO_STOP_BITS;
		else if(stopBitsString.contentEquals("ONE_POINT_FIVE"))stopBits=SerialPort.ONE_POINT_FIVE_STOP_BITS;
		
		String parityString=config.getOptString("external_parity", "NONE");
		int parity=SerialPort.NO_PARITY;
		if(parityString.contentEquals("EVEN"))parity=SerialPort.EVEN_PARITY;
		else if(parityString.contentEquals("ODD"))parity=SerialPort.ODD_PARITY;
		else if(parityString.contentEquals("MARK"))parity=SerialPort.MARK_PARITY;
		else if(parityString.contentEquals("SPACE"))parity=SerialPort.SPACE_PARITY;
		
		externalPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
		externalPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
		externalPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		
		new Thread() {
			public void run() {
				try {
					while (externalPort.bytesAvailable() == 0)
						Thread.sleep(1);
					byte[] readBuffer = new byte[externalPort.bytesAvailable()];
					int numRead = externalPort.readBytes(readBuffer, readBuffer.length);
					synchronized(lock) {
						for(int i=0;i<numRead;i++) {
							transmit.add(""+(char)(readBuffer[i]));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	@Override
	public void writeData(int data) throws MemoryAccessException {
		if(saveInternal!=null) {
			try {
				saveInternal.write(data);
				saveInternal.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// write data
		externalPort.writeBytes(new byte[] {(byte)data}, 1);
	}

	@Override
	public void writeControl(long address, long value) throws MemoryAccessException {
	}

	@Override
	public void getTransmitData() throws MemoryAccessException {
	}

}
