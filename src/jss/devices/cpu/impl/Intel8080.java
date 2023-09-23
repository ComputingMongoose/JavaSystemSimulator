package jss.devices.cpu.impl;

import jss.configuration.ConfigurationValueOptionException;
import jss.configuration.ConfigurationValueTypeException;
import jss.configuration.DeviceConfiguration;
import jss.configuration.DeviceConfigurationException;
import jss.devices.bus.ControlBusUnknownSignalException;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;
import jss.simulation.Simulation;
import jss.devices.cpu.CPUState;
import jss.devices.cpu.AbstractCPUDevice;

public class Intel8080 extends AbstractCPUDevice {

    int PC;
    int last_PC;
    int []registers;
    int SP;
    int flags;
    int []parity_map;
    boolean flag_ei;
    boolean flag_halt;
    int opcodeByte1;
    int acc;
    int tmp1=0,tmp2=0,tmp3=0;
    byte[] int_instr=null;
    int int_instr_pc=0;
          
	CPUState cpuState=new CPUState();
	
	public CPUState getCPUState() {
		cpuState.setRegister("PC", 16 , PC);
		cpuState.setRegister("SP", 16 , SP);
		cpuState.setRegister("A", 8 , acc);
		cpuState.setRegister("B", 8 , registers[0]);
		cpuState.setRegister("C", 8 , registers[1]);
		cpuState.setRegister("D", 8 , registers[2]);
		cpuState.setRegister("E", 8 , registers[3]);
		cpuState.setRegister("H", 8 , registers[4]);
		cpuState.setRegister("L", 8 , registers[5]);
		cpuState.setRegister("FLAGS", 8 , flags);
		return cpuState;
	}
      
