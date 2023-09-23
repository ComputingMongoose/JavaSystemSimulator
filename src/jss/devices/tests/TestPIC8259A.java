package jss.devices.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.bus.impl.ControlBusBasic;
import jss.devices.impl.PIC_8259A;

public class TestPIC8259A {
	public static void check(long actual, long desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}

	public static void check(boolean actual, boolean desired) throws Exception {
		if(actual!=desired)
			throw new Exception();
	}

	public static void check(byte[] actual, byte[] desired) throws Exception {
		if(actual.length!=desired.length)
			throw new Exception();
		for(int i=0;i<actual.length;i++) {
			if(actual[i]!=desired[i])
				throw new Exception();
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Testing PIC 8259A");
		DeviceConfiguration config=new DeviceConfiguration("","");
		config.set("signals_receive", new ConfigurationValue("S0,S1,S2,S3,S4,S5,S6,S7"));
		config.set("signal_send", new ConfigurationValue("INT"));
		
		PIC_8259A pic=new PIC_8259A();
		pic.configure(config,null);
		pic.initialize();
		
		config=new DeviceConfiguration("","");
		config.set("signals", new ConfigurationValue("INT"));
		ControlBusBasic busCPU=new ControlBusBasic();
		busCPU.configure(config, null);
		busCPU.initialize();
		
		config=new DeviceConfiguration("","");
		config.set("signals", new ConfigurationValue("S0,S1,S2,S3,S4,S5,S6,S7"));
		ControlBusBasic busSignals=new ControlBusBasic();
		busSignals.configure(config, null);
		busSignals.initialize();

		pic.attachToControlBus(busSignals);
		pic.attachToControlBus(busCPU);
		
		
		busSignals.setSignal("S0");
		pic.step();
		check(busCPU.isSignalSet("INT"),false);
		busSignals.clearSignal("S0");
		
		int icw1=0B00010010;
		int icw2=0B00000000;
		int ocw3=0B00001011;
		pic.write(0, icw1);
		
		busSignals.setSignal("S0");
		pic.step();
		check(busCPU.isSignalSet("INT"),false);
		busSignals.clearSignal("S0");

		pic.write(1, icw2);
		busSignals.setSignal("S0");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x00,0x00});
		busSignals.clearSignal("S0");
		busCPU.clearSignal("INT");

		busSignals.setSignal("S1");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x08,0x00});
		busSignals.clearSignal("S1");
		busCPU.clearSignal("INT");

		busSignals.setSignal("S2");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x10,0x00});
		busSignals.clearSignal("S2");
		busCPU.clearSignal("INT");
		
		busSignals.setSignal("S3");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x18,0x00});
		busSignals.clearSignal("S3");
		busCPU.clearSignal("INT");
		
		busSignals.setSignal("S4");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x20,0x00});
		busSignals.clearSignal("S4");
		busCPU.clearSignal("INT");
		
		busSignals.setSignal("S5");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x28,0x00});
		busSignals.clearSignal("S5");
		busCPU.clearSignal("INT");
		
		busSignals.setSignal("S6");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x30,0x00});
		busSignals.clearSignal("S6");
		busCPU.clearSignal("INT");
		
		busSignals.setSignal("S7");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x38,0x00});
		busSignals.clearSignal("S7");
		busCPU.clearSignal("INT");
		
		int poll=0x0C;
		pic.write(0, poll);
		pic.step();
		check(pic.read(0),0);
		
		busSignals.setSignal("S7");
		pic.step();
		check(busCPU.isSignalSet("INT"),false);

		int eoi=0x20;
		pic.write(0, eoi);
		pic.step();
		busSignals.setSignal("S7");
		pic.step();
		check(busCPU.isSignalSet("INT"),true);
		check(busCPU.getSignalData("INT"),new byte[] {(byte)0xCD,0x38,0x00});
		busSignals.clearSignal("S7");
		busCPU.clearSignal("INT");
		
		
		System.out.println("OK");

	}

}
