package jss.devices.cpu.impl;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfigurationException;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.cpu.AbstractCPUDevice;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.CPUState;
import jss.devices.memory.MemoryAccessException;

public class Intel4040 extends AbstractCPUDevice {

	// address
	//    4-bit MODE | 3-bit RAM | 1-bit ROM | 12-bit address => total 20-bit
	//    MODE: 0x8=DATA | 0x4=PROM | 0x2=RAM | 0x1=ROM
	//
	// ROM bank 0 = 0x10000
	// ROM bank 1 = 0x11000
	//
	// RAM bank 0 = 0x20000
	// RAM bank 1 = 0x21000
	//
	// PROM bank 0 = 0x40000
	// PROM bank 1 = 0x41000
	//
	// DATA bank 0 = 0x80000
	// DATA bank 1 = 0x82000
	// DATA bank 2 = 0x84000
	// DATA bank 3 = 0x88000
	// DATA bank 4 = 0x86000
	// DATA bank 5 = 0x8A000
	// DATA bank 6 = 0x8C000
	// DATA bank 7 = 0x8E000
	
	// ROM I/O = 0x10000 | SRC
	//      0x10000, 0x10010, 0x10020, 0x10030
	//      0x100E0 => enable/disable RAM write
	// DATA bank 0 /O = 0x80000 | SRC
	//		0x80000, 0x80010, 0x80020, 0x80030
	// DATA bank 1 /O = 0x82000 | SRC
	// DATA bank 2 /O = 0x84000 | SRC
	// DATA bank 3 /O = 0x88000 | SRC
	// DATA bank 4 /O = 0x86000 | SRC
	// DATA bank 5 /O = 0x8A000 | SRC
	// DATA bank 6 /O = 0x8C000 | SRC
	// DATA bank 7 /O = 0x8E000 | SRC
	// ROM 0  = 0x10000 | SRC
	// ROM 1  = 0x10010 not connected
	// ROM 2  = 0x10020 | SRC
	// ROM 3  = 0x10030 | SRC
	
	// Intel 4002 DATA RAM
	// 4 registers x (16 characters x 4-bit + 4 status character x 4-bit)
	// 4 chips per memory bank
	// X2 time D3,D2 = chip select (00,01,10,11) ; D1,D0 = register select
	// X3 time D3,D2,D1,D0 = select character 
	// SRC = CHIP + REGISTER + CHARACTER
	// Data ADDRESS   = MODE_DATA | CM_RAM | 0 (ROM) | 0000 | CHIP | REGISTER | CHARACTER
	// Status ADDRESS = MODE_DATA | CM_RAM | 0 (ROM) | 0001 | CHIP | REGISTER | 00 | CHARACTER
	// Memory for bank 0 = 0x80000 - 0x801F3  (499 locations, some will not be accessed)
	
	// Control flags: TEST, INT, STOP, STOPA
	
	public static final long MODE_ROM=0x10000;
	public static final long MODE_RAM=0x20000;
	public static final long MODE_PROM=0x40000;
	public static final long MODE_DATA=0x80000;
	long mode;
	
	long pc;
	long acc;
	long[] pc_stack;
	int pc_stack_ptr;
	int r_bank;
	long [][]registers;
	int flag_halt;
	int flag_carry;
	int flag_interrupt_enable;
	
	long cm_ram;
	long cm_rom;
	
	long reg_src;
	long reg_src_save;
	int reg_src_valid;
	
	long x2;
	long x3;
	
	int set_cm_rom;
	
	long pm_seq; // program memory sequence for WPM/RPM
	
	long last_pc;
	int opr,opa,opr2,opa2;
	
	static final long[] KBP=new long[] {
			0,1,2,0xF,3,0xF,0xF,0xF,4,0xF,0xF,0xF,0xF,0xF,0xF,0xF
	};
	
	static final long[] acc_to_RAMBANK=new long[] {
			0, 1, 2, 4, 3, 5, 6, 7
	};
	
	static final long[] acc_to_CMRAM_decode=new long[] {
			1, 2, 4, 6, 8, 10, 12, 14
	};
	
