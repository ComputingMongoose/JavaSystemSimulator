package jss.devices.memory.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryDevice;
import jss.devices.memory.impl.MemoryW8D64;

public class TestMemoryW8D64 {

	public static void check(long actual, long desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}
	
	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing MemoryW8D64");
		DeviceConfiguration config=new DeviceConfiguration();
		config.set("size", new ConfigurationValue(16));
		config.set("initialization_policy", new ConfigurationValue("ZERO"));
		
		MemoryDevice mem=new MemoryW8D64();
		mem.configure(config,null);
		mem.initialize();
		
		check(mem.read(0),0);
		
		for(int i=0;i<9;i++) {
			mem.write(i, i);
			check(mem.read(i),i);
		}
		
		// re-initialize with 0
		mem.initialize();

		mem.write(0, 0x0102030405060708L);
		check(mem.read(0),0x0102030405060708L);
		check(mem.read(1),0x01020304050607L);
		check(mem.read(2),0x010203040506L);
		check(mem.read(3),0x0102030405L);
		check(mem.read(4),0x01020304L);
		check(mem.read(5),0x010203L);
		check(mem.read(6),0x0102L);
		check(mem.read(7),0x01L);
		check(mem.read(8),0);
		
		System.out.println("    OK");
	}
	
}