    @Override
    public void configure(DeviceConfiguration config, Simulation sim)
	   throws DeviceConfigurationException, ConfigurationValueTypeException {
            super.configure(config,sim);

            registers=new int[6];
            parity_map=new int[256];
    		for(int i=0;i<256;i++) {
    			parity_map[i]=
    				((   (i&0x1)+
    					((i>>1)&0x1)+
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
          super.initialize();
          
          for(int i=0;i<registers.length;i++)registers[i]=0;
          flags=0x02;
          SP=0;
          PC=0;
          flag_halt=false;
          flag_ei=true;
          last_PC=0;
    }

	@Override
	public long getCurrentAddress() {
		return this.PC;
	}
          
	public int getLast_pc() {
		return last_PC;
	}

	public boolean isFlag_halt() {
		return flag_halt;
	}

	public int getInstr() {
		return opcodeByte1;
	}
          





// Flags = S|Z|-|AC|-|P|-|C
// Flags = S|Z|0|AC|0|P|1|C
/* # d e f i n e SET_FLAG_AC(v) #{ flags=(flags&0xEF)|((v)<<4); #} */







	@Override
	public void stepImpl() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
                    last_PC=PC;
                    
                    int_instr=null;
                    if(flag_ei && controlBus.isSignalSet("INT")) {
                              int_instr=controlBus.getSignalData("INT");
                              int_instr_pc=0;
                              flag_halt=false;
                              controlBus.clearSignal("INT");
                    }

		if(flag_halt) {
			return ;
		}


if(int_instr!=null){
          opcodeByte1=(int)int_instr[int_instr_pc++] & 0xFF;
}else{
          opcodeByte1=(int)memoryBus.read(PC++);
}




switch(opcodeByte1){
case 0x00:
opcode__0x00();
break;
case 0x01:
opcode__0x01();
break;
case 0x02:
opcode__0x02();
break;
case 0x03:
opcode__0x03();
break;
case 0x04:
opcode__0x04();
break;
case 0x05:
opcode__0x05();
break;
case 0x06:
opcode__0x06();
break;
case 0x07:
opcode__0x07();
break;
case 0x09:
opcode__0x09();
break;
case 0x0A:
opcode__0x0A();
break;
case 0x0B:
opcode__0x0B();
break;
case 0x0C:
opcode__0x0C();
break;
case 0x0D:
opcode__0x0D();
break;
case 0x0E:
opcode__0x0E();
break;
case 0x0F:
opcode__0x0F();
break;
case 0x11:
opcode__0x11();
break;
case 0x12:
opcode__0x12();
break;
case 0x13:
opcode__0x13();
break;
case 0x14:
opcode__0x14();
break;
case 0x15:
opcode__0x15();
break;
case 0x16:
opcode__0x16();
break;
case 0x17:
opcode__0x17();
break;
case 0x19:
opcode__0x19();
break;
case 0x1A:
opcode__0x1A();
break;
case 0x1B:
opcode__0x1B();
break;
case 0x1C:
opcode__0x1C();
break;
case 0x1D:
opcode__0x1D();
break;
case 0x1E:
opcode__0x1E();
break;
case 0x1F:
opcode__0x1F();
break;
case 0x21:
opcode__0x21();
break;
case 0x22:
opcode__0x22();
break;
case 0x23:
opcode__0x23();
break;
case 0x24:
opcode__0x24();
break;
case 0x25:
opcode__0x25();
break;
case 0x26:
opcode__0x26();
break;
case 0x27:
opcode__0x27();
break;
case 0x29:
opcode__0x29();
break;
case 0x2A:
opcode__0x2A();
break;
case 0x2B:
opcode__0x2B();
break;
case 0x2C:
opcode__0x2C();
break;
case 0x2D:
opcode__0x2D();
break;
case 0x2E:
opcode__0x2E();
break;
case 0x2F:
opcode__0x2F();
break;
case 0x31:
opcode__0x31();
break;
case 0x32:
opcode__0x32();
break;
case 0x33:
opcode__0x33();
break;
case 0x34:
opcode__0x34();
break;
case 0x35:
opcode__0x35();
break;
case 0x36:
opcode__0x36();
break;
case 0x37:
opcode__0x37();
break;
case 0x39:
opcode__0x39();
break;
case 0x3A:
opcode__0x3A();
break;
case 0x3B:
opcode__0x3B();
break;
case 0x3C:
opcode__0x3C();
break;
case 0x3D:
opcode__0x3D();
break;
case 0x3E:
opcode__0x3E();
break;
case 0x3F:
opcode__0x3F();
break;
case 0x40:
opcode__0x40();
break;
case 0x41:
opcode__0x41();
break;
case 0x42:
opcode__0x42();
break;
case 0x43:
opcode__0x43();
break;
case 0x44:
opcode__0x44();
break;
case 0x45:
opcode__0x45();
break;
case 0x46:
opcode__0x46();
break;
case 0x47:
opcode__0x47();
break;
case 0x48:
opcode__0x48();
break;
case 0x49:
opcode__0x49();
break;
case 0x4A:
opcode__0x4A();
break;
case 0x4B:
opcode__0x4B();
break;
case 0x4C:
opcode__0x4C();
break;
case 0x4D:
opcode__0x4D();
break;
case 0x4E:
opcode__0x4E();
break;
case 0x4F:
opcode__0x4F();
break;
case 0x50:
opcode__0x50();
break;
case 0x51:
opcode__0x51();
break;
case 0x52:
opcode__0x52();
break;
case 0x53:
opcode__0x53();
break;
case 0x54:
opcode__0x54();
break;
case 0x55:
opcode__0x55();
break;
case 0x56:
opcode__0x56();
break;
case 0x57:
opcode__0x57();
break;
case 0x58:
opcode__0x58();
break;
case 0x59:
opcode__0x59();
break;
case 0x5A:
opcode__0x5A();
break;
case 0x5B:
opcode__0x5B();
break;
case 0x5C:
opcode__0x5C();
break;
case 0x5D:
opcode__0x5D();
break;
case 0x5E:
opcode__0x5E();
break;
case 0x5F:
opcode__0x5F();
break;
case 0x60:
opcode__0x60();
break;
case 0x61:
opcode__0x61();
break;
case 0x62:
opcode__0x62();
break;
case 0x63:
opcode__0x63();
break;
case 0x64:
opcode__0x64();
break;
case 0x65:
opcode__0x65();
break;
case 0x66:
opcode__0x66();
break;
case 0x67:
opcode__0x67();
break;
case 0x68:
opcode__0x68();
break;
case 0x69:
opcode__0x69();
break;
case 0x6A:
opcode__0x6A();
break;
case 0x6B:
opcode__0x6B();
break;
case 0x6C:
opcode__0x6C();
break;
case 0x6D:
opcode__0x6D();
break;
case 0x6E:
opcode__0x6E();
break;
case 0x6F:
opcode__0x6F();
break;
case 0x70:
opcode__0x70();
break;
case 0x71:
opcode__0x71();
break;
case 0x72:
opcode__0x72();
break;
case 0x73:
opcode__0x73();
break;
case 0x74:
opcode__0x74();
break;
case 0x75:
opcode__0x75();
break;
case 0x76:
opcode__0x76();
break;
case 0x77:
opcode__0x77();
break;
case 0x78:
opcode__0x78();
break;
case 0x79:
opcode__0x79();
break;
case 0x7A:
opcode__0x7A();
break;
case 0x7B:
opcode__0x7B();
break;
case 0x7C:
opcode__0x7C();
break;
case 0x7D:
opcode__0x7D();
break;
case 0x7E:
opcode__0x7E();
break;
case 0x7F:
opcode__0x7F();
break;
case 0x80:
opcode__0x80();
break;
case 0x81:
opcode__0x81();
break;
case 0x82:
opcode__0x82();
break;
case 0x83:
opcode__0x83();
break;
case 0x84:
opcode__0x84();
break;
case 0x85:
opcode__0x85();
break;
case 0x86:
opcode__0x86();
break;
case 0x87:
opcode__0x87();
break;
case 0x88:
opcode__0x88();
break;
case 0x89:
opcode__0x89();
break;
case 0x8A:
opcode__0x8A();
break;
case 0x8B:
opcode__0x8B();
break;
case 0x8C:
opcode__0x8C();
break;
case 0x8D:
opcode__0x8D();
break;
case 0x8E:
opcode__0x8E();
break;
case 0x8F:
opcode__0x8F();
break;
case 0x90:
opcode__0x90();
break;
case 0x91:
opcode__0x91();
break;
case 0x92:
opcode__0x92();
break;
case 0x93:
opcode__0x93();
break;
case 0x94:
opcode__0x94();
break;
case 0x95:
opcode__0x95();
break;
case 0x96:
opcode__0x96();
break;
case 0x97:
opcode__0x97();
break;
case 0x98:
opcode__0x98();
break;
case 0x99:
opcode__0x99();
break;
case 0x9A:
opcode__0x9A();
break;
case 0x9B:
opcode__0x9B();
break;
case 0x9C:
opcode__0x9C();
break;
case 0x9D:
opcode__0x9D();
break;
case 0x9E:
opcode__0x9E();
break;
case 0x9F:
opcode__0x9F();
break;
case 0xA0:
opcode__0xA0();
break;
case 0xA1:
opcode__0xA1();
break;
case 0xA2:
opcode__0xA2();
break;
case 0xA3:
opcode__0xA3();
break;
case 0xA4:
opcode__0xA4();
break;
case 0xA5:
opcode__0xA5();
break;
case 0xA6:
opcode__0xA6();
break;
case 0xA7:
opcode__0xA7();
break;
case 0xA8:
opcode__0xA8();
break;
case 0xA9:
opcode__0xA9();
break;
case 0xAA:
opcode__0xAA();
break;
case 0xAB:
opcode__0xAB();
break;
case 0xAC:
opcode__0xAC();
break;
case 0xAD:
opcode__0xAD();
break;
case 0xAE:
opcode__0xAE();
break;
case 0xAF:
opcode__0xAF();
break;
case 0xB0:
opcode__0xB0();
break;
case 0xB1:
opcode__0xB1();
break;
case 0xB2:
opcode__0xB2();
break;
case 0xB3:
opcode__0xB3();
break;
case 0xB4:
opcode__0xB4();
break;
case 0xB5:
opcode__0xB5();
break;
case 0xB6:
opcode__0xB6();
break;
case 0xB7:
opcode__0xB7();
break;
case 0xB8:
opcode__0xB8();
break;
case 0xB9:
opcode__0xB9();
break;
case 0xBA:
opcode__0xBA();
break;
case 0xBB:
opcode__0xBB();
break;
case 0xBC:
opcode__0xBC();
break;
case 0xBD:
opcode__0xBD();
break;
case 0xBE:
opcode__0xBE();
break;
case 0xBF:
opcode__0xBF();
break;
case 0xC0:
opcode__0xC0();
break;
case 0xC1:
opcode__0xC1();
break;
case 0xC2:
opcode__0xC2();
break;
case 0xC3:
opcode__0xC3();
break;
case 0xC4:
opcode__0xC4();
break;
case 0xC5:
opcode__0xC5();
break;
case 0xC6:
opcode__0xC6();
break;
case 0xC7:
opcode__0xC7();
break;
case 0xC8:
opcode__0xC8();
break;
case 0xC9:
opcode__0xC9();
break;
case 0xCA:
opcode__0xCA();
break;
case 0xCC:
opcode__0xCC();
break;
case 0xCD:
opcode__0xCD();
break;
case 0xCE:
opcode__0xCE();
break;
case 0xCF:
opcode__0xCF();
break;
case 0xD0:
opcode__0xD0();
break;
case 0xD1:
opcode__0xD1();
break;
case 0xD2:
opcode__0xD2();
break;
case 0xD3:
opcode__0xD3();
break;
case 0xD4:
opcode__0xD4();
break;
case 0xD5:
opcode__0xD5();
break;
case 0xD6:
opcode__0xD6();
break;
case 0xD7:
opcode__0xD7();
break;
case 0xD8:
opcode__0xD8();
break;
case 0xDA:
opcode__0xDA();
break;
case 0xDB:
opcode__0xDB();
break;
case 0xDC:
opcode__0xDC();
break;
case 0xDE:
opcode__0xDE();
break;
case 0xDF:
opcode__0xDF();
break;
case 0xE0:
opcode__0xE0();
break;
case 0xE1:
opcode__0xE1();
break;
case 0xE2:
opcode__0xE2();
break;
case 0xE3:
opcode__0xE3();
break;
case 0xE4:
opcode__0xE4();
break;
case 0xE5:
opcode__0xE5();
break;
case 0xE6:
opcode__0xE6();
break;
case 0xE7:
opcode__0xE7();
break;
case 0xE8:
opcode__0xE8();
break;
case 0xE9:
opcode__0xE9();
break;
case 0xEA:
opcode__0xEA();
break;
case 0xEB:
opcode__0xEB();
break;
case 0xEC:
opcode__0xEC();
break;
case 0xEE:
opcode__0xEE();
break;
case 0xEF:
opcode__0xEF();
break;
case 0xF0:
opcode__0xF0();
break;
case 0xF1:
opcode__0xF1();
break;
case 0xF2:
opcode__0xF2();
break;
case 0xF3:
opcode__0xF3();
break;
case 0xF4:
opcode__0xF4();
break;
case 0xF5:
opcode__0xF5();
break;
case 0xF6:
opcode__0xF6();
break;
case 0xF7:
opcode__0xF7();
break;
case 0xF8:
opcode__0xF8();
break;
case 0xF9:
opcode__0xF9();
break;
case 0xFA:
opcode__0xFA();
break;
case 0xFB:
opcode__0xFB();
break;
case 0xFC:
opcode__0xFC();
break;
case 0xFE:
opcode__0xFE();
break;
case 0xFF:
opcode__0xFF();
break;
default: throw new CPUInvalidOpcodeException(new long[] {opcodeByte1});
}

          } // end step()

private void opcode__0x00()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //NOP

}

private void opcode__0x01()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI B
registers[1]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
registers[0]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x02()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STAX B
memoryBus.write(((registers[0]<<8)|(registers[1])),acc);
}

private void opcode__0x03()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=BC
tmp1=((registers[0]<<8)|(registers[1]));
tmp1=tmp1+1;
registers[0]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[1]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x04()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=B
tmp1=registers[0];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[0]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x05()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=B
tmp1=registers[0];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[0]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x06()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=B
registers[0]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x07()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RLC
tmp1=acc;
tmp1<<=1;
flags=(flags&0xFE)|(((tmp1&0x100)==0x100)?(1):(0));;
acc=((tmp1&0xFF) | (flags&0x1)) & 0xFF;
}

