package jss.bridge.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class TestSerialPort {

	public static void main(String args[]) throws IOException {
		SerialPort[] ports=SerialPort.getCommPorts();
		System.out.println("PORTS:");
		SerialPort externalPort=null;
		for(SerialPort p:ports) {
			//externalPort=p;
			System.out.println("Port:");
			System.out.println("    "+p.getDescriptivePortName());
			System.out.println("    "+p.getPortDescription());
			System.out.println("    "+p.getPortLocation());
			System.out.println("    "+p.getSystemPortName());
			System.out.println("    "+p.getSystemPortPath());
		}
		
		externalPort=SerialPort.getCommPort("COM4");
		int baudRate=38400;
		int dataBits=8;
		int stopBits=SerialPort.ONE_STOP_BIT;// .TWO_STOP_BITS;
		int parity=SerialPort.NO_PARITY;// .EVEN_PARITY;

		externalPort.openPort();

		externalPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 5*1000, 5*1000);
		externalPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
		externalPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		
		
		InputStream in=externalPort.getInputStream();
		OutputStream out=externalPort.getOutputStream();
		
		//out.write('A');
		//out.write('B');
		
		//byte []buff=new byte[1];
		//externalPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 60*1000, 60*1000);
		//int n=externalPort.readBytes(buff, 1);
		//System.out.println("read= "+n+" bytes");
		
		char c=(char) in.read();
		
		
		//in.close();
		//out.close();
		
	}
	
}