	CPUState cpuState=new CPUState();
	
	public CPUState getCPUState() {
		cpuState.setRegister("PC", 12 , pc);
		cpuState.setRegister("PC1", 12 , pc_stack[0]);
		cpuState.setRegister("PC2", 12 , pc_stack[1]);
		cpuState.setRegister("PC3", 12 , pc_stack[2]);
		cpuState.setRegister("PC4", 12 , pc_stack[3]);
		cpuState.setRegister("PC5", 12 , pc_stack[4]);
		cpuState.setRegister("PC6", 12 , pc_stack[5]);
		cpuState.setRegister("PC7", 12 , pc_stack[6]);
		cpuState.setRegister("PCPTR", 4 , pc_stack_ptr);
		cpuState.setRegister("R00", 4 , registers[0][0]);
		cpuState.setRegister("R01", 4 , registers[0][1]);
		cpuState.setRegister("R02", 4 , registers[0][2]);
		cpuState.setRegister("R03", 4 , registers[0][3]);
		cpuState.setRegister("R04", 4 , registers[0][4]);
		cpuState.setRegister("R05", 4 , registers[0][5]);
		cpuState.setRegister("R06", 4 , registers[0][6]);
		cpuState.setRegister("R07", 4 , registers[0][7]);
		cpuState.setRegister("R08", 4 , registers[0][8]);
		cpuState.setRegister("R09", 4 , registers[0][9]);
		cpuState.setRegister("R0A", 4 , registers[0][10]);
		cpuState.setRegister("R0B", 4 , registers[0][11]);
		cpuState.setRegister("R0C", 4 , registers[0][12]);
		cpuState.setRegister("R0D", 4 , registers[0][13]);
		cpuState.setRegister("R0E", 4 , registers[0][14]);
		cpuState.setRegister("R0F", 4 , registers[0][15]);
		cpuState.setRegister("R10", 4 , registers[1][0]);
		cpuState.setRegister("R11", 4 , registers[1][1]);
		cpuState.setRegister("R12", 4 , registers[1][2]);
		cpuState.setRegister("R13", 4 , registers[1][3]);
		cpuState.setRegister("R14", 4 , registers[1][4]);
		cpuState.setRegister("R15", 4 , registers[1][5]);
		cpuState.setRegister("R16", 4 , registers[1][6]);
		cpuState.setRegister("R17", 4 , registers[1][7]);
		cpuState.setRegister("RBANK", 4 , r_bank);
		cpuState.setRegister("ACC", 4 , acc);
		return cpuState;
	}
	
	
	public Intel4040() {
		super();
		
		pc_stack=new long[7];
		registers=new long[2][]; // banks
		registers[0]=new long[16];
		registers[1]=new long[8];
	}
	
	@Override
	public void initialize()
			throws DeviceConfigurationException, ConfigurationValueTypeException, ConfigurationValueOptionException {
		super.initialize();
		
		pc_stack_ptr=0;
		for(int i=0;i<pc_stack.length;i++)pc_stack[i]=0;
		acc=0;
		pc=0;
		r_bank=0;
		for(int i=0;i<registers.length;i++)
			for(int j=0;j<registers[i].length;j++)
				registers[i][j]=0;
		flag_halt=0;
		flag_carry=0;
		cm_ram=0;
		cm_rom=0;
		set_cm_rom=0;
		flag_interrupt_enable=1;
		reg_src=0;
		reg_src_save=0;
		pm_seq=0;
		last_pc=0;
		opr=0;
		opa=0;
		opr2=0;
		opa2=0;
		mode=MODE_ROM;
		reg_src_valid=0;
		x2=0;
		x3=0;
	}

	public long getPc() {
		return pc;
	}

	public long getAcc() {
		return acc;
	}

	public int getR_bank() {
		return r_bank;
	}

	public long[][] getRegisters() {
		return registers;
	}

	public int getFlag_halt() {
		return flag_halt;
	}

	public int getFlag_carry() {
		return flag_carry;
	}