private void opcode__0x09()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=BC
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[0]<<8)|(registers[1]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=((tmp2)&0xFFFF)>>8; registers[5]=(tmp2)&0xFF;
}

private void opcode__0x0A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDAX B
acc=((int)memoryBus.read(((registers[0]<<8)|(registers[1])))) & 0xFF;
}

private void opcode__0x0B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=BC
tmp1=((registers[0]<<8)|(registers[1]));
tmp1=tmp1-1;
registers[0]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[1]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x0C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=C
tmp1=registers[1];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[1]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x0D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=C
tmp1=registers[1];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[1]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x0E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=C
registers[1]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x0F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RRC
tmp1=acc;
flags=(flags&0xFE)|(tmp1&0x1);
tmp1>>=1;
tmp1|= ((flags&0x1)<<7);
acc=(tmp1) & 0xFF;;
}

private void opcode__0x11()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI D
registers[3]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
registers[2]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x12()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STAX D
memoryBus.write(((registers[2]<<8)|(registers[3])),acc);
}

private void opcode__0x13()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=DE
tmp1=((registers[2]<<8)|(registers[3]));
tmp1=tmp1+1;
registers[2]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[3]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x14()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=D
tmp1=registers[2];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[2]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x15()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=D
tmp1=registers[2];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[2]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x16()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=D
registers[2]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x17()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RAL
tmp1=acc;
tmp1<<=1;
acc=((tmp1&0xFF) | (flags&0x1)) & 0xFF;
flags=(flags&0xFE)|(((tmp1&0x100)==0x100)?(1):(0));;
}

