package jss.devices.impl;

import java.io.IOException;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDataDevice;
import jss.devices.GenericExecutionDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.MemoryOperation;
import jss.simulation.Simulation;

public class PIC_8259A implements GenericDataDevice, GenericExecutionDevice, GenericControlDevice {

	Simulation sim;
	ControlBus controlBusReceive;
	ControlBus controlBusSend;
	String[] signalsReceive;
	String signalSend;
	int irr; // Interrupt Request Register
	int imr; // Interrupt Mask Register
	int isr; // Interrupt Service Register
	int isr_n; // what level is in isr ?
	
	/* 
	 * status =
	 *   0 - waiting for ICW1
	 *   1 - waiting for ICW2
	 *   2 - waiting for ICW3
	 *   3 - waiting for ICW4
	 *   4 - normal
	 */
	int status;
	
	public static final int STATUS_WAIT_ICW1=0;
	public static final int STATUS_WAIT_ICW2=1;
	public static final int STATUS_WAIT_ICW3=2;
	public static final int STATUS_WAIT_ICW4=3;
	public static final int STATUS_NORMAL=4;
	
	int priority[];
	
	int ibyte1[]; // interrupt vector byte 1
	int ibyte2[]; // interrupt vector byte 2
	
	boolean needsICW4;
	boolean cascade;
	int call_address_interval;
	boolean mode8086;
	boolean autoEOI;
	
	int icw1;
	int icw2;
	int icw3;
	int icw4;
	
	int ris;
	
	boolean poll;
	
	byte[][] jump_table;
	
	private static final int []masks=new int[] {1,2,4,8,0x10,0x20,0x40,0x80};
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		String sSignalsReceive=config.getString("signals_receive");
		signalsReceive=sSignalsReceive.split("[,]");
		if(signalsReceive.length>8) {
			throw new DeviceConfigurationException("Too many signals; this device only supports 8 received signals");
		}
		if(signalsReceive.length!=8) {
			throw new DeviceConfigurationException("Too few signals; please specify exactly 8 received signals");
		}
		
		signalSend=config.getString("signal_send");
		
		priority=new int[signalsReceive.length];
		ibyte1=new int[signalsReceive.length];
		ibyte2=new int[signalsReceive.length];
		
		this.jump_table=new byte[8][];
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		
		irr=0;
		imr=0;
		isr=0;
		isr_n=-1;
		status=0;
		ris=0;
		
		for(int i=0;i<priority.length;i++)priority[i]=i;
		for(int i=0;i<ibyte1.length;i++)ibyte1[i]=0;
		for(int i=0;i<ibyte2.length;i++)ibyte2[i]=0;
		
		needsICW4=false;
		cascade=false;
		call_address_interval=8;
		icw1=0;
		icw2=0;
		icw3=0;
		icw4=0;
		mode8086=false;
		autoEOI=false;
		
		poll=false;
	}

	@Override
	public long read(long address) throws MemoryAccessException {

		switch((int)address) {
		case 0:
			if(poll) {
				int n=7;
				for(n=7;n>=0;n--) {
					if((irr & masks[n])!=0)break;
				}
				if(n<0)return 0;
				return (0x80 | n);
			}
			if(ris==0)return irr;
			else return isr;
			
		case 1:
			return imr;
		default:
			throw new MemoryAccessException(address,MemoryOperation.READ);
		}
		
		
	}
	
	public void setStatusNormal() {
		// compute jump tables
		for(int i=0;i<8;i++) {
			if(this.mode8086) {
				jump_table[i]=new byte[1];
				jump_table[i][0]=(byte)((icw2&0xF8)|i);
			}else {
				jump_table[i]=new byte[3];
				jump_table[i][0]=(byte)0xCD; // CALL
				if(call_address_interval==4)
					jump_table[i][1]=(byte)((icw1&0xE0)|(i*call_address_interval));
				else
					jump_table[i][1]=(byte)((icw1&0xC0)|(i*call_address_interval));
				jump_table[i][2]=(byte)icw2;
			}
		}
		
		status=STATUS_NORMAL;
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		switch((int)address) {
		case 0:
			if((value & 0x10) != 0) { // ICW1
				imr=0;
				autoEOI=false;
				mode8086=false;
				icw1=(int)value;
				status=STATUS_WAIT_ICW2; // will wait for ICW2
				needsICW4= (value&1)!=0;
				cascade= (value&2)==0;
				call_address_interval= ((value&4)==0)?(8):(4);
				// LTIM is ignored
			}else if(status==STATUS_NORMAL && ((value & 0x18)==0)) { // OCW2
				if((value&0x20)!=0) { // EOI
					if((value & 0x40)!=0) { // specific EOI
						int n=(int)(value&0x07);
						if(isr_n==n) {isr_n=-1;isr=0;}
						if((irr&masks[n])!=0)irr=irr^masks[n];
					}else {
						isr=0;isr_n=-1;irr=0;
					}
				}
			}else if(status==STATUS_NORMAL && ((value & 0x18)==0x08)) { // OCW3
				if((value & 0x2)!=0) {
					ris=(int)(value & 0x1);
				}
				poll=(value & 0x4)!=0;
			}
			break;
		case 1:
			if(status==STATUS_WAIT_ICW2) {
				icw2=(int)value;
				if(cascade)status=STATUS_WAIT_ICW3;
				else if (needsICW4)status=STATUS_WAIT_ICW4;
				else setStatusNormal();
			}else if(status==STATUS_WAIT_ICW3) {
				icw3=(int)value;
				if (needsICW4)status=STATUS_WAIT_ICW4;
				else setStatusNormal();
			}else if(status==STATUS_WAIT_ICW4) {
				icw4=(int)value;
				mode8086=(icw4&1)!=0;
				autoEOI=(icw4&2)!=0;
				//sfnm
				//buff
				setStatusNormal();
			}else if(status==STATUS_NORMAL) { // OCW1
				imr=(int)value;
			}
			break;
		default:
			throw new MemoryAccessException(address,MemoryOperation.WRITE);
		}
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		if(status!=STATUS_NORMAL)return ;
		
		int n=0;
		
		if(autoEOI) {irr=0;isr=0;isr_n=-1;}
		
		// SET IRR
		for(String s:signalsReceive) {
			if(((imr & masks[n])==0) && controlBusReceive.isSignalSet(s)) {
				irr|=masks[n];
			} //else irr &= (masks[n]^0xFF); // negate the mask
			n++;
		}
		
		// PRIORITY RESOLVER
		for(n=7;n>isr_n;n--) {
			if((irr&masks[n])!=0 && ((imr & masks[n])==0)) {
				isr=masks[n];
				irr^=masks[n];
				isr_n=n;
				// TRIGGER
				controlBusSend.setSignal(this.signalSend);
				controlBusSend.setSignalData(this.signalSend, this.jump_table[isr_n]);
				break;
			}
		}
		
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		if(this.controlBusReceive==null)this.controlBusReceive=bus;
		else this.controlBusSend=bus;
	}
	
}
