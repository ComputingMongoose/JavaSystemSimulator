package jss.devices.memory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.simulation.Simulation;

public abstract class AbstractMemoryDevice implements MemoryDevice {

	protected DeviceConfiguration config;
	protected byte[] mem;
	protected Simulation sim;

	public void loadHex(Path hexFile, boolean invertNibbles, int offset) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(hexFile.toFile()),Charset.forName("UTF8")));
		loadHex(reader,invertNibbles,offset);
		reader.close();
	}
	
	public void loadHex(URL hexFile, boolean invertNibbles, int offset) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						hexFile.openStream(),Charset.forName("UTF8")));
		loadHex(reader,invertNibbles,offset);
		reader.close();
	}

	public void loadHex(BufferedReader reader, boolean invertNibbles, int offset) throws IOException {
		long startAddress=0;
		for(String line = reader.readLine();line!=null;line=reader.readLine()) {
			// start code
			int pos=line.indexOf(":");
			if(pos<0)continue;
			
			// byte count
			String bc=line.substring(pos+1,pos+3);
			int byteCount=Integer.parseInt(bc,16);
			
			// address, 4 hex
			String adr=line.substring(pos+3,pos+7);
			startAddress=Integer.parseInt(adr,16)-offset;
			
			// record type
			String rec=line.substring(pos+7,pos+9);
			if(!rec.contentEquals("00"))continue;
			
			for(int i=0;i<byteCount;i++) {
				String data=line.substring(pos+9+2*i,pos+9+2*i+2);
				if(invertNibbles)data=data.substring(1,2)+data.substring(0,1);
				mem[(int) startAddress]=(byte) Integer.parseInt(data,16);
				startAddress++;
			}
		}
	}

	public void loadBin(Path binFile, boolean invertNibbles, int offset) throws IOException {
		InputStream in = new FileInputStream(binFile.toFile());
		loadBin(in,invertNibbles,offset);
		in.close();
	}
	
	public void loadBin(URL binFile, boolean invertNibbles, int offset) throws IOException {
		InputStream in=binFile.openStream();
		loadBin(in,invertNibbles,offset);
		in.close();
	}
	
	public void loadBin(InputStream in, boolean invertNibbles, int offset) throws IOException {
		long startAddress=offset;
		for(int data=in.read();data!=-1;data=in.read()) {
			if(invertNibbles)data=((data>>4)&0x0F)|((data<<4)&0xF0);;
			mem[(int) startAddress]=(byte) data;
			startAddress++;
		}
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim) throws DeviceConfigurationException, ConfigurationValueTypeException {
		this.config=config;
		this.sim=sim;
		mem=new byte[(int) config.getLong("size")];
	}
	
	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException, IOException {
		String policy=config.getString("initialization_policy");
		if(policy.contentEquals("ZERO")) {
			for(int i=0;i<mem.length;i++)mem[i]=0;
		}else if(policy.contentEquals("RANDOM")) {
			for(int i=0;i<mem.length;i++)mem[i]=(byte) Math.round(Math.random()*255);
		}else if(policy.contentEquals("FIXED")) {
			byte fixed=(byte) config.getLong("fixed_value");
			fixed=(byte)(fixed&0xFF);
			for(int i=0;i<mem.length;i++)mem[i]=fixed;
		}else if(policy.contentEquals("VALUES")) {
			String values=config.getString("initialization_values");
			String[] valuesHex=values.split("[ ,]+");
			for(int i=0;i<mem.length && i<valuesHex.length;i++) {
				mem[i]=(byte) (Integer.parseInt(valuesHex[i],16) & 0xFF);
			}
		}else{
			throw new ConfigurationValueOptionException("initialization_policy",policy);
		}
		
		if(config.contains("load_hex")) {
			boolean invertNibbles=false;
			if(config.contains("invert_nibbles"))
				invertNibbles=(config.getLong("invert_nibbles")==1);
			int load_hex_offset=(int)config.getOptLong("load_hex_offset", 0);
			loadHex(sim.getFilePath(config.getString("load_hex")),invertNibbles,load_hex_offset);
		}

		if(config.contains("load_bin")) {
			boolean invertNibbles=false;
			if(config.contains("invert_nibbles"))
				invertNibbles=(config.getLong("invert_nibbles")==1);
			int load_offset=(int)config.getOptLong("load_bin_offset", 0);
			loadBin(sim.getFilePath(config.getString("load_bin")),invertNibbles,load_offset);
		}
	
	}

	public byte[] getMem() {
		return mem;
	}

}