private void opcode__0x19()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=DE
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[2]<<8)|(registers[3]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=((tmp2)&0xFFFF)>>8; registers[5]=(tmp2)&0xFF;
}

private void opcode__0x1A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDAX D
acc=((int)memoryBus.read(((registers[2]<<8)|(registers[3])))) & 0xFF;
}

private void opcode__0x1B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=DE
tmp1=((registers[2]<<8)|(registers[3]));
tmp1=tmp1-1;
registers[2]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[3]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x1C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=E
tmp1=registers[3];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[3]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x1D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=E
tmp1=registers[3];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[3]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x1E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=E
registers[3]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x1F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RAR
tmp1=acc;
acc=((tmp1>>1) | ((flags&0x1)<<7)) & 0xFF;;
flags=(flags&0xFE)|(tmp1&0x1);
}

private void opcode__0x21()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI H
registers[5]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
registers[4]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x22()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SHLD
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
memoryBus.write(tmp2,registers[5]);
tmp2++;
memoryBus.write(tmp2,registers[4]);
}

private void opcode__0x23()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp1=tmp1+1;
registers[4]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[5]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x24()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=H
tmp1=registers[4];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[4]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x25()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=H
tmp1=registers[4];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[4]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x26()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=H
registers[4]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x27()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAA
tmp1=acc;
tmp2=0; // add
if( ((tmp1 & 0xF) > 9) || ((flags&0x10)==0x10) ){
    tmp2 = 0x06;
}

