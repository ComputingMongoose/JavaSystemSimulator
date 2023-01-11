package jss.devices.memory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public void loadHex(Path hexFile, boolean invertNibbles) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(hexFile.toFile()),Charset.forName("UTF8")));
		long startAddress=0;
		for(String line = reader.readLine();line!=null;line=reader.readLine()) {
			// start code
			int pos=line.indexOf(":");
			if(pos<0)continue;
			
			// byte count
			String bc=line.substring(pos+1,pos+3);
			int byteCount=Integer.parseInt(bc,16);
			
			// address, 4 hex
			// TODO implement address read from HEX file
			
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
		reader.close();
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
		}else{
			throw new ConfigurationValueOptionException("initialization_policy",policy);
		}
		
		if(config.contains("load_hex")) {
			boolean invertNibbles=false;
			if(config.contains("invert_nibbles"))
				invertNibbles=(config.getLong("invert_nibbles")==1);
			loadHex(sim.getFilePath(config.getString("load_hex")),invertNibbles);
		}
	}

	public byte[] getMem() {
		return mem;
	}

}
