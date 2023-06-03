package jss.devices.cpu.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.bus.ControlBus;
import jss.devices.bus.DataBus;
import jss.devices.bus.impl.ControlBusBasic;
import jss.devices.bus.impl.DataBusNoError;
import jss.devices.cpu.impl.Intel4040;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.impl.MemoryW4D4;

public class TestIntel4040 {

	public static void check(long actual, long desired) throws Exception {
		if (actual != desired)
			throw new Exception();
	}
	
	static int[] program=new int[] {
			0x22,0xFE, // FIM 1, 0xFE
			0x24,0xFE, // FIM 2, 0xFE
			
			0xD1,      // LDM 1
			0xF8,      // DAC
			
			0xD2,      // LDM 2
			0x20, 0x02,// FIM 0,2
			0xFA,      // STC
			0x91,      // SUB 1
	};

	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing Intel4040");

		// Memory
		DeviceConfiguration configROM = new DeviceConfiguration("","");
		configROM.set("size", new ConfigurationValue(16));
		configROM.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW4D4 rom = new MemoryW4D4();
		rom.configure(configROM,null);
		rom.initialize();

		for(int i=0;i<program.length;i++) {
			rom.write(2*i, (program[i]>>4)&0x0F);
			rom.write(2*i+1, (program[i])&0x0F);
		}

		// Memory BUS
		DeviceConfiguration configMemoryBus = new DeviceConfiguration("","");
		DataBus memoryBus=new DataBusNoError();
		memoryBus.configure(configMemoryBus,null);
		memoryBus.initialize();
		memoryBus.attachDataDevice(rom, 0, 32, 0);
		
		// Control BUS
		DeviceConfiguration configControlBus = new DeviceConfiguration("","");
		configControlBus.set("signals", new ConfigurationValue("TEST,INT"));
		ControlBus controlBus=new ControlBusBasic();
		controlBus.configure(configControlBus,null);
		controlBus.initialize();
		
		// CPU
		DeviceConfiguration configCPU = new DeviceConfiguration("","");
		Intel4040 cpu = new Intel4040();
		cpu.configure(configCPU,null);
		cpu.initialize();
		cpu.attachToDataBus(memoryBus);
		cpu.attachToControlBus(controlBus);
		
		check(cpu.getRegisters()[0][2],0);
		check(cpu.getRegisters()[0][3],0);
		
		cpu.step();

		check(cpu.getRegisters()[0][2],0xF);
		check(cpu.getRegisters()[0][3],0xE);
		
		cpu.step();

		check(cpu.getRegisters()[0][4],0xF);
		check(cpu.getRegisters()[0][5],0xE);

		cpu.step();
		
		check(cpu.getAcc(),1);

		cpu.step();
		
		check(cpu.getAcc(),0);
		check(cpu.getFlag_carry(),1);
		
		cpu.step();
		
		check(cpu.getAcc(),2);
		
		cpu.step();
		check(cpu.getRegisters()[0][1],2);
		
		cpu.step();
		check(cpu.getFlag_carry(),1);
		
		cpu.step();
		check(cpu.getAcc(),0x0F);

		System.out.println("    OK");
	}

}
