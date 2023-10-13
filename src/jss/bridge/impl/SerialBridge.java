package jss.bridge.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

import jss.bridge.BridgeCommand;
import jss.bridge.BridgeCommandStatus;
import jss.bridge.BridgeDevType;
import jss.bridge.GenericBridge;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDevice;
import jss.simulation.Simulation;

public class SerialBridge implements GenericBridge, GenericDevice {

	protected DeviceConfiguration config;
	protected Simulation sim;
	
	protected SerialPort externalPort;
	protected InputStream in;
	protected OutputStream out;
	
	protected boolean bridgeOK;
	protected int bridgeVerMaj;
	protected int bridgeVerMin;
	protected int numDevices;
	
	@Override
	public void executeCommand(BridgeCommand cmd) throws IOException {
		if(!bridgeOK) {
			sim.writeToCurrentLog("SerialBridge: The bridge is not operating properly. Not executing command.");
			cmd.setStatus(BridgeCommandStatus.ERROR);
			return ;
		}
		out.write(cmd.getCommandPacket());
		
		int status=in.read();
		cmd.setStatus(status);
		
		int sz1=in.read();
		int sz2=in.read();
		int size=((sz2&0xFF)<<8)|(sz1&0xFF);
		if(size>0) {
			byte[] data=new byte[size];
			for(int i=0;i<data.length;i++) {
				data[i]=(byte) in.read();
			}
			cmd.setResponseData(data);
		}else cmd.setResponseData(null);
	}

	@Override
	public void configure(DeviceConfiguration config, Simulation sim) throws DeviceConfigurationException,
			ConfigurationValueTypeException, IOException, ConfigurationValueOptionException {
		this.config=config;
		this.sim=sim;
		
		bridgeOK=false;
		
		String portName=config.getString("port");
		externalPort=SerialPort.getCommPort(portName);
		externalPort.openPort();
		
		int baudRate=(int)config.getOptLong("baud_rate",9600);
		int dataBits=(int)config.getOptLong("data_bits",8);
		
		int stopBits=SerialPort.ONE_STOP_BIT;
		String stopBitsString=config.getOptString("stop_bits","ONE");
		if(stopBitsString.contentEquals("TWO"))stopBits=SerialPort.TWO_STOP_BITS;
		else if(stopBitsString.contentEquals("ONE_POINT_FIVE"))stopBits=SerialPort.ONE_POINT_FIVE_STOP_BITS;
		
		String parityString=config.getOptString("parity", "NONE");
		int parity=SerialPort.NO_PARITY;
		if(parityString.contentEquals("EVEN"))parity=SerialPort.EVEN_PARITY;
		else if(parityString.contentEquals("ODD"))parity=SerialPort.ODD_PARITY;
		else if(parityString.contentEquals("MARK"))parity=SerialPort.MARK_PARITY;
		else if(parityString.contentEquals("SPACE"))parity=SerialPort.SPACE_PARITY;
		
		externalPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 60*1000, 60*1000);
		externalPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
		externalPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

		in=externalPort.getInputStream();
		out=externalPort.getOutputStream();
		
		sim.writeToCurrentLog("SerialBridge connected. Check bridge version");
		bridgeOK=true;
		BridgeCommand cmd=new BridgeCommand(BridgeDevType.BRIDGE,255,0,null);
		executeCommand(cmd);
		if(cmd.getStatus()!=BridgeCommandStatus.DONE || cmd.getResponseData()==null || cmd.getResponseData().length<6) {
			sim.writeToCurrentLog("SerialBridge: ERROR communicating with the bridge [Invalid response].");
			bridgeOK=false;
		}else {
			// check signature
			if(cmd.getResponseData()[0]!='J' || cmd.getResponseData()[1]!='S' || cmd.getResponseData()[2]!='S') {
				sim.writeToCurrentLog("SerialBridge: ERROR communicating with the bridge [Invalid signature].");
				bridgeOK=false;
			}else {
				bridgeVerMin=cmd.getResponseData()[3];
				bridgeVerMaj=cmd.getResponseData()[4];
				numDevices=cmd.getResponseData()[5];
				sim.writeToCurrentLog(String.format("SerialBridge: Communication OK. Bridge version %d.%d. Number of devices %d.",bridgeVerMaj,bridgeVerMin,numDevices));
			}
		}
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
	}

}
