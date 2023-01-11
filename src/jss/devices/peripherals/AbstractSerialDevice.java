package jss.devices.peripherals;

import java.io.IOException;
import java.util.ArrayList;
import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericDataDevice;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public abstract class AbstractSerialDevice implements GenericDataDevice {

	Simulation sim;
	
	ArrayList<String> transmit;
	int transmit_current_bit;
	int receive;
	int receive_current_bit;
	
	int uart;
	int uart_receive_complement;
	int uart_send_complement;
	
	Object lock;
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException, IOException {
		
		this.sim=sim;
		
		lock=new Object();
		
		transmit=new ArrayList<>(100);
		transmit_current_bit=0;
		receive=0;
		receive_current_bit=0;
		
		uart=(int) config.getOptLong("uart", 0);
		uart_receive_complement=(int) config.getOptLong("uart_receive_complement", 0);
		uart_send_complement=(int) config.getOptLong("uart_send_complement", 0);
		
	}

	@Override
	public void initialize() throws DeviceConfigurationException, ConfigurationValueTypeException,
			ConfigurationValueOptionException, IOException {
		transmit.clear();
		transmit_current_bit=0;
	}

	@Override
	public long read(long address) throws MemoryAccessException {
		if(address==0) { // DATA PORT
			if(transmit.isEmpty()) {
				getTransmitData();
				if(transmit.isEmpty())return 0;
			}
			
			if(uart==1) {
				int data=(int)transmit.get(0).charAt(0);
				transmit.remove(0);
				if(uart_send_complement==1)data^=0xFF;
				return data;
			}else {
				transmit_current_bit++;
				
				if(transmit_current_bit==1)return 1;
				
				if(transmit_current_bit>=2 && transmit_current_bit<=9)
					return (((int)(transmit.get(0).charAt(0))>>(transmit_current_bit-2))&0x1)^0x1; // complement
				
				if(transmit_current_bit==10)return 0;
				
				if(transmit_current_bit==11) {
					transmit_current_bit=0;
					transmit.remove(0);
				}
			}
		}else { // STATUS PORT (address=1)
			int status=0x00;
			if(transmit.isEmpty())status|=1; // no data available
			
			return status;
		}
		return 0;
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		if(address==0) { // data port
			if(uart==1) {
				if(uart_receive_complement==1)value^=0xFF;
				writeData((int) value);
			}else {
				int bit=(int) (value&0x1);
				receive_current_bit++;
				// 1=START, 2,3,4,5,6,7,8,9 = DATA, 10,11=STOP (process only 10)
				if(receive_current_bit==1) { // check for START
					if(bit!=0)receive_current_bit=0; // not START
					return ;
				}
				
				if(receive_current_bit==10) {
					receive_current_bit=0;
					writeData(receive);
					receive=0;
					return ;
				}
				
				//receive|=(bit<<(9-receive_current_bit));
				receive|=(bit<<(receive_current_bit-2));
			}
		}else if(address==1) {// Control port
			writeControl(address,value);
		}
	}
	
	public abstract void writeControl(long address,long value) throws MemoryAccessException;
	public abstract void writeData(int value) throws MemoryAccessException;
	public abstract void getTransmitData() throws MemoryAccessException;
	
}