tmp3=(((flags&0x1)==0x1)) ? (1) : (0); // tmp3=carry
if (((tmp1 & 0xF0) > 0x90) ||
      (((tmp1 & 0xF0) >= 0x90) && ((tmp1 & 0xF) > 9)) ||
      tmp3!=0) {
    tmp2 |= 0x60;
    tmp3 = 1;
}      

tmp2=tmp1+tmp2;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;

flags=(flags&0xFE)|(tmp3);;
}

private void opcode__0x29()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[4]<<8)|(registers[5]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=((tmp2)&0xFFFF)>>8; registers[5]=(tmp2)&0xFF;
}

private void opcode__0x2A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LHLD
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
registers[5]=(int)memoryBus.read(tmp2);
tmp2++;
registers[4]=(int)memoryBus.read(tmp2);
}

private void opcode__0x2B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp1=tmp1-1;
registers[4]=((tmp1&0xFFFF)&0xFFFF)>>8; registers[5]=(tmp1&0xFFFF)&0xFF;
}

private void opcode__0x2C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=L
tmp1=registers[5];
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
registers[5]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x2D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=L
tmp1=registers[5];
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
registers[5]=tmp2;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x2E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=L
registers[5]=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
}

private void opcode__0x2F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMA
acc=(~(acc)) & 0xFF;
}

private void opcode__0x31()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI SP
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp1=(((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF))<<8)|tmp1;
SP=(tmp1)&0xFFFF;
}

private void opcode__0x32()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STA
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
memoryBus.write(tmp2,acc);;
}

private void opcode__0x33()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=SP
tmp1=SP;
tmp1=tmp1+1;
SP=(tmp1&0xFFFF)&0xFFFF;
}

private void opcode__0x34()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR M
tmp1=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
memoryBus.write(((registers[4]<<8)|(registers[5])),tmp2);;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x35()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR M
tmp1=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
memoryBus.write(((registers[4]<<8)|(registers[5])),tmp2);;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x36()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI M
memoryBus.write(((registers[4]<<8)|(registers[5])),((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)));
}

private void opcode__0x37()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STC
flags=(flags&0xFE)|(1);
}

private void opcode__0x39()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=SP
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+SP;
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=((tmp2)&0xFFFF)>>8; registers[5]=(tmp2)&0xFF;
}

private void opcode__0x3A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDA
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
acc=((int)memoryBus.read(tmp2)) & 0xFF;
}

private void opcode__0x3B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=SP
tmp1=SP;
tmp1=tmp1-1;
SP=(tmp1&0xFFFF)&0xFFFF;
}

private void opcode__0x3C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=A
tmp1=acc;
tmp2=(tmp1+1)&0xFF;
tmp3=(tmp1&0x0F)+1;
acc=(tmp2) & 0xFF;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x3D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=A
tmp1=acc;
tmp2=(tmp1-1)&0xFF;
tmp3=(tmp1&0x0F)+0x0F;
acc=(tmp2) & 0xFF;;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x3E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=A
acc=(((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF))) & 0xFF;
}

private void opcode__0x3F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMC
flags=(flags&0xFE)|(((~flags)&0x01));
}

private void opcode__0x40()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=B
registers[0]=registers[0];
}

private void opcode__0x41()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=C
registers[0]=registers[1];
}

private void opcode__0x42()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=D
registers[0]=registers[2];
}

private void opcode__0x43()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=E
registers[0]=registers[3];
}

private void opcode__0x44()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=H
registers[0]=registers[4];
}

private void opcode__0x45()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=L
registers[0]=registers[5];
}

private void opcode__0x46()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=B
registers[0]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x47()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=A
registers[0]=acc;
}

private void opcode__0x48()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=B
registers[1]=registers[0];
}

private void opcode__0x49()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=C
registers[1]=registers[1];
}

private void opcode__0x4A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=D
registers[1]=registers[2];
}

private void opcode__0x4B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=E
registers[1]=registers[3];
}

private void opcode__0x4C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=H
registers[1]=registers[4];
}

private void opcode__0x4D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=L
registers[1]=registers[5];
}

private void opcode__0x4E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=C
registers[1]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x4F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=A
registers[1]=acc;
}

private void opcode__0x50()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=B
registers[2]=registers[0];
}

private void opcode__0x51()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=C
registers[2]=registers[1];
}

private void opcode__0x52()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=D
registers[2]=registers[2];
}

private void opcode__0x53()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=E
registers[2]=registers[3];
}

private void opcode__0x54()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=H
registers[2]=registers[4];
}

private void opcode__0x55()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=L
registers[2]=registers[5];
}

private void opcode__0x56()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=D
registers[2]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x57()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=A
registers[2]=acc;
}

