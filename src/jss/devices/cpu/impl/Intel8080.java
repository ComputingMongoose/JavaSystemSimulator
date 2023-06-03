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

public class Intel8080 implements CPUDevice, GenericControlDevice, GenericDataAccessDevice {

	private DataBus memoryBus=null;
	private DataBus ioBus=null;
	private ControlBus controlBus=null;

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

          @Override
          public void configure(DeviceConfiguration config, Simulation sim)
			throws DeviceConfigurationException, ConfigurationValueTypeException {

                    registers=new int[6];
                    parity_map=new int[256];
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
                    for(int i=0;i<registers.length;i++)registers[i]=0;
                    flags=0;
                    SP=0;
                    PC=0;
                    flag_halt=false;
                    flag_ei=true;
                    last_PC=0;
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







	@Override
	public void step() throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
                    last_PC=PC;
                    int tmp1=0,tmp2=0;
                    byte[] int_instr=null;
                    int int_instr_pc=0;
                    
                    if(flag_ei && controlBus.isSignalSet("INT")) {
                              int_instr=controlBus.getSignalData("INT");
                              flag_halt=false;
                    }

		if(flag_halt) {
			return ;
		}


if(int_instr!=null){
          opcodeByte1=(int)int_instr[int_instr_pc++];
}else{
          opcodeByte1=(int)memoryBus.read(PC++);
}




switch(opcodeByte1){
case 0x00: //NOP

break;

case 0x01: //LXI B
registers[1]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
registers[0]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x02: //STAX B
memoryBus.write(((registers[0]<<8)|(registers[1])),acc);
break;

case 0x03: //INX RP RP=BC
tmp1=((registers[0]<<8)|(registers[1]));
tmp1=tmp1+1;
registers[0]=(tmp1&0xFFFF)>>8; registers[1]=(tmp1&0xFFFF)&0xFF;
break;

case 0x04: //INR R DDD=B
tmp1=registers[0];
tmp2=(tmp1+1)&0xFF;
registers[0]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x05: //DCR R DDD=B
tmp1=registers[0];
tmp2=(tmp1-1)&0xFF;
registers[0]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x06: //MVI R DDD=B
registers[0]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x07: //RLC
tmp1=acc;
tmp1<<=1;
flags=(flags&0xFE)|(((tmp1&0x100)==0x100)?(1):(0));;
acc=(tmp1&0xFF) | (flags&0x1);
break;

case 0x09: //DAD RP RP=BC
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[0]<<8)|(registers[1]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=(tmp2)>>8; registers[5]=(tmp2)&0xFF;
break;

case 0x0A: //LDAX B
acc=(int)memoryBus.read(((registers[0]<<8)|(registers[1])));
break;

case 0x0B: //DCX RP RP=BC
tmp1=((registers[0]<<8)|(registers[1]));
tmp1=tmp1-1;
registers[0]=(tmp1&0xFFFF)>>8; registers[1]=(tmp1&0xFFFF)&0xFF;
break;

case 0x0C: //INR R DDD=C
tmp1=registers[1];
tmp2=(tmp1+1)&0xFF;
registers[1]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x0D: //DCR R DDD=C
tmp1=registers[1];
tmp2=(tmp1-1)&0xFF;
registers[1]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x0E: //MVI R DDD=C
registers[1]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x0F: //RRC
tmp1=acc;
flags=(flags&0xFE)|(tmp1&0x1);
tmp1>>=1;
tmp1|= ((flags&0x1)<<7);
acc=tmp1;;
break;

case 0x11: //LXI D
registers[3]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
registers[2]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x12: //STAX D
memoryBus.write(((registers[2]<<8)|(registers[3])),acc);
break;

case 0x13: //INX RP RP=DE
tmp1=((registers[2]<<8)|(registers[3]));
tmp1=tmp1+1;
registers[2]=(tmp1&0xFFFF)>>8; registers[3]=(tmp1&0xFFFF)&0xFF;
break;

case 0x14: //INR R DDD=D
tmp1=registers[2];
tmp2=(tmp1+1)&0xFF;
registers[2]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x15: //DCR R DDD=D
tmp1=registers[2];
tmp2=(tmp1-1)&0xFF;
registers[2]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x16: //MVI R DDD=D
registers[2]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x17: //RAL
tmp1=acc;
tmp1<<=1;
acc=(tmp1&0xFF) | (flags&0x1);
flags=(flags&0xFE)|(((tmp1&0x100)==0x100)?(1):(0));;
break;

case 0x19: //DAD RP RP=DE
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[2]<<8)|(registers[3]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=(tmp2)>>8; registers[5]=(tmp2)&0xFF;
break;

case 0x1A: //LDAX D
acc=(int)memoryBus.read(((registers[2]<<8)|(registers[3])));
break;

case 0x1B: //DCX RP RP=DE
tmp1=((registers[2]<<8)|(registers[3]));
tmp1=tmp1-1;
registers[2]=(tmp1&0xFFFF)>>8; registers[3]=(tmp1&0xFFFF)&0xFF;
break;

case 0x1C: //INR R DDD=E
tmp1=registers[3];
tmp2=(tmp1+1)&0xFF;
registers[3]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x1D: //DCR R DDD=E
tmp1=registers[3];
tmp2=(tmp1-1)&0xFF;
registers[3]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x1E: //MVI R DDD=E
registers[3]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x1F: //RAR
tmp1=acc;
acc=(tmp1>>1) | ((flags&0x1)<<7);;
flags=(flags&0xFE)|(tmp1&0x1);
break;

case 0x21: //LXI H
registers[5]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
registers[4]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x22: //SHLD
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
memoryBus.write(tmp2,registers[5]);
tmp2++;
memoryBus.write(tmp2,registers[4]);
break;

case 0x23: //INX RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp1=tmp1+1;
registers[4]=(tmp1&0xFFFF)>>8; registers[5]=(tmp1&0xFFFF)&0xFF;
break;

case 0x24: //INR R DDD=H
tmp1=registers[4];
tmp2=(tmp1+1)&0xFF;
registers[4]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x25: //DCR R DDD=H
tmp1=registers[4];
tmp2=(tmp1-1)&0xFF;
registers[4]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x26: //MVI R DDD=H
registers[4]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x27: //DAA
tmp1=acc;
if( ((tmp1&0xF)>9) || ((flags&0x10)==0x10) ){
          tmp2=tmp1;
          tmp1+=6;
          flags=(flags&0xEF)|((((tmp1&0x0F)<(tmp2&0x0F))?(1):(0))<<4);;           
}

tmp2=(tmp1>>4)&0x0F;
if( tmp2>9 || ((flags&0x1)==0x1) ){
          tmp1+=0x60;
          if(tmp1>0xFF)flags=(flags&0xFE)|(1);;
} 
acc=tmp1&0xFF;;
break;

case 0x29: //DAD RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+((registers[4]<<8)|(registers[5]));
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=(tmp2)>>8; registers[5]=(tmp2)&0xFF;
break;

case 0x2A: //LHLD
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
registers[5]=(int)memoryBus.read(tmp2);
tmp2++;
registers[4]=(int)memoryBus.read(tmp2);
break;

case 0x2B: //DCX RP RP=HL
tmp1=((registers[4]<<8)|(registers[5]));
tmp1=tmp1-1;
registers[4]=(tmp1&0xFFFF)>>8; registers[5]=(tmp1&0xFFFF)&0xFF;
break;

case 0x2C: //INR R DDD=L
tmp1=registers[5];
tmp2=(tmp1+1)&0xFF;
registers[5]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x2D: //DCR R DDD=L
tmp1=registers[5];
tmp2=(tmp1-1)&0xFF;
registers[5]=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x2E: //MVI R DDD=L
registers[5]=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x2F: //CMA
acc=~(acc);
break;

case 0x31: //LXI SP
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp1=(((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]))<<8)|tmp1;
SP=tmp1;
break;

case 0x32: //STA
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
memoryBus.write(tmp2,acc);;
break;

case 0x33: //INX RP RP=SP
tmp1=SP;
tmp1=tmp1+1;
SP=tmp1&0xFFFF;
break;

case 0x34: //INR M
tmp1=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=(tmp1+1)&0xFF;
memoryBus.write(((registers[4]<<8)|(registers[5])),tmp2);;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x35: //DCR M
tmp1=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
tmp2=(tmp1-1)&0xFF;
memoryBus.write(((registers[4]<<8)|(registers[5])),tmp2);;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x36: //MVI M
memoryBus.write(((registers[4]<<8)|(registers[5])),((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])));
break;

case 0x37: //STC
flags=(flags&0xFE)|(1);
break;

case 0x39: //DAD RP RP=SP
tmp1=((registers[4]<<8)|(registers[5]));
tmp2=tmp1+SP;
flags=(flags&0xFE)|((((tmp2>>8)&0x100)==0x100)?(1):(0));;;
registers[4]=(tmp2)>>8; registers[5]=(tmp2)&0xFF;
break;

case 0x3A: //LDA
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
acc=(int)memoryBus.read(tmp2);
break;

case 0x3B: //DCX RP RP=SP
tmp1=SP;
tmp1=tmp1-1;
SP=tmp1&0xFFFF;
break;

case 0x3C: //INR R DDD=A
tmp1=acc;
tmp2=(tmp1+1)&0xFF;
acc=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x3D: //DCR R DDD=A
tmp1=acc;
tmp2=(tmp1-1)&0xFF;
acc=tmp2;;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x3E: //MVI R DDD=A
acc=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
break;

case 0x3F: //CMC
flags=(flags&0xFE)|(((~flags)&0x01));
break;

case 0x40: //MOV R1,R2 DDD=B SSS=B
registers[0]=registers[0];
break;

case 0x41: //MOV R1,R2 DDD=B SSS=C
registers[0]=registers[1];
break;

case 0x42: //MOV R1,R2 DDD=B SSS=D
registers[0]=registers[2];
break;

case 0x43: //MOV R1,R2 DDD=B SSS=E
registers[0]=registers[3];
break;

case 0x44: //MOV R1,R2 DDD=B SSS=H
registers[0]=registers[4];
break;

case 0x45: //MOV R1,R2 DDD=B SSS=L
registers[0]=registers[5];
break;

case 0x46: //MOV R,M DDD=B
registers[0]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x47: //MOV R1,R2 DDD=B SSS=A
registers[0]=acc;
break;

case 0x48: //MOV R1,R2 DDD=C SSS=B
registers[1]=registers[0];
break;

case 0x49: //MOV R1,R2 DDD=C SSS=C
registers[1]=registers[1];
break;

case 0x4A: //MOV R1,R2 DDD=C SSS=D
registers[1]=registers[2];
break;

case 0x4B: //MOV R1,R2 DDD=C SSS=E
registers[1]=registers[3];
break;

case 0x4C: //MOV R1,R2 DDD=C SSS=H
registers[1]=registers[4];
break;

case 0x4D: //MOV R1,R2 DDD=C SSS=L
registers[1]=registers[5];
break;

case 0x4E: //MOV R,M DDD=C
registers[1]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x4F: //MOV R1,R2 DDD=C SSS=A
registers[1]=acc;
break;

case 0x50: //MOV R1,R2 DDD=D SSS=B
registers[2]=registers[0];
break;

case 0x51: //MOV R1,R2 DDD=D SSS=C
registers[2]=registers[1];
break;

case 0x52: //MOV R1,R2 DDD=D SSS=D
registers[2]=registers[2];
break;

case 0x53: //MOV R1,R2 DDD=D SSS=E
registers[2]=registers[3];
break;

case 0x54: //MOV R1,R2 DDD=D SSS=H
registers[2]=registers[4];
break;

case 0x55: //MOV R1,R2 DDD=D SSS=L
registers[2]=registers[5];
break;

case 0x56: //MOV R,M DDD=D
registers[2]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x57: //MOV R1,R2 DDD=D SSS=A
registers[2]=acc;
break;

case 0x58: //MOV R1,R2 DDD=E SSS=B
registers[3]=registers[0];
break;

case 0x59: //MOV R1,R2 DDD=E SSS=C
registers[3]=registers[1];
break;

case 0x5A: //MOV R1,R2 DDD=E SSS=D
registers[3]=registers[2];
break;

case 0x5B: //MOV R1,R2 DDD=E SSS=E
registers[3]=registers[3];
break;

case 0x5C: //MOV R1,R2 DDD=E SSS=H
registers[3]=registers[4];
break;

case 0x5D: //MOV R1,R2 DDD=E SSS=L
registers[3]=registers[5];
break;

case 0x5E: //MOV R,M DDD=E
registers[3]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x5F: //MOV R1,R2 DDD=E SSS=A
registers[3]=acc;
break;

case 0x60: //MOV R1,R2 DDD=H SSS=B
registers[4]=registers[0];
break;

case 0x61: //MOV R1,R2 DDD=H SSS=C
registers[4]=registers[1];
break;

case 0x62: //MOV R1,R2 DDD=H SSS=D
registers[4]=registers[2];
break;

case 0x63: //MOV R1,R2 DDD=H SSS=E
registers[4]=registers[3];
break;

case 0x64: //MOV R1,R2 DDD=H SSS=H
registers[4]=registers[4];
break;

case 0x65: //MOV R1,R2 DDD=H SSS=L
registers[4]=registers[5];
break;

case 0x66: //MOV R,M DDD=H
registers[4]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x67: //MOV R1,R2 DDD=H SSS=A
registers[4]=acc;
break;

case 0x68: //MOV R1,R2 DDD=L SSS=B
registers[5]=registers[0];
break;

case 0x69: //MOV R1,R2 DDD=L SSS=C
registers[5]=registers[1];
break;

case 0x6A: //MOV R1,R2 DDD=L SSS=D
registers[5]=registers[2];
break;

case 0x6B: //MOV R1,R2 DDD=L SSS=E
registers[5]=registers[3];
break;

case 0x6C: //MOV R1,R2 DDD=L SSS=H
registers[5]=registers[4];
break;

case 0x6D: //MOV R1,R2 DDD=L SSS=L
registers[5]=registers[5];
break;

case 0x6E: //MOV R,M DDD=L
registers[5]=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x6F: //MOV R1,R2 DDD=L SSS=A
registers[5]=acc;
break;

case 0x70: //MOV M,R SSS=B
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[0]);
break;