	public void setPc(long pc) {
		this.pc = pc;
	}

	public void setAcc(long acc) {
		this.acc = acc;
	}

	public void setR_bank(int r_bank) {
		this.r_bank = r_bank;
	}

	public void setRegisters(long[][] registers) {
		this.registers = registers;
	}

	public void setFlag_halt(int flag_halt) {
		this.flag_halt = flag_halt;
	}

	public void setFlag_carry(int flag_carry) {
		this.flag_carry = flag_carry;
	}

	public int getFlag_interrupt_enable() {
		return flag_interrupt_enable;
	}

	public void setFlag_interrupt_enable(int flag_interrupt_enable) {
		this.flag_interrupt_enable = flag_interrupt_enable;
	}

	public long getCm_ram() {
		return cm_ram;
	}

	public void setCm_ram(long cm_ram) {
		this.cm_ram = cm_ram;
	}
	
	public long getCMRAM_decoded() {
		return acc_to_CMRAM_decode[(int) this.cm_ram];
	}

	public long getCm_rom() {
		return cm_rom;
	}

	public void setCm_rom(long cm_rom) {
		this.cm_rom = cm_rom;
	}

	public long getReg_src() {
		return reg_src;
	}

	public void setReg_src(long reg_src) {
		this.reg_src = reg_src;
	}

	@Override
	public void stepImpl() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
		
		if(controlBus.isSignalSet("STOP")) {
			controlBus.setSignal("STOPA");
			return;
		}else if(controlBus.isSignalSet("STOPA")) {
			controlBus.clearSignal("STOPA");
		}
		
		x2=0xF;
		x3=0xF;
		

		boolean service_int=false;
		if(flag_interrupt_enable==1) {
			if(controlBus.isSignalSet("INT")) {
				reg_src_save=reg_src;
				pc_stack[pc_stack_ptr]=pc & 0xFFF;
				pc_stack_ptr++;
				if(pc_stack_ptr>=pc_stack.length)pc_stack_ptr=0;
				pc=(pc&0xFFFFFFFFFFFFF000L) | 0x3; // jump to location 003
				service_int=true;
			}
		}

		if(flag_halt==1 && !service_int) {
			x2=(reg_src>>4)&0xF;
			x3=reg_src&0xF;
			
			return ;
		}
		
		
		last_pc=pc;
		
		int data8=(int) memoryBus.read(mode|pc);
		opr=data8>>4;
		opa=data8&0xF;
		pc++;

		opr2=0;
		opa2=0;
		
		int ti;
		long tl;
		
		if(opr==0x01 || (opr==0x02 && ((opa&0x1)==0)) || opr==0x04 || opr==0x05 || opr==0x07) {
			data8=(int) memoryBus.read(mode|pc);
			opr2=data8>>4;
			opa2=data8&0xF;
			pc++;			
		}
		
		if(set_cm_rom==1) {
			set_cm_rom=0;
			pc=(pc&0xFFFFFFFFFFFFEFFFL) | (cm_rom<<12);
		}
		