private void opcode__0x58()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=B
registers[3]=registers[0];
}

private void opcode__0x59()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=C
registers[3]=registers[1];
}

private void opcode__0x5A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=D
registers[3]=registers[2];
}

private void opcode__0x5B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=E
registers[3]=registers[3];
}

private void opcode__0x5C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=H
registers[3]=registers[4];
}

private void opcode__0x5D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=L
registers[3]=registers[5];
}

private void opcode__0x5E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=E
registers[3]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x5F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=A
registers[3]=acc;
}

private void opcode__0x60()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=B
registers[4]=registers[0];
}

private void opcode__0x61()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=C
registers[4]=registers[1];
}

private void opcode__0x62()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=D
registers[4]=registers[2];
}

private void opcode__0x63()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=E
registers[4]=registers[3];
}

private void opcode__0x64()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=H
registers[4]=registers[4];
}

private void opcode__0x65()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=L
registers[4]=registers[5];
}

private void opcode__0x66()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=H
registers[4]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x67()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=A
registers[4]=acc;
}

private void opcode__0x68()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=B
registers[5]=registers[0];
}

private void opcode__0x69()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=C
registers[5]=registers[1];
}

private void opcode__0x6A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=D
registers[5]=registers[2];
}

private void opcode__0x6B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=E
registers[5]=registers[3];
}

private void opcode__0x6C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=H
registers[5]=registers[4];
}

private void opcode__0x6D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=L
registers[5]=registers[5];
}

private void opcode__0x6E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=L
registers[5]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
}

private void opcode__0x6F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=A
registers[5]=acc;
}

private void opcode__0x70()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=B
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[0]);
}

private void opcode__0x71()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=C
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[1]);
}

private void opcode__0x72()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=D
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[2]);
}

private void opcode__0x73()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=E
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[3]);
}

private void opcode__0x74()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=H
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[4]);
}

private void opcode__0x75()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=L
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[5]);
}

private void opcode__0x76()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //HLT
flag_halt=true;
}

private void opcode__0x77()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=A
memoryBus.write(((registers[4]<<8)|(registers[5])),acc);
}

private void opcode__0x78()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=B
acc=(registers[0]) & 0xFF;
}

private void opcode__0x79()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=C
acc=(registers[1]) & 0xFF;
}

private void opcode__0x7A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=D
acc=(registers[2]) & 0xFF;
}

private void opcode__0x7B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=E
acc=(registers[3]) & 0xFF;
}

private void opcode__0x7C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=H
acc=(registers[4]) & 0xFF;
}

private void opcode__0x7D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=L
acc=(registers[5]) & 0xFF;
}

private void opcode__0x7E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=A
acc=((int)memoryBus.read(((registers[4]<<8)|(registers[5])))) & 0xFF;
}

private void opcode__0x7F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=A
acc=(acc) & 0xFF;
}

