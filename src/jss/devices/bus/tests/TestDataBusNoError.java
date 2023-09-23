package jss.devices.bus.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.bus.DataBus;
import jss.devices.bus.impl.DataBusNoError;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryDevice;
import jss.devices.memory.impl.MemoryW8D8;

public class TestDataBusNoError {

	public static void check(long actual, long desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}
	
	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing DataBusNoError");
		DeviceConfiguration config=new DeviceConfiguration("","");
		config.set("size", new ConfigurationValue(16));
		config.set("initialization_policy", new ConfigurationValue("ZERO"));
		
		MemoryDevice mem=new MemoryW8D8();
		mem.configure(config,null);
		mem.initialize();
		
		DataBus bus=new DataBusNoError();
		bus.configure(new DeviceConfiguration("",""),null);
		bus.initialize();
		
		bus.attachDataDevice(mem, 0, 15, 0,"",true);
		bus.attachDataDevice(mem, 100, 115, 100,"",true);
		
		check(bus.read(0),0);
		check(bus.read(50),0);
		check(bus.read(100),0);
		
		for(int i=0;i<16;i++) {
			mem.write(i, i);
			check(bus.read(i),i);
			check(bus.read(100+i),i);
		}
		
		bus.write(110, 0xFF);
		check(bus.read(10),0xFF);
		
		System.out.println("    OK");
	}
	
}
