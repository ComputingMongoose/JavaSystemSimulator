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
	
	int transmit_bit_number;
	int receive_bit_number;
	
	int bit_send_complement;
	int bit_transmit_empty;
	//int bit_transmit_start;
	int receive_ignore_bit_7;
	int transmit_set_bit_7;
	
	int[] map_send;
	int[] map_receive;
	
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
		
		map_send=new int[256];
		map_receive=new int[256];
		for(int i=0;i<256;i++) {
			map_send[i]=i;
			map_receive[i]=i;
		}
		
		uart=(int) config.getOptLong("uart", 0);
		uart_receive_complement=(int) config.getOptLong("uart_receive_complement", 0);
		uart_send_complement=(int) config.getOptLong("uart_send_complement", 0);

		transmit_bit_number=(int) config.getOptLong("transmit_bit_number", 0);
		receive_bit_number=(int) config.getOptLong("receive_bit_number", 0);
		
		bit_send_complement=(int) config.getOptLong("bit_send_complement", 1);
		bit_transmit_empty=(int) config.getOptLong("bit_transmit_empty", 0);
		//bit_transmit_start=(int) config.getOptLong("bit_transmit_start", 1);
		
		String map_codes_from=config.getOptString("map_codes_from", null);
		String map_codes_to=config.getOptString("map_codes_to", null);
		if(map_codes_from!=null && map_codes_to!=null) {
			String[] map_from=map_codes_from.split("[,]");
			String[] map_to=map_codes_to.split("[,]");
			for(int i=0;i<map_from.length;i++) {
				map_send[Integer.parseInt(map_from[i],16)]=Integer.parseInt(map_to[i],16);
				map_receive[Integer.parseInt(map_to[i],16)]=Integer.parseInt(map_from[i],16);
			}
		}

		receive_ignore_bit_7=(int) config.getOptLong("receive_ignore_bit_7", 0);
		transmit_set_bit_7=(int) config.getOptLong("transmit_set_bit_7", 0);
	
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
				if(transmit.isEmpty()) {
					
					if(uart==1)return 0;
					else return bit_transmit_empty<<transmit_bit_number;
				}
			}
			
			if(uart==1) {
				int data=map_send[(int)transmit.get(0).charAt(0)];
				transmit.remove(0);
				if(uart_send_complement==1)data^=0xFF;
				return data;
			}else {
				transmit_current_bit++;
				
				if(transmit_current_bit==1) {
					return (bit_transmit_empty^0x1)<<transmit_bit_number;
				}
				
				if(transmit_current_bit>=2 && transmit_current_bit<=9) {
					if(transmit_set_bit_7==1 && transmit_current_bit==9) {
						return 1<<transmit_bit_number;//(1^bit_send_complement)<<transmit_bit_number; // complement						
					}
					return ((((int)(map_send[transmit.get(0).charAt(0)])>>(transmit_current_bit-2))&0x1)^bit_send_complement)<<transmit_bit_number; // complement
				}
				
				if(transmit_current_bit==10)return (bit_send_complement^0x1)<<transmit_bit_number;
				
				if(transmit_current_bit==11) {
					transmit_current_bit=0;
					transmit.remove(0);
				}
				return (bit_send_complement^0x1)<<transmit_bit_number;
			}
		}else { // STATUS PORT (address=1)
			int status=0x00;
			if(transmit.isEmpty())status|=1; // no data available
			
			return status;
		}
	}
	
	@Override
	public void write(long address, long value) throws MemoryAccessException {
		if(address==0) { // data port
			if(uart==1) {
				if(uart_receive_complement==1)value^=0xFF;
				if(receive_ignore_bit_7==1)value=value & 0x7F;
				writeData(map_receive[(int) value]);
			}else {
				int bit=(int) ((value>>receive_bit_number)&0x1);
				receive_current_bit++;
				// 1=START, 2,3,4,5,6,7,8,9 = DATA, 10,11=STOP (process only 10)
				if(receive_current_bit==1) { // check for START
					if(bit!=0)receive_current_bit=0; // not START
					return ;
				}
				
				if(receive_current_bit==10) {
					receive_current_bit=0;
					if(receive_ignore_bit_7==1)receive=receive & 0x7F;
					writeData(map_receive[receive]);
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