		switch(opr) {
		case 0x0: //
			switch(opa) {
			case 0: // NOP
				break;
				
			case 0x1: // HLT
				flag_halt=1;
				break;
				
			case 0x2: // BBS
				pc_stack_ptr--;
				if(pc_stack_ptr<0)pc_stack_ptr=pc_stack.length-1;
				pc=(pc&0xFFFFFFFFFFFFF000L) | (pc_stack[pc_stack_ptr]&0xFFF);
				reg_src=reg_src_save;
				x2=(reg_src>>4)&0xF;
				x3=reg_src&0xF;

				break;
				
			case 0x3: // LCR
				acc=(cm_rom<<3) | cm_ram;
				x2=acc;
				break;
				
			case 0x4: // OR4
				acc|=registers[r_bank][4];
				x2=registers[r_bank][4];
				break;
				
			case 0x5: // OR5
				acc|=registers[r_bank][5];
				x2=registers[r_bank][5];
				break;

			case 0x6: // AN6
				acc&=registers[r_bank][6];
				x2=registers[r_bank][6];
				break;

			case 0x7: // AN7
				acc&=registers[r_bank][7];
				x2=registers[r_bank][7];
				break;

			case 0x8: // DB0
				cm_rom=0;
				set_cm_rom=1;
				break;
				
			case 0x9: // DB1
				cm_rom=1;
				set_cm_rom=1;
				break;

			case 0xA: // SB0
				r_bank=0;
				break;
				
			case 0xB: // SB1
				r_bank=1;
				break;
				
			case 0xC: // EIN
				flag_interrupt_enable=1;
				break;
				
			case 0xD: // DIN
				flag_interrupt_enable=0;
				break;
				
			case 0xE: // RPM
				data8=(int)memoryBus.read(MODE_RAM|(cm_rom<<12)|(reg_src));
				if(pm_seq==0) {acc=data8>>4;x2=acc;}
				else {acc=data8&0xF;x3=acc;}
				pm_seq^=1;
				break;
				
			default:
				throw new CPUInvalidOpcodeException(new long[] {opr,opa,opr2,opa2});
				
			}
			break;
		case 0x1: // *JCN
			long nextpc=(pc&0xFFFFFFFFFFFFFF00L) | ((opr2<<4) | opa2);
			switch(opa) {
			case 1: //JNT
				if(!controlBus.isSignalSet("TEST"))pc=nextpc;
				break;
			case 2: // JC
				if(flag_carry==1)pc=nextpc;
				break;
			case 4: // JZ
				if(acc==0)pc=nextpc;
				break;
			case 9: // JT
				if(controlBus.isSignalSet("TEST"))pc=nextpc;
				break;
			case 10: // JNC
				if(flag_carry==0)pc=nextpc;
				break;
			case 12: // JNZ
				if(acc!=0)pc=nextpc;
				break;
			default:
				throw new CPUInvalidOpcodeException(new long[] {opr,opa,opr2,opa2});
			}
			break;
			
		case 0x2: // *FIM / SRC
			if((opa&0x1)==0) { // FIM
				registers[r_bank][opa]=opr2;
				registers[r_bank][opa+1]=opa2;
				x2=opr2;
				x3=opa2;
			}else { // SRC
				reg_src=(registers[r_bank][opa-1]<<4) | registers[r_bank][opa];
				reg_src_valid=1;
				x2=(reg_src>>4)&0xF;
				x3=reg_src&0xF;
			}
			break;
			
		case 0x3: // FIN / JIN
			if((opa&0x01)==0) { // FIN
				long addr=(pc&0xFFFFFFFFFFFFFF00L) | (registers[r_bank][0]<<4) | registers[r_bank][1];
				data8=(int)memoryBus.read(mode|addr);
				registers[r_bank][opa]=data8>>4;
				registers[r_bank][opa+1]=data8&0xF;
				x2=registers[r_bank][opa];
				x3=registers[r_bank][opa+1];
			}else { // JIN
				pc=(pc&0xFFFFFFFFFFFFFF00L) | (registers[r_bank][opa-1]<<4) | registers[r_bank][opa];
				x2=registers[r_bank][opa-1];
				x3=registers[r_bank][opa];
			}
			break;
			
		case 0x4: // *JUN
			pc=(pc&0xFFFFFFFFFFFFF000L) | (opa<<8) | (opr2<<4) | opa2;
			x2=opa;
			break;
			
		case 0x5: // *JMS
			pc_stack[pc_stack_ptr]=pc & 0xFFF;
			pc_stack_ptr++;
			if(pc_stack_ptr>=pc_stack.length)pc_stack_ptr=0;
			pc=(pc&0xFFFFFFFFFFFFF000L) | (opa<<8) | (opr2<<4) | opa2;
			x2=opa;
			break;
			
		case 0x6: // INC
			x2=registers[r_bank][opa];
			registers[r_bank][opa]++;
			registers[r_bank][opa]&=0x0F;
			x3=registers[r_bank][opa];
			break;
			
		case 0x7: // *ISZ
			x2=registers[r_bank][opa];
			registers[r_bank][opa]++;
			registers[r_bank][opa]&=0x0F;
			x3=registers[r_bank][opa];
			if(registers[r_bank][opa]!=0)
				pc=(pc&0xFFFFFFFFFFFFFF00L) | (opr2<<4) | opa2;
			break;
			
		case 0x8: // ADD
			x2=registers[r_bank][opa];
			acc=acc+registers[r_bank][opa]+flag_carry;
			if((acc&0x10)==0x10) {
				flag_carry=1; 
				acc=acc&0x0F;
			}else flag_carry=0;
			break;
			
		case 0x9: // SUB
			x2=registers[r_bank][opa];
			acc=acc+((~registers[r_bank][opa])&0x0F)+((~flag_carry)&0x1);
			if((acc&0x10)==0x10) {
				flag_carry=1; 
				acc=acc&0x0F;
			}else flag_carry=0;
			break;
			
		case 0xA: // LD
			x2=registers[r_bank][opa];
			acc=registers[r_bank][opa];
			break;
			
		case 0xB: // XCH
			x2=registers[r_bank][opa];
			x3=acc;
			tl=acc;
			acc=registers[r_bank][opa];
			registers[r_bank][opa]=tl;
			break;
			
		case 0xC: // BBL
			x2=opa;
			acc=opa;
			pc_stack_ptr--;
			if(pc_stack_ptr<0)pc_stack_ptr=pc_stack.length-1;
			pc=(pc&0xFFFFFFFFFFFFF000L) | (pc_stack[pc_stack_ptr]&0xFFF);
			break;
			
		case 0xD: // LDM
			x2=opa;
			acc=opa;
			break;
			
		case 0xE: // 
			switch(opa) {
			case 0: // WRM
				x2=acc;
				x3=0xE | this.flag_carry;
				memoryBus.write(MODE_DATA|(cm_ram<<13)|reg_src, acc);
				break;
			case 1: // WMP
				x2=acc;
				x3=0xE | this.flag_carry;
				ioBus.write(MODE_DATA|(cm_ram<<13)|reg_src, acc);
				break;
			case 2: // WRR
				x2=acc;
				x3=0xE | this.flag_carry;
				ioBus.write(MODE_ROM|(cm_rom<<12)|reg_src, acc);
				break;
			case 3: // WPM
				x2=acc;
				x3=0xE | this.flag_carry;
				data8=(int)memoryBus.read(MODE_RAM|(cm_rom<<12)|(reg_src));
				if(pm_seq==0) {data8=(int) ((data8&0xF)|(acc<<4));}
				else data8=(int) ((data8&0xF0)|acc);
				
				memoryBus.write(MODE_RAM|(cm_rom<<12)|(reg_src), data8);
				
				pm_seq^=1;
				x2=acc;
				break;
			case 4: // WR0
				x2=acc;
				x3=0xE | this.flag_carry;
				memoryBus.write(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x0, acc);
				break;
			case 5: // WR1
				x2=acc;
				x3=0xE | this.flag_carry;
				memoryBus.write(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x1, acc);
				break;
			case 6: // WR2
				x2=acc;
				x3=0xE | this.flag_carry;
				memoryBus.write(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x2, acc);
				break;
			case 7: // WR3
				x2=acc;
				x3=0xE | this.flag_carry;
				memoryBus.write(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x3, acc);
				break;
			case 8: // SBM
				tl=memoryBus.read(MODE_DATA|(cm_ram<<13)|reg_src) & 0x0F;
				x2=tl;
				acc=acc+((~tl)&0x0F)+((~flag_carry)&0x1);
				if((acc&0x10)==0x10) {
					flag_carry=1; 
					acc=acc&0x0F;
				}else flag_carry=0;
				break;
			case 9: // RDM
				acc=memoryBus.read(MODE_DATA|(cm_ram<<13)|reg_src) &0x0F;
				x2=acc;
				break;
			case 0xA: // RDR
				acc=ioBus.read(MODE_ROM|(cm_rom<<12)|reg_src) & 0x0F;
				x2=acc;
				break;
			case 0xB: // ADM
				tl=memoryBus.read(MODE_DATA|(cm_ram<<13)|reg_src) & 0x0F;
				x2=tl;
				acc=acc+tl+flag_carry;
				if((acc&0x10)==0x10) {
					flag_carry=1; 
					acc=acc&0x0F;
				}else flag_carry=0;
				break;
			case 0xC: // RD0
				acc=memoryBus.read(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x0);
				break;
			case 0xD: // RD1
				acc=memoryBus.read(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x1);
				break;
			case 0xE: // RD2
				acc=memoryBus.read(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x2);
				break;
			case 0xF: // RD3
				acc=memoryBus.read(MODE_DATA|(cm_ram<<13)|0x100|(reg_src&0xF0)|0x3);
				break;
			default:
				throw new CPUInvalidOpcodeException(new long[] {opr,opa,opr2,opa2});
			}

			break; // End 0xE
		case 0xF: // 
			switch(opa) {
			case 0x0: // CLB
				acc=0;
				flag_carry=0;
				x2=0;
				break;
				
			case 0x1: // CLC
				flag_carry=0;
				x2=0;
				break;
				
			case 0x2: // IAC
				x2=0;
				acc++;
				if((acc&0x10)==0x10) {
					flag_carry=1; 
					acc=acc&0x0F;
				}else flag_carry=0;
				break;
				
			case 0x3: // CMC
				x2=0;
				flag_carry^=0x1;
				break;
				
			case 0x4: // CMA
				x2=0;
				acc^=0x0F;
				break;
				
			case 0x5: // RAL
				x2=0;
				acc<<=1;
				acc|=flag_carry;
				flag_carry=(int)((acc&0x10)>>4);
				break;
				
			case 0x6: // RAR
				x2=0;
				ti=(int)(acc&0x01);
				acc|=(flag_carry<<4);
				acc>>=1;
				flag_carry=ti;
				break;
				
			case 0x7: // TCC
				x2=0;
				acc=flag_carry;
				flag_carry=0;
				break;
				
			case 0x8: // DAC
				acc+=0x0F;
				flag_carry=(int)((acc&0x10)>>4);
				acc&=0x0F;
				break;
				
			case 0x9: // TCS
				x2=9;
				if(flag_carry==1)acc=9;
				else acc=10;
				flag_carry=0;
				break;
				
			case 0xA: // STC
				flag_carry=1;
				break;
				
			case 0xB: // DAA
				x2=0;
				if(flag_carry==1 || acc>9) {
					acc+=6;
					x2=6;
					if((acc&0x10)==0x10)flag_carry=1; 
				}
				break;
				
			case 0xC: // KBP
				x2=acc;
				acc=KBP[(int) acc];
				break;
				
			case 0xD: // DCL
				cm_ram=acc&0x7;
				break;
				
			default:
				throw new CPUInvalidOpcodeException(new long[] {opr,opa,opr2,opa2});
			}
			break; // End 0xF
			
		default:
			throw new CPUInvalidOpcodeException(new long[] {opr,opa,opr2,opa2});

		}
	}

	public long getLast_pc() {
		return last_pc;
	}

	public int getOpr() {
		return opr;
	}

	public int getOpa() {
		return opa;
	}

	public int getOpr2() {
		return opr2;
	}

	public int getOpa2() {
		return opa2;
	}

	public long getMode() {
		return mode;
	}

	public void setMode(long mode) {
		this.mode = mode;
	}
	
	public long getCR() {
		return (cm_rom<<3) | cm_ram;
	}

	public int getReg_src_valid() {
		return reg_src_valid;
	}

	public void setReg_src_valid(int reg_src_valid) {
		this.reg_src_valid = reg_src_valid;
	}

	public long getX2() {
		return x2;
	}

	public long getX3() {
		return x3;
	}
	
	@Override
	public long getCurrentAddress() {
		return mode|pc;
	}
	
}
