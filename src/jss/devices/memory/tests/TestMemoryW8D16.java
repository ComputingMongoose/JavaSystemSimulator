package jss.devices.memory.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryDevice;
import jss.devices.memory.impl.MemoryW8D16;

public class TestMemoryW8D16 {

	public static void check(long actual, long desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}
	
	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing MemoryW8D16");
		DeviceConfiguration config=new DeviceConfiguration();
		config.set("size", new ConfigurationValue(16));
		config.set("initialization_policy", new ConfigurationValue("ZERO"));
		
		MemoryDevice mem=new MemoryW8D16();
		mem.configure(config,null);
		mem.initialize();
		
		check(mem.read(0),0);
		
		for(int i=0;i<15;i++) {
			mem.write(i, i);
			check(mem.read(i),i);
		}
		
		// re-initialize with 0
		mem.initialize();

		mem.write(0, 0x0102);
		check(mem.read(0),0x0102);
		check(mem.read(1),0x01);
		
		System.out.println("    OK");
	}
	
}
