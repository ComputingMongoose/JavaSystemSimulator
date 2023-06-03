package jss.devices.impl;

import java.io.IOException;
import java.util.ArrayList;
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
import jss.simulation.Simulation;

public class PIT implements GenericDataDevice, GenericExecutionDevice, GenericControlDevice {

	Simulation sim;
	int last_control_word;
	int last_c;
	int last_mode;
	int last_op;
	int num_rw;
	
	int []c_mode=new int[3];
	int []c_counter=new int[3];	
	int []c_init=new int[3];
	int []c_int=new int[3];	
	int []c_running=new int[3];
	
	long last_step;
	
	ControlBus controlBus;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		for(int i=0;i<3;i++) {
			c_int[i]=(int)config.getOptLong("counter_0_int", -1);
		}
		
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		
		this.last_control_word=0;
		this.last_c=0;
		this.last_mode=0;
		this.last_op=0;
		this.num_rw=0;
		this.last_step=0;
		for(int i=0;i<c_mode.length;i++) {
			c_mode[i]=0;
			c_counter[i]=0;
			c_init[i]=0;
			c_running[i]=0;
		}
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		long ret=0;
		switch((int)address) {
		case 0: // Counter 0 Time of Day Clock (normally mode 3), usually mapped at 0x40
			if(last_c==0) 
				ret=readCounter();
			break;
			
		case 1: // Counter 1 RAM Refresh Counter (normally mode 2), usually mapped at 0x41
			if(last_c==1) 
				ret=readCounter();
			break;
			
		case 2: // Counter 2 Cassette and Speaker Functions, usually mapped at 0x42
			if(last_c==2) 
				ret=readCounter();
			break;
			
		case 3: // Mode Control Register, usually mapped at 0x43
			ret=this.last_control_word;
			break;
			
		default:
			sim.writeToCurrentLog(String.format("PIT READ UNKNOWN port [%x]", address));
			break;
		}

		sim.writeToCurrentLog(String.format("PIT read port [%x] ==> [%x]", address,ret));

		return ret;
	}
	
	private long readCounter() {
		switch(last_op) {
		case 0:
			if(num_rw==0) {num_rw=1;return this.c_counter[last_c]&0xFF;}
			num_rw=0;
			return this.c_counter[last_c]>>8;
			
		case 1: 
			return this.c_init[last_c]&0xFF;
			
		case 2: 
			return this.c_init[last_c]>>8;
			
		case 3:
			if(num_rw==0) {
				num_rw=1;
				return this.c_init[last_c]&0xFF;
			}else {
				return this.c_init[last_c]>>8;
			}
		}
		return 0;
	}
	
	private void writeCounter(long value) {
		switch(last_op) {
			case 1: 
				this.c_init[last_c]=(int)value;
				this.c_counter[last_c]=this.c_init[last_c];
				this.c_running[last_c]=1;
				break;
			case 2: 
				this.c_init[last_c]=((int)value)<<8;
				this.c_counter[last_c]=this.c_init[last_c];
				this.c_running[last_c]=1;
				break;
			case 3:
				if(num_rw==0) {
					this.c_init[last_c]=(int)value;
					num_rw=1;
				}else {
					this.c_init[last_c]|=(((int)value)<<8);
					this.c_counter[last_c]=this.c_init[last_c];
					this.c_running[last_c]=1;
				}
				break;
		}
		
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		sim.writeToCurrentLog(String.format("PIT WRITE port [%x] [%x]", address,value));
		
		switch((int)address) {
		case 0: // Counter 0 Time of Day Clock (normally mode 3), usually mapped at 0x40
			if(last_c==0 && last_op!=0)
				writeCounter(value);
			break;
		case 1: // Counter 1 RAM Refresh Counter (normally mode 2), usually mapped at 0x41
			if(last_c==1 && last_op!=0)
				writeCounter(value);
			break;
		case 2: // Counter 2 Cassette and Speaker Functions, usually mapped at 0x42
			if(last_c==0 && last_op!=0)
				writeCounter(value);
			break;
		case 3: // Mode Control Register, usually mapped at 0x43
			last_control_word=(int)value;
			last_c=(last_control_word>>6)&0x3;
			last_op=(last_control_word>>4)&0x3;
			last_mode=(last_control_word>>1)&0x7;
			num_rw=0;
			if(last_c!=3 && last_op!=0) {
				this.c_mode[last_c]=last_mode;
				this.c_running[last_c]=0;
			}
			break;
		default:
			sim.writeToCurrentLog(String.format("PIT WRITE Unknown port [%x] [%x]", address,value));
			break;
		}
	}

	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		long current = System.nanoTime();
		if(this.last_step==0 || current-this.last_step>=3000) { // 1000 => approx 1MHz, should be 1.19318MHz
			this.last_step=current;
			for(int i=0;i<3;i++) {
				if(this.c_running[i]!=0) {
					this.c_counter[i]--;
					this.c_counter[i]&=0xFFFF;
					if(this.c_counter[i]==0) {
						if(i==0) {
							this.controlBus.setSignal("INT");
							this.controlBus.setSignalData("INT", new byte[] {8});
						}
						
						if(this.c_mode[i]==2 || this.c_mode[i]==3)this.c_counter[i]=this.c_init[i];
						else this.c_running[i]=0;
					}
				}
			}
		}
		
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		this.controlBus=bus;
	}
	
}