private void opcode__0x80()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=B
tmp1=acc;
tmp2=tmp1+registers[0];
tmp3=(tmp1&0x0F)+(registers[0]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x81()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=C
tmp1=acc;
tmp2=tmp1+registers[1];
tmp3=(tmp1&0x0F)+(registers[1]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x82()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=D
tmp1=acc;
tmp2=tmp1+registers[2];
tmp3=(tmp1&0x0F)+(registers[2]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x83()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=E
tmp1=acc;
tmp2=tmp1+registers[3];
tmp3=(tmp1&0x0F)+(registers[3]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x84()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=H
tmp1=acc;
tmp2=tmp1+registers[4];
tmp3=(tmp1&0x0F)+(registers[4]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x85()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=L
tmp1=acc;
tmp2=tmp1+registers[5];
tmp3=(tmp1&0x0F)+(registers[5]&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x86()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD M
tmp1=acc;
tmp3=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=tmp1+tmp3;
tmp3=(tmp1&0x0F)+(tmp3&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x87()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=A
tmp1=acc;
tmp2=tmp1+acc;
tmp3=(tmp1&0x0F)+(acc&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x88()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=B
tmp1=acc;
tmp2=tmp1+registers[0]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[0])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x89()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=C
tmp1=acc;
tmp2=tmp1+registers[1]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[1])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=D
tmp1=acc;
tmp2=tmp1+registers[2]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[2])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=E
tmp1=acc;
tmp2=tmp1+registers[3]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[3])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=H
tmp1=acc;
tmp2=tmp1+registers[4]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[4])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=L
tmp1=acc;
tmp2=tmp1+registers[5]+(flags&0x1);
tmp3=(tmp1&0x0F)+((registers[5])&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC M
tmp1=acc;
tmp3=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=tmp1+tmp3+(flags&0x1);
tmp3=(tmp1&0x0F)+(tmp3&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x8F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=A
tmp1=acc;
tmp2=tmp1+acc+(flags&0x1);
tmp3=(tmp1&0x0F)+((acc)&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0x90()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=B
tmp1=acc;
tmp2=tmp1 + ((registers[0])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[0])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x91()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=C
tmp1=acc;
tmp2=tmp1 + ((registers[1])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[1])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x92()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=D
tmp1=acc;
tmp2=tmp1 + ((registers[2])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[2])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x93()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=E
tmp1=acc;
tmp2=tmp1 + ((registers[3])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[3])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x94()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=H
tmp1=acc;
tmp2=tmp1 + ((registers[4])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[4])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x95()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=L
tmp1=acc;
tmp2=tmp1 + ((registers[5])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[5])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x96()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB M
tmp1=acc;
tmp3=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=tmp1 + (tmp3^0xFF) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x97()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=A
tmp1=acc;
tmp2=tmp1 + ((acc)^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((acc)^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x98()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=B
tmp1=acc;
tmp3=registers[0];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x99()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=C
tmp1=acc;
tmp3=registers[1];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=D
tmp1=acc;
tmp3=registers[2];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=E
tmp1=acc;
tmp3=registers[3];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=H
tmp1=acc;
tmp3=registers[4];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=L
tmp1=acc;
tmp3=registers[5];
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB M
tmp1=acc;
tmp3=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1 ;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0x9F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=A
tmp1=acc;
tmp3=acc;
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1 ;
//tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + ((flags&0x01)^0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xA0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=B
flags=(flags&0xEF)|((( ((acc)| (registers[0])) &0x08)>>3)<<4);; 
acc=(acc & (registers[0])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=C
flags=(flags&0xEF)|((( ((acc)| (registers[1])) &0x08)>>3)<<4);; 
acc=(acc & (registers[1])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=D
flags=(flags&0xEF)|((( ((acc)| (registers[2])) &0x08)>>3)<<4);; 
acc=(acc & (registers[2])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=E
flags=(flags&0xEF)|((( ((acc)| (registers[3])) &0x08)>>3)<<4);; 
acc=(acc & (registers[3])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=H
flags=(flags&0xEF)|((( ((acc)| (registers[4])) &0x08)>>3)<<4);; 
acc=(acc & (registers[4])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=L
flags=(flags&0xEF)|((( ((acc)| (registers[5])) &0x08)>>3)<<4);; 
acc=(acc & (registers[5])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA M
tmp1=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
flags=(flags&0xEF)|((( ((acc)| tmp1) &0x08)>>3)<<4);; 
acc=(acc & tmp1) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=A
flags=(flags&0xEF)|((( ((acc)| (acc)) &0x08)>>3)<<4);; 
acc=(acc & (acc)) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xA8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=B
acc=(acc ^ (registers[0])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xA9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=C
acc=(acc ^ (registers[1])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=D
acc=(acc ^ (registers[2])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=E
acc=(acc ^ (registers[3])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=H
acc=(acc ^ (registers[4])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=L
acc=(acc ^ (registers[5])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA M
acc=(acc ^ ((int)memoryBus.read(((registers[4]<<8)|(registers[5]))))) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xAF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=A
acc=(acc ^ (acc)) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=B
acc=(acc | (registers[0])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=C
acc=(acc | (registers[1])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=D
acc=(acc | (registers[2])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=E
acc=(acc | (registers[3])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=H
acc=(acc | (registers[4])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=L
acc=(acc | (registers[5])) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA M
acc=(acc | ((int)memoryBus.read(((registers[4]<<8)|(registers[5]))))) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=A
acc=(acc | (acc)) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xB8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=B
tmp1=acc;
tmp2=tmp1 + ((registers[0])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[0])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xB9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=C
tmp1=acc;
tmp2=tmp1 + ((registers[1])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[1])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=D
tmp1=acc;
tmp2=tmp1 + ((registers[2])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[2])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=E
tmp1=acc;
tmp2=tmp1 + ((registers[3])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[3])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=H
tmp1=acc;
tmp2=tmp1 + ((registers[4])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[4])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=L
tmp1=acc;
tmp2=tmp1 + ((registers[5])^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((registers[5])^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP M
tmp1=acc;
tmp3=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=tmp1 + (tmp3^0xFF) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xBF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=A
tmp1=acc;
tmp2=tmp1 + ((acc)^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((acc)^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xC0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=NZ
if( ((flags&0x40)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xC1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP B
registers[1]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[0]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
}

private void opcode__0xC2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=NZ
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x40)==0) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xC3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JMP
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
PC=(tmp2)&0xFFFF;
}

private void opcode__0xC4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=NZ
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x40)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xC5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH B
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[0]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[1]);
}

private void opcode__0xC6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADI
tmp1=acc;
tmp3=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=tmp1+(tmp3&0xFF);
tmp3=(tmp1&0x0F) + (tmp3&0x0F);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0xC7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=0
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xC8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=Z
if( ((flags&0x40)==0x40) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xC9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RET
tmp1=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
SP++; SP&=0xFFFF;
}

private void opcode__0xCA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=Z
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x40)==0x40) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xCC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=Z
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x40)==0x40) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xCD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALL
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(tmp2)&0xFFFF;
}

private void opcode__0xCE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ACI
tmp1=acc;
tmp3=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=tmp1+tmp3+(flags&0x1);
tmp3=(tmp1&0x0F)+(tmp3&0x0F)+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
}

private void opcode__0xCF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=1
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xD0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=NC
if( ((flags&0x1)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xD1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP D
registers[3]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[2]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
}

private void opcode__0xD2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=NC
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x1)==0) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xD3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //OUT
ioBus.write(((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)),acc);
}

private void opcode__0xD4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=NC
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x1)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xD5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH D
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[2]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[3]);
}

private void opcode__0xD6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUI
tmp1=acc;
tmp3=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=tmp1 + (tmp3^0xFF) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xD7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=2
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xD8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=C
if( ((flags&0x1)==0x1) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xDA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=C
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x1)==0x1) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xDB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //IN
acc=((int)ioBus.read(((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))) & 0xFF;
}

private void opcode__0xDC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=C
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x1)==0x1) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xDE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBI
tmp1=acc;
tmp3=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=tmp1 + ((tmp3 + (flags&0x01))^0xFF) + 1;
tmp3=(tmp1&0x0F) + (((tmp3 + (flags&0x01))^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=(tmp2) & 0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xDF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=3
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xE0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=PO
if( ((flags&0x4)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xE1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP H
registers[5]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[4]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
}

private void opcode__0xE2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=PO
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x4)==0) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xE3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XTHL
tmp1=(int)memoryBus.read(SP);
memoryBus.write(SP,registers[5]);
registers[5]=tmp1;
SP++; SP&=0xFFFF;
tmp2=(int)memoryBus.read(SP);
memoryBus.write(SP,registers[4]);
registers[4]=tmp2;
SP--; SP&=0xFFFF;
}

private void opcode__0xE4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=PO
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x4)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xE5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH H
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[4]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[5]);
}

private void opcode__0xE6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANI
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
flags=(flags&0xEF)|((( ((acc)| tmp1) &0x08)>>3)<<4);; 
acc=(acc & tmp1) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=flags&0xFE;
}

private void opcode__0xE7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=4
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xE8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=PE
if( ((flags&0x4)==0x4) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xE9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PCHL
PC=(((registers[4]<<8)|(registers[5])))&0xFFFF;
}

private void opcode__0xEA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=PE
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x4)==0x4) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xEB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XCHG
tmp1=registers[4];
registers[4]=registers[2];
registers[2]=tmp1;
tmp1=registers[5];
registers[5]=registers[3];
registers[3]=tmp1;
}

private void opcode__0xEC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=PE
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x4)==0x4) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xEE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRI
acc=(acc ^ (((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xEF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=5
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xF0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=P
if( ((flags&0x80)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xF1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP PSW
flags=((int)memoryBus.read(SP) & 0x00D7) | 0x02;
SP++; SP&=0xFFFF;
acc=((int)memoryBus.read(SP)) & 0xFF;
SP++; SP&=0xFFFF;
}

private void opcode__0xF2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=P
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x80)==0) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xF3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DI
flag_ei=false;
}

private void opcode__0xF4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=P
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x80)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xF5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH PSW
SP--; SP&=0xFFFF;
memoryBus.write(SP,acc);
SP--; SP&=0xFFFF;
memoryBus.write(SP,flags);
}

private void opcode__0xF6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORI
acc=(acc | (((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))) & 0xFF;
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);;;
flags=(flags&0xEF)|((0)<<4);;
flags=(flags&0xFE)|(0);;
flags=flags&0xFE;
}

private void opcode__0xF7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=6
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

private void opcode__0xF8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=M
if( ((flags&0x80)==0x80) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=(((int)memoryBus.read(SP)<<8)|tmp1)&0xFFFF;
  SP++; SP&=0xFFFF;
}
}

private void opcode__0xF9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SPHL
SP=(((registers[4]<<8)|(registers[5])))&0xFFFF;
}

private void opcode__0xFA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=M
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x80)==0x80) ){
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xFB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //EI
flag_ei=true;
}

private void opcode__0xFC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=M
tmp1=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF)))<<8)|tmp1;
if( ((flags&0x80)==0x80) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=(tmp2)&0xFFFF;
}
}

private void opcode__0xFE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CPI
tmp1=acc;
tmp3=((int_instr==null)?((int)memoryBus.read(PC++) &0xFF):(int_instr[int_instr_pc++] & 0xFF));
tmp2=tmp1 + (tmp3^0xFF) + 1;
tmp3=(tmp1&0x0F) + ((tmp3^0xFF)&0x0F) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xEF) | ((tmp3)&0x10);; 
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);;;
flags=flags^1;
}

private void opcode__0xFF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=7
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=(opcodeByte1&0x38)&0xFFFF;
}

}