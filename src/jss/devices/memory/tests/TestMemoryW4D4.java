package jss.devices.memory.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryDevice;
import jss.devices.memory.impl.MemoryW4D4;

public class TestMemoryW4D4 {

	public static void check(long actual, long desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}
	
	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing MemoryW4D4");
		DeviceConfiguration config=new DeviceConfiguration();
		config.set("size", new ConfigurationValue(16));
		config.set("initialization_policy", new ConfigurationValue("ZERO"));
		
		MemoryDevice mem=new MemoryW4D4();
		mem.configure(config,null);
		mem.initialize();
		
		check(mem.read(0),0);
		
		for(int i=0;i<16;i++) {
			mem.write(i, i);
		}
		
		for(int i=0;i<16;i++) {
			check(mem.read(i),i);
		}
		
		System.out.println("    OK");
	}
	
}
