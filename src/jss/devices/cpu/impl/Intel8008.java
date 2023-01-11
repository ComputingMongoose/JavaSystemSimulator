package jss.devices.cpu.impl;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.GenericControlDevice;
import jss.devices.GenericDataAccessDevice;
import jss.devices.bus.ControlBus;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.bus.DataBus;
import jss.devices.cpu.CPUDevice;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;

public class Intel8008 implements CPUDevice, GenericControlDevice, GenericDataAccessDevice {

	private DataBus memoryBus=null;
	private DataBus ioBus=null;
	private ControlBus controlBus=null;
	
	// address 14-bit, for 16K memory

	// Control flags: INT, S0, S1, S2
	// INT data will contain the instruction
	
	
	int []stack;
	int stack_ptr; // stack[stack_ptr]=PC
	int []regs;
	
	int flag_zero;
	int flag_carry;
	int flag_parity;
	int flag_sign;
	
	int instr;
	
	boolean flag_halt;
	
	int last_pc;
	
	public static final int REG_A=0;
	public static final int REG_B=1;
	public static final int REG_C=2;
	public static final int REG_D=3;
	public static final int REG_E=4;
	public static final int REG_H=5;
	public static final int REG_L=6;
	
	int []parity_map;
	
	public Intel8008() {
		stack=new int[8];
		regs=new int[7];
		parity_map=new int[256];
	}
	
	@Override
	public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException {
		for(int i=0;i<256;i++) {
			parity_map[i]=
				((   (i&0x1)+
					((i>>2)&0x1)+
					((i>>3)&0x1)+
					((i>>4)&0x1)+
					((i>>5)&0x1)+
					((i>>6)&0x1)+
					((i>>7)&0x1)
				)&0x1)^0x1;
					
		}
	}