case 0x71: //MOV M,R SSS=C
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[1]);
break;

case 0x72: //MOV M,R SSS=D
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[2]);
break;

case 0x73: //MOV M,R SSS=E
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[3]);
break;

case 0x74: //MOV M,R SSS=H
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[4]);
break;

case 0x75: //MOV M,R SSS=L
memoryBus.write(((registers[4]<<8)|(registers[5])),registers[5]);
break;

case 0x76: //HLT
flag_halt=true;
break;

case 0x77: //MOV M,R SSS=A
memoryBus.write(((registers[4]<<8)|(registers[5])),acc);
break;

case 0x78: //MOV R1,R2 DDD=A SSS=B
acc=registers[0];
break;

case 0x79: //MOV R1,R2 DDD=A SSS=C
acc=registers[1];
break;

case 0x7A: //MOV R1,R2 DDD=A SSS=D
acc=registers[2];
break;

case 0x7B: //MOV R1,R2 DDD=A SSS=E
acc=registers[3];
break;

case 0x7C: //MOV R1,R2 DDD=A SSS=H
acc=registers[4];
break;

case 0x7D: //MOV R1,R2 DDD=A SSS=L
acc=registers[5];
break;

case 0x7E: //MOV R,M DDD=A
acc=(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
break;

case 0x7F: //MOV R1,R2 DDD=A SSS=A
acc=acc;
break;

case 0x80: //ADD R SSS=B
tmp1=acc;
tmp2=tmp1+registers[0];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x81: //ADD R SSS=C
tmp1=acc;
tmp2=tmp1+registers[1];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x82: //ADD R SSS=D
tmp1=acc;
tmp2=tmp1+registers[2];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x83: //ADD R SSS=E
tmp1=acc;
tmp2=tmp1+registers[3];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x84: //ADD R SSS=H
tmp1=acc;
tmp2=tmp1+registers[4];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x85: //ADD R SSS=L
tmp1=acc;
tmp2=tmp1+registers[5];
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x86: //ADD M
tmp1=acc;
tmp2=tmp1+(int)memoryBus.read(((registers[4]<<8)|(registers[5])));
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x87: //ADD R SSS=A
tmp1=acc;
tmp2=tmp1+acc;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x88: //ADC R SSS=B
tmp1=acc;
tmp2=tmp1+registers[0]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x89: //ADC R SSS=C
tmp1=acc;
tmp2=tmp1+registers[1]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8A: //ADC R SSS=D
tmp1=acc;
tmp2=tmp1+registers[2]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8B: //ADC R SSS=E
tmp1=acc;
tmp2=tmp1+registers[3]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8C: //ADC R SSS=H
tmp1=acc;
tmp2=tmp1+registers[4]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8D: //ADC R SSS=L
tmp1=acc;
tmp2=tmp1+registers[5]+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8E: //ADC M
tmp1=acc;
tmp2=tmp1+(int)memoryBus.read(((registers[4]<<8)|(registers[5])))+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x8F: //ADC R SSS=A
tmp1=acc;
tmp2=tmp1+acc+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0x90: //SUB R SSS=B
tmp1=acc;
tmp2=tmp1 + ((registers[0])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x91: //SUB R SSS=C
tmp1=acc;
tmp2=tmp1 + ((registers[1])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x92: //SUB R SSS=D
tmp1=acc;
tmp2=tmp1 + ((registers[2])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x93: //SUB R SSS=E
tmp1=acc;
tmp2=tmp1 + ((registers[3])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x94: //SUB R SSS=H
tmp1=acc;
tmp2=tmp1 + ((registers[4])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x95: //SUB R SSS=L
tmp1=acc;
tmp2=tmp1 + ((registers[5])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x96: //SUB M
tmp1=acc;
tmp2=tmp1 + (((int)memoryBus.read(((registers[4]<<8)|(registers[5]))))^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x97: //SUB R SSS=A
tmp1=acc;
tmp2=tmp1 + ((acc)^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x98: //SBB R SSS=B
tmp1=acc;
tmp2=tmp1 + ((registers[0] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x99: //SBB R SSS=C
tmp1=acc;
tmp2=tmp1 + ((registers[1] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9A: //SBB R SSS=D
tmp1=acc;
tmp2=tmp1 + ((registers[2] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9B: //SBB R SSS=E
tmp1=acc;
tmp2=tmp1 + ((registers[3] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9C: //SBB R SSS=H
tmp1=acc;
tmp2=tmp1 + ((registers[4] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9D: //SBB R SSS=L
tmp1=acc;
tmp2=tmp1 + ((registers[5] + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9E: //SBB R
tmp1=acc;
tmp2=tmp1 + (((int)memoryBus.read(((registers[4]<<8)|(registers[5]))))^0xFF) + 1 + (flags&0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0x9F: //SBB R SSS=A
tmp1=acc;
tmp2=tmp1 + ((acc + (flags&0x01))^0xFF) + 1 ;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xA0: //ANA R SSS=B
acc=acc & (registers[0]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA1: //ANA R SSS=C
acc=acc & (registers[1]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA2: //ANA R SSS=D
acc=acc & (registers[2]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA3: //ANA R SSS=E
acc=acc & (registers[3]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA4: //ANA R SSS=H
acc=acc & (registers[4]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA5: //ANA R SSS=L
acc=acc & (registers[5]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA6: //ANA M
acc=acc & ((int)memoryBus.read(((registers[4]<<8)|(registers[5]))));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA7: //ANA R SSS=A
acc=acc & (acc);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA8: //XRA R SSS=B
acc=acc ^ (registers[0]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xA9: //XRA R SSS=C
acc=acc ^ (registers[1]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAA: //XRA R SSS=D
acc=acc ^ (registers[2]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAB: //XRA R SSS=E
acc=acc ^ (registers[3]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAC: //XRA R SSS=H
acc=acc ^ (registers[4]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAD: //XRA R SSS=L
acc=acc ^ (registers[5]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAE: //XRA M
acc=acc ^ ((int)memoryBus.read(((registers[4]<<8)|(registers[5]))));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xAF: //XRA R SSS=A
acc=acc ^ (acc);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB0: //ORA R SSS=B
acc=acc | (registers[0]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB1: //ORA R SSS=C
acc=acc | (registers[1]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB2: //ORA R SSS=D
acc=acc | (registers[2]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB3: //ORA R SSS=E
acc=acc | (registers[3]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB4: //ORA R SSS=H
acc=acc | (registers[4]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB5: //ORA R SSS=L
acc=acc | (registers[5]);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB6: //ORA M
acc=acc | ((int)memoryBus.read(((registers[4]<<8)|(registers[5]))));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB7: //ORA R SSS=A
acc=acc | (acc);
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xB8: //CMP R SSS=B
tmp1=acc;
tmp2=tmp1 + ((registers[0])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xB9: //CMP R SSS=C
tmp1=acc;
tmp2=tmp1 + ((registers[1])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBA: //CMP R SSS=D
tmp1=acc;
tmp2=tmp1 + ((registers[2])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBB: //CMP R SSS=E
tmp1=acc;
tmp2=tmp1 + ((registers[3])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBC: //CMP R SSS=H
tmp1=acc;
tmp2=tmp1 + ((registers[4])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBD: //CMP R SSS=L
tmp1=acc;
tmp2=tmp1 + ((registers[5])^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBE: //CMP M
tmp1=acc;
tmp2=tmp1 + (((int)memoryBus.read(((registers[4]<<8)|(registers[5]))))^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xBF: //CMP R SSS=A
tmp1=acc;
tmp2=tmp1 + ((acc)^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xC0: //RETC CCC=NZ
if( ((flags&0x40)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xC1: //POP B
registers[1]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[0]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
break;

case 0xC2: //JC CCC=NZ
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x40)==0) ){
  PC=tmp2;
}
break;

case 0xC3: //JMP
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
PC=tmp2;
break;

case 0xC4: //CALLC CCC=NZ
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x40)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xC5: //PUSH B
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[0]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[1]);
break;

case 0xC6: //ADI
tmp1=acc;
tmp2=tmp1+((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0xC7: //RST AAA=0
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xC8: //RETC CCC=Z
if( ((flags&0x40)==0x40) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xC9: //RET
tmp1=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
PC=((int)memoryBus.read(SP)<<8)|tmp1;
SP++; SP&=0xFFFF;
break;

case 0xCA: //JC CCC=Z
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x40)==0x40) ){
  PC=tmp2;
}
break;

case 0xCC: //CALLC CCC=Z
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x40)==0x40) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xCD: //CALL
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=tmp2;
break;

case 0xCE: //ACI
tmp1=acc;
tmp2=tmp1+((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]))+(flags&0x1);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
break;

case 0xCF: //RST AAA=1
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xD0: //RETC CCC=NC
if( ((flags&0x1)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xD1: //POP D
registers[3]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[2]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
break;

case 0xD2: //JC CCC=NC
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x1)==0) ){
  PC=tmp2;
}
break;

case 0xD3: //OUT
ioBus.write(((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])),acc);
break;

case 0xD4: //CALLC CCC=NC
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x1)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xD5: //PUSH D
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[2]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[3]);
break;

case 0xD6: //SUI
tmp1=acc;
tmp2=tmp1 + ((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xD7: //RST AAA=2
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xD8: //RETC CCC=C
if( ((flags&0x1)==0x1) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xDA: //JC CCC=C
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x1)==0x1) ){
  PC=tmp2;
}
break;

case 0xDB: //IN
acc=(int)ioBus.read(((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])));
break;

case 0xDC: //CALLC CCC=C
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x1)==0x1) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xDE: //SBI
tmp1=acc;
tmp2=tmp1 + ((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))^0xFF) + 1 + (flags&0x01);
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
acc=tmp2;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xDF: //RST AAA=3
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xE0: //RETC CCC=PO
if( ((flags&0x4)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xE1: //POP H
registers[5]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
registers[4]=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
break;

case 0xE2: //JC CCC=PO
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x4)==0) ){
  PC=tmp2;
}
break;

case 0xE3: //XTHL
tmp1=(int)memoryBus.read(SP);
memoryBus.write(SP,registers[5]);
registers[5]=tmp1;
SP++; SP&=0xFFFF;
tmp2=(int)memoryBus.read(SP);
memoryBus.write(SP,registers[4]);
registers[4]=tmp2;
SP--; SP&=0xFFFF;
break;

case 0xE4: //CALLC CCC=PO
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x4)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xE5: //PUSH H
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[4]);
SP--; SP&=0xFFFF;
memoryBus.write(SP,registers[5]);
break;

case 0xE6: //ANI
acc=acc & (((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xE7: //RST AAA=4
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xE8: //RETC CCC=PE
if( ((flags&0x4)==0x4) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xE9: //PCHL
PC=((registers[4]<<8)|(registers[5]));
break;

case 0xEA: //JC CCC=PE
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x4)==0x4) ){
  PC=tmp2;
}
break;

case 0xEB: //XCHG
tmp1=registers[4];
registers[4]=registers[2];
registers[2]=tmp1;
tmp1=registers[5];
registers[5]=registers[3];
registers[3]=tmp1;
break;

case 0xEC: //CALLC CCC=PE
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x4)==0x4) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xEE: //XRI
acc=acc ^ (((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xEF: //RST AAA=5
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xF0: //RETC CCC=P
if( ((flags&0x80)==0) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xF1: //POP PSW
flags=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
acc=(int)memoryBus.read(SP);
SP++; SP&=0xFFFF;
break;

case 0xF2: //JC CCC=P
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x80)==0) ){
  PC=tmp2;
}
break;

case 0xF3: //DI
flag_ei=false;
break;

case 0xF4: //CALLC CCC=P
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x80)==0) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xF5: //PUSH PSW
SP--; SP&=0xFFFF;
memoryBus.write(SP,acc);
SP--; SP&=0xFFFF;
memoryBus.write(SP,flags);
break;

case 0xF6: //ORI
acc=acc | (((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])));
flags=(flags&0xBF)|((((acc&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[acc])<<2);; flags=(flags&0x7F)|((((acc&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((acc&0x0F)<(acc&0x0F))?(1):(0))<<4);;;
flags=flags&0xFE;
break;

case 0xF7: //RST AAA=6
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

case 0xF8: //RETC CCC=M
if( ((flags&0x80)==0x80) ){
  tmp1=(int)memoryBus.read(SP);
  SP++; SP&=0xFFFF;
  PC=((int)memoryBus.read(SP)<<8)|tmp1;
  SP++; SP&=0xFFFF;
}
break;

case 0xF9: //SPHL
SP=((registers[4]<<8)|(registers[5]));
break;

case 0xFA: //JC CCC=M
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x80)==0x80) ){
  PC=tmp2;
}
break;

case 0xFB: //EI
flag_ei=true;
break;

case 0xFC: //CALLC CCC=M
tmp1=((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++]));
tmp2=((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))<<8)|tmp1;
if( ((flags&0x80)==0x80) ){
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,((PC>>8)&0xFF));
  SP--; SP&=0xFFFF;
  memoryBus.write(SP,(PC&0xFF));
  PC=tmp2;
}
break;

case 0xFE: //CPI
tmp1=acc;
tmp2=tmp1 + ((((int_instr==null)?((int)memoryBus.read(PC++)):(int_instr[int_instr_pc++])))^0xFF) + 1;
flags=(flags&0xFE)|(((tmp2&0x100)==0x100)?(1):(0));;
tmp2=tmp2&0xFF;
flags=(flags&0xBF)|((((tmp2&0xFF)==0)?(1):(0))<<6);; flags=(flags&0xFB)|((parity_map[tmp2])<<2);; flags=(flags&0x7F)|((((tmp2&0x80)==0x80)?(1):(0))<<7);; flags=(flags&0xEF)|((((tmp2&0x0F)<(tmp1&0x0F))?(1):(0))<<4);;;
flags=flags^1;
break;

case 0xFF: //RST AAA=7
SP--; SP&=0xFFFF;
memoryBus.write(SP,((PC>>8)&0xFF));
SP--; SP&=0xFFFF;
memoryBus.write(SP,(PC&0xFF));
PC=opcodeByte1&0x38;
break;

default: throw new CPUInvalidOpcodeException(new long[] {opcodeByte1});
}

          } // end step()
}