	@Override
	public void initialize()
			throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		stack_ptr=0;
		for(int i=0;i<stack.length;i++)stack[i]=0;
		for(int i=0;i<regs.length;i++)regs[i]=0;
		flag_halt=true;
		flag_zero=1;
		flag_carry=0;
		flag_parity=0;
		flag_sign=0;
		last_pc=0;
		instr=0;
	}

	@Override
	public void attachToDataBus(DataBus bus) {
		if(memoryBus==null)memoryBus=bus;
		else if(ioBus==null)ioBus=bus;
	}

	@Override
	public void attachToControlBus(ControlBus bus) {
		controlBus=bus;
	}


	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		
		byte[] int_instr=null;
		
		if(controlBus.isSignalSet("INT")) {
			int_instr=controlBus.getSignalData("INT");
			flag_halt=false;
		}
		
		if(flag_halt) {
			return ;
		}
		
		instr=0;
		if(int_instr!=null) {
			stack_ptr++;
			instr=int_instr[0];
		}else {
			instr=(int) memoryBus.read(stack[stack_ptr]);
			last_pc=stack[stack_ptr];
			stack[stack_ptr]++;
		}
		
		int c1=instr & 0xC0; // D76
		int c2=(instr & 0x38)>>3; // D53
		int c3=instr & 0x07; // D20
		
		int t;
		int adr;
		
		switch(c1) {
		case 0x00: // 00 xxx xxx
			switch(c3) { // 00 xxx 000
			case 0x00:
				switch(c2) {
				case 0x00: // HLT
					flag_halt=true;
					break;
				case 0x07: // ?????
					throw new CPUInvalidOpcodeException(new long[] {instr});
				default: // INR
					regs[c2]=(regs[c2]+1)&0xFF;
					if(regs[c2]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[c2]];
					flag_sign=regs[c2]>>7;
					break;
				}
				break;
			case 0x01: // 00 xxx 001
				switch(c2) {
				case 0x00: // HLT
					flag_halt=true;
					break;
				case 0x07: // ?????
					throw new CPUInvalidOpcodeException(new long[] {instr});
				default: // DECR
					regs[c2]=(regs[c2]-1)&0xFF;
					if(regs[c2]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[c2]];
					flag_sign=regs[c2]>>7;
					break;
				}
				break;
			case 0x02: // 00 xxx 010
				switch(c2) {
				case 0x00: // RLC
					regs[REG_A]=((regs[REG_A]<<1)&0xFF) | (regs[REG_A]>>7);
					break;
				case 0x01: // RRC
					regs[REG_A]=(regs[REG_A]>>1) | ((regs[REG_A]<<7)&0xFF);
					break;
				case 0x02: // RAL
					t=regs[REG_A]>>7;
					regs[REG_A]=((regs[REG_A]<<1)&0xFF) | flag_carry;
					flag_carry=t;
					break;
				case 0x03: // RAR
					t=regs[REG_A]&0x01;
					regs[REG_A]=(regs[REG_A]>>1) | (flag_carry<<7);
					flag_carry=t;
					break;
				default:
					throw new CPUInvalidOpcodeException(new long[] {instr});
				}
				break;
			case 0x03: // 00 xxx 011
				switch(c2) {
				case 0: // RNC
					if(flag_carry==0) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 1: // RNZ
					if(flag_zero==0) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 2: // RP 
					if(flag_sign==0) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 3: // RPO
					if(flag_parity==0) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 4: // RC
					if(flag_carry==1) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 5: // RZ
					if(flag_zero==1) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 6: // RM
					if(flag_sign==1) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				case 7: // RPE
					if(flag_parity==1) {stack_ptr--; if(stack_ptr<0)stack_ptr=stack.length-1;}
					break;
				}
				break;
			case 0x04: // 00 xxx 100
				switch(c2) {
				case 0x00: // ADI
					if(int_instr==null) {
						t=(int) memoryBus.read(stack[stack_ptr]);
						stack[stack_ptr]++;
					}else t=int_instr[1];
					regs[REG_A]+=t;
					flag_carry=regs[REG_A]>>8;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x01: // ACI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]+=(t+flag_carry);
					flag_carry=regs[REG_A]>>8;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x02: // SUI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]-=t;
					flag_carry=(regs[REG_A]>>8)&0x1;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x03: // SBI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]-=(t+flag_carry);
					flag_carry=(regs[REG_A]>>8)&0x1;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x04: // ANI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]&=t;
					flag_carry=(regs[REG_A]>>8)&0x1;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x05: // XRI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]^=t;
					flag_carry=(regs[REG_A]>>8)&0x1;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x06: // ORI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					regs[REG_A]|=t;
					flag_carry=(regs[REG_A]>>8)&0x1;
					regs[REG_A]&=0xFF;
					if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[regs[REG_A]];
					flag_sign=regs[REG_A]>>7;
					break;
				case 0x07: // CPI
					t=(int) memoryBus.read(stack[stack_ptr]);
					stack[stack_ptr]++;
					t=regs[REG_A]-t;
					flag_carry=(t>>8)&0x1;
					t&=0xFF;
					if(t==0)flag_zero=1; else flag_zero=0;
					flag_parity=parity_map[t];
					flag_sign=t>>7;
					break;
				}
				break;
				
			case 0x05: // 00 xxx 101  RST
				t=instr&0x38;
				stack_ptr++; if(stack_ptr>=stack.length)stack_ptr=0;
				stack[stack_ptr]=t;
				break;
				
			case 0x06: // 00 xxx 110 MVI
				t=(int) memoryBus.read(stack[stack_ptr]);
				stack[stack_ptr]++;
				if(c2==0x07) {
					memoryBus.write((regs[REG_H]<<8)|regs[REG_L], t);
				}else {
					regs[c2]=t;
				}
				break;
				
			case 0x07: // 00 xxx 111 RET (don't care about xxx)
				stack_ptr--;
				if(stack_ptr<0)stack_ptr=stack.length-1;
				break;
			}
			break;
			
		case 0x40: // 01 xxx xxx
			if((instr&1)==1) { // IN/OUT
				if((c2&0x6)==0) { // IN
					regs[REG_A]=(int) ioBus.read((instr>>1)&0x07);
				}else { // OUT
					ioBus.write((instr>>1)&0x1F, regs[REG_A]);
				}
			}else { // JMP, ... CALL
				t=(int) memoryBus.read(stack[stack_ptr]);
				stack[stack_ptr]++;
				adr=(int) memoryBus.read(stack[stack_ptr]);
				stack[stack_ptr]++;
				adr=((adr&0x3F)<<8)|t;
				
				switch(c3) {
				case 0: // JNC,....,JC,...
					switch(c2) {
					case 0: // JNC
						if(flag_carry==0)stack[stack_ptr]=adr;
						break;
					case 1: // JNZ
						if(flag_zero==0)stack[stack_ptr]=adr;
						break;
					case 2: // JP
						if(flag_sign==0)stack[stack_ptr]=adr;
						break;
					case 3: // JPO
						if(flag_parity==0)stack[stack_ptr]=adr;
						break;
					case 4: // JC
						if(flag_carry==1)stack[stack_ptr]=adr;
						break;
					case 5: // JZ
						if(flag_zero==1)stack[stack_ptr]=adr;
						break;
					case 6: // JM
						if(flag_sign==1)stack[stack_ptr]=adr;
						break;
					case 7: // JPE
						if(flag_parity==1)stack[stack_ptr]=adr;
						break;
					}
					break;

				case 2: // CNC,....,CC....
					switch(c2) {
					case 0: // CNC
						if(flag_carry==0) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 1: // CNZ
						if(flag_zero==0) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 2: // CP
						if(flag_sign==0) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 3: // CPO
						if(flag_parity==0) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 4: // CC
						if(flag_carry==1) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 5: // CZ
						if(flag_zero==1) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 6: // CM
						if(flag_sign==1) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					case 7: // CPE
						if(flag_parity==1) {stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;}
						break;
					}
					break;
					
				case 4: // JMP 01 xxx 100 => don't care about xxx
					stack[stack_ptr]=adr;
					break;
					
				case 6: // CALL 01 xxx 110 => don't care about xxx
					stack_ptr++; if(stack_ptr>=stack.length) {stack_ptr=0;} stack[stack_ptr]=adr;
					break;
				}
			}
			break;
			
		case 0x80:
			switch(c2) {
			case 0x00: // ADD
				if(c3==0x7)
					regs[REG_A]+=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					regs[REG_A]+=regs[c3];
				flag_carry=regs[REG_A]>>8;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
				
			case 0x01: // ADC
				if(c3==0x7)
					regs[REG_A]+=((int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L])+flag_carry);
				else
					regs[REG_A]+=(regs[c3]+flag_carry);
				flag_carry=regs[REG_A]>>8;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
				
			case 0x02: // SUB
				if(c3==0x7)
					regs[REG_A]-=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					regs[REG_A]-=regs[c3];
				flag_carry=(regs[REG_A]>>8)&0x1;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
				
			case 0x03: // SBB
				if(c3==0x7)
					regs[REG_A]-=((int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L])+flag_carry);
				else
					regs[REG_A]-=(regs[c3]+flag_carry);
				flag_carry=(regs[REG_A]>>8)&0x1;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
				
			case 0x04: // ANA
				if(c3==0x7)
					regs[REG_A]&=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					regs[REG_A]&=regs[c3];
				flag_carry=(regs[REG_A]>>8)&0x1;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;

			case 0x05: // XRA
				if(c3==0x7)
					regs[REG_A]^=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					regs[REG_A]^=regs[c3];
				flag_carry=(regs[REG_A]>>8)&0x1;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
			
			case 0x06: // ORA
				if(c3==0x7)
					regs[REG_A]|=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					regs[REG_A]|=regs[c3];
				flag_carry=(regs[REG_A]>>8)&0x1;
				regs[REG_A]&=0xFF;
				if(regs[REG_A]==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[regs[REG_A]];
				flag_sign=regs[REG_A]>>7;
				break;
				
			case 0x07: // CMP
				if(c3==0x7)
					t=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				else
					t=regs[c3];
				t=regs[REG_A]-t;
				flag_carry=(t>>8)&0x1;
				t&=0xFF;
				if(t==0)flag_zero=1; else flag_zero=0;
				flag_parity=parity_map[t];
				flag_sign=t>>7;
				break;
				
			}
			break;
			
		case 0xC0: // 11 xxx xxx
			if(c2 != 0x07) { 
				if(c3!=0x07) { // MOV R1,R2
					regs[c2]=regs[c3];
				}else { // MOV R1,M
					regs[c2]=(int) memoryBus.read((regs[REG_H]<<8)|regs[REG_L]);
				}
			}else {
				if(c3!=0x07) { // MOV M,R1
					memoryBus.write((regs[REG_H]<<8)|regs[REG_L], regs[c3]);
				}else { // HLT
					flag_halt=true;
				}
			}
			break;
		}
	}

	public int getLast_pc() {
		return last_pc;
	}

	public boolean isFlag_halt() {
		return flag_halt;
	}

	public int getInstr() {
		return instr;
	}

}
