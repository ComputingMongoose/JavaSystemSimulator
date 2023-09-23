package jss.devices.cpu.impl;

import jss.devices.cpu.AbstractDisassembler;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.memory.MemoryAccessException;
import jss.devices.bus.ControlBusUnknownSignalException;

public class Intel8080Disassembler extends AbstractDisassembler {

    int opcodeByte1;
    int tmp1=0,tmp2=0;
          








    @Override
	public void step() throws MemoryAccessException, CPUInvalidOpcodeException, ControlBusUnknownSignalException {
          last_PC=PC;
                    
          opcodeByte1=(int)memoryBus.read(PC++);

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
write("NOP");
}

private void opcode__0x01()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI B
tmp1=((int)memoryBus.read(PC++));
tmp2=((int)memoryBus.read(PC++));
write(String.format("LXI BC,%04X    # B=%02X, C=%02X",(tmp2<<8)|tmp1,tmp2,tmp1));
}

private void opcode__0x02()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STAX B
write("STAX B");
}

private void opcode__0x03()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=BC
write("INX BC");
}

private void opcode__0x04()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=B
write("INR B");
}

private void opcode__0x05()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=B
write("DCR B");
}

private void opcode__0x06()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=B
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI B,%02X",tmp1));
}

private void opcode__0x07()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RLC
write("RLC");
}

private void opcode__0x09()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=BC
write("DAD BC");
}

private void opcode__0x0A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDAX B
write("LDAX B");
}

private void opcode__0x0B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=BC
write("DCX BC");
}

private void opcode__0x0C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=C
write("INR C");
}

private void opcode__0x0D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=C
write("DCR C");
}

private void opcode__0x0E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=C
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI C,%02X",tmp1));
}

private void opcode__0x0F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RRC
write("RRC");
}

private void opcode__0x11()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI D
tmp1=((int)memoryBus.read(PC++));
tmp2=((int)memoryBus.read(PC++));
write(String.format("LXI DE,%04X    # D=%02X, E=%02X",(tmp2<<8)|tmp1,tmp2,tmp1));
}

private void opcode__0x12()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STAX D
write("STAX D");
}

private void opcode__0x13()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=DE
write("INX DE");
}

private void opcode__0x14()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=D
write("INR D");
}

private void opcode__0x15()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=D
write("DCR D");
}

private void opcode__0x16()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=D
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI D,%02X",tmp1));
}

private void opcode__0x17()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RAL
write("RAL");
}

private void opcode__0x19()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=DE
write("DAD DE");
}

private void opcode__0x1A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDAX D
write("LDAX D");
}

private void opcode__0x1B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=DE
write("DCX DE");
}

private void opcode__0x1C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=E
write("INR E");
}

private void opcode__0x1D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=E
write("DCR E");
}

private void opcode__0x1E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=E
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI E,%02X",tmp1));
}

private void opcode__0x1F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RAR
write("RAR");
}

private void opcode__0x21()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI H
tmp1=((int)memoryBus.read(PC++));
tmp2=((int)memoryBus.read(PC++));
write(String.format("LXI HL,%04X    # H=%02X, L=%02X",(tmp2<<8)|tmp1,tmp2,tmp1));
}

private void opcode__0x22()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SHLD
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("SHLD %04X      # [%04X] = HL",tmp2,tmp2));
}

private void opcode__0x23()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=HL
write("INX HL");
}

private void opcode__0x24()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=H
write("INR H");
}

private void opcode__0x25()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=H
write("DCR H");
}

private void opcode__0x26()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=H
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI H,%02X",tmp1));
}

private void opcode__0x27()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAA
write("DAA");
}

private void opcode__0x29()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=HL
write("DAD HL");
}

private void opcode__0x2A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LHLD
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("LHLD %04X      # HL = [%04X]",tmp2,tmp2));
}

private void opcode__0x2B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=HL
write("DCX HL");
}

private void opcode__0x2C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=L
write("INR L");
}

private void opcode__0x2D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=L
write("DCR L");
}

private void opcode__0x2E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=L
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI L,%02X",tmp1));
}

private void opcode__0x2F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMA
write("CMA");
}

private void opcode__0x31()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LXI SP
tmp1=((int)memoryBus.read(PC++));
tmp1=(((int)memoryBus.read(PC++))<<8)|tmp1;
write(String.format("LXI SP,%04X    # SP = %04X",tmp1,tmp1));
}

private void opcode__0x32()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STA
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("STA %04X      # [%04X] = A",tmp2,tmp2));
}

private void opcode__0x33()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INX RP RP=SP
write("INX SP");
}

private void opcode__0x34()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR M
write("INR M");
}

private void opcode__0x35()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR M
write("DCR M");
}

private void opcode__0x36()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI M
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI M,%02X",tmp1));
}

private void opcode__0x37()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //STC
write("STC");
}

private void opcode__0x39()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DAD RP RP=SP
write("DAD SP");
}

private void opcode__0x3A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //LDA
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("LDA %04X      # A = [%04X]",tmp2,tmp2));
}

private void opcode__0x3B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCX RP RP=SP
write("DCX SP");
}

private void opcode__0x3C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //INR R DDD=A
write("INR A");
}

private void opcode__0x3D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DCR R DDD=A
write("DCR A");
}

private void opcode__0x3E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MVI R DDD=A
tmp1=((int)memoryBus.read(PC++));
write(String.format("MVI A,%02X",tmp1));
}

private void opcode__0x3F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMC
write("CMC");
}

private void opcode__0x40()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=B
write("MOV B,B");
}

private void opcode__0x41()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=C
write("MOV B,C");
}

private void opcode__0x42()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=D
write("MOV B,D");
}

private void opcode__0x43()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=E
write("MOV B,E");
}

private void opcode__0x44()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=H
write("MOV B,H");
}

private void opcode__0x45()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=L
write("MOV B,L");
}

private void opcode__0x46()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=B
write("MOV B,M");
}

private void opcode__0x47()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=B SSS=A
write("MOV B,A");
}

private void opcode__0x48()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=B
write("MOV C,B");
}

private void opcode__0x49()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=C
write("MOV C,C");
}

private void opcode__0x4A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=D
write("MOV C,D");
}

private void opcode__0x4B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=E
write("MOV C,E");
}

private void opcode__0x4C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=H
write("MOV C,H");
}

private void opcode__0x4D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=L
write("MOV C,L");
}

private void opcode__0x4E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=C
write("MOV C,M");
}

private void opcode__0x4F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=C SSS=A
write("MOV C,A");
}

private void opcode__0x50()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=B
write("MOV D,B");
}

private void opcode__0x51()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=C
write("MOV D,C");
}

private void opcode__0x52()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=D
write("MOV D,D");
}

private void opcode__0x53()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=E
write("MOV D,E");
}

private void opcode__0x54()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=H
write("MOV D,H");
}

private void opcode__0x55()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=L
write("MOV D,L");
}

private void opcode__0x56()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=D
write("MOV D,M");
}

private void opcode__0x57()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=D SSS=A
write("MOV D,A");
}

private void opcode__0x58()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=B
write("MOV E,B");
}

private void opcode__0x59()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=C
write("MOV E,C");
}

private void opcode__0x5A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=D
write("MOV E,D");
}

private void opcode__0x5B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=E
write("MOV E,E");
}

private void opcode__0x5C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=H
write("MOV E,H");
}

private void opcode__0x5D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=L
write("MOV E,L");
}

private void opcode__0x5E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=E
write("MOV E,M");
}

private void opcode__0x5F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=E SSS=A
write("MOV E,A");
}

private void opcode__0x60()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=B
write("MOV H,B");
}

private void opcode__0x61()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=C
write("MOV H,C");
}

private void opcode__0x62()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=D
write("MOV H,D");
}

private void opcode__0x63()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=E
write("MOV H,E");
}

private void opcode__0x64()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=H
write("MOV H,H");
}

private void opcode__0x65()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=L
write("MOV H,L");
}

private void opcode__0x66()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=H
write("MOV H,M");
}

private void opcode__0x67()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=H SSS=A
write("MOV H,A");
}

private void opcode__0x68()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=B
write("MOV L,B");
}

private void opcode__0x69()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=C
write("MOV L,C");
}

private void opcode__0x6A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=D
write("MOV L,D");
}

private void opcode__0x6B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=E
write("MOV L,E");
}

private void opcode__0x6C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=H
write("MOV L,H");
}

private void opcode__0x6D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=L
write("MOV L,L");
}

private void opcode__0x6E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=L
write("MOV L,M");
}

private void opcode__0x6F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=L SSS=A
write("MOV L,A");
}

private void opcode__0x70()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=B
write("MOV M,B");
}

private void opcode__0x71()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=C
write("MOV M,C");
}

private void opcode__0x72()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=D
write("MOV M,D");
}

private void opcode__0x73()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=E
write("MOV M,E");
}

private void opcode__0x74()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=H
write("MOV M,H");
}

private void opcode__0x75()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=L
write("MOV M,L");
}

private void opcode__0x76()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //HLT
write("HLT");
}

private void opcode__0x77()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV M,R SSS=A
write("MOV M,A");
}

private void opcode__0x78()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=B
write("MOV A,B");
}

private void opcode__0x79()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=C
write("MOV A,C");
}

private void opcode__0x7A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=D
write("MOV A,D");
}

private void opcode__0x7B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=E
write("MOV A,E");
}

private void opcode__0x7C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=H
write("MOV A,H");
}

private void opcode__0x7D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=L
write("MOV A,L");
}

private void opcode__0x7E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R,M DDD=A
write("MOV A,M");
}

private void opcode__0x7F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //MOV R1,R2 DDD=A SSS=A
write("MOV A,A");
}

private void opcode__0x80()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=B
write("ADD B");
}

private void opcode__0x81()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=C
write("ADD C");
}

private void opcode__0x82()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=D
write("ADD D");
}

private void opcode__0x83()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=E
write("ADD E");
}

private void opcode__0x84()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=H
write("ADD H");
}

private void opcode__0x85()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=L
write("ADD L");
}

private void opcode__0x86()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD M
write("ADD M");
}

private void opcode__0x87()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADD R SSS=A
write("ADD A");
}

private void opcode__0x88()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=B
write("ADC B");
}

private void opcode__0x89()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=C
write("ADC C");
}

private void opcode__0x8A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=D
write("ADC D");
}

private void opcode__0x8B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=E
write("ADC E");
}

private void opcode__0x8C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=H
write("ADC H");
}

private void opcode__0x8D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=L
write("ADC L");
}

private void opcode__0x8E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC M
write("ADC M");
}

private void opcode__0x8F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADC R SSS=A
write("ADC A");
}

private void opcode__0x90()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=B
write("SUB B");
}

private void opcode__0x91()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=C
write("SUB C");
}

private void opcode__0x92()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=D
write("SUB D");
}

private void opcode__0x93()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=E
write("SUB E");
}

private void opcode__0x94()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=H
write("SUB H");
}

private void opcode__0x95()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=L
write("SUB L");
}

private void opcode__0x96()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB M
write("SUB M");
}

private void opcode__0x97()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUB R SSS=A
write("SUB A");
}

private void opcode__0x98()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=B
write("SBB B");
}

private void opcode__0x99()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=C
write("SBB C");
}

private void opcode__0x9A()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=D
write("SBB D");
}

private void opcode__0x9B()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=E
write("SBB E");
}

private void opcode__0x9C()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=H
write("SBB H");
}

private void opcode__0x9D()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=L
write("SBB L");
}

private void opcode__0x9E()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R
write("SBB M");
}

private void opcode__0x9F()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBB R SSS=A
write("SBB A");
}

private void opcode__0xA0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=B
write("ANA B");
}

private void opcode__0xA1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=C
write("ANA C");
}

private void opcode__0xA2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=D
write("ANA D");
}

private void opcode__0xA3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=E
write("ANA E");
}

private void opcode__0xA4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=H
write("ANA H");
}

private void opcode__0xA5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=L
write("ANA L");
}

private void opcode__0xA6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA M
write("ANA M");
}

private void opcode__0xA7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANA R SSS=A
write("ANA A");
}

private void opcode__0xA8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=B
write("XRA B");
}

private void opcode__0xA9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=C
write("XRA C");
}

private void opcode__0xAA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=D
write("XRA D");
}

private void opcode__0xAB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=E
write("XRA E");
}

private void opcode__0xAC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=H
write("XRA H");
}

private void opcode__0xAD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=L
write("XRA L");
}

private void opcode__0xAE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA M
write("ANA M");
}

private void opcode__0xAF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRA R SSS=A
write("XRA A");
}

private void opcode__0xB0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=B
write("ORA B");
}

private void opcode__0xB1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=C
write("ORA C");
}

private void opcode__0xB2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=D
write("ORA D");
}

private void opcode__0xB3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=E
write("ORA E");
}

private void opcode__0xB4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=H
write("ORA H");
}

private void opcode__0xB5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=L
write("ORA L");
}

private void opcode__0xB6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA M
write("ORA M");
}

private void opcode__0xB7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORA R SSS=A
write("ORA A");
}

private void opcode__0xB8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=B
write("CMP B");
}

private void opcode__0xB9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=C
write("CMP C");
}

private void opcode__0xBA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=D
write("CMP D");
}

private void opcode__0xBB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=E
write("CMP E");
}

private void opcode__0xBC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=H
write("CMP H");
}

private void opcode__0xBD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=L
write("CMP L");
}

private void opcode__0xBE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP M
write("CMP M");
}

private void opcode__0xBF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CMP R SSS=A
write("CMP A");
}

private void opcode__0xC0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=NZ
write("RETC NZ");
}

private void opcode__0xC1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP B
write("POP B");
}

private void opcode__0xC2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=NZ
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC NZ, %04X",tmp2));
}

private void opcode__0xC3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JMP
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JMP %04X",tmp2));
}

private void opcode__0xC4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=NZ
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC NZ, %04X",tmp2));
}

private void opcode__0xC5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH B
write("PUSH B");
}

private void opcode__0xC6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ADI
tmp2=((int)memoryBus.read(PC++));
write(String.format("ADI %02X",tmp2));
}

private void opcode__0xC7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=0
write(String.format("RST %01X     # PC = %02X", 0 , opcodeByte1&0x38));
}

private void opcode__0xC8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=Z
write("RETC Z");
}

private void opcode__0xC9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RET
write("RET");
}

private void opcode__0xCA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=Z
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC Z, %04X",tmp2));
}

private void opcode__0xCC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=Z
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC Z, %04X",tmp2));
}

private void opcode__0xCD()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALL
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALL %04X",tmp2));
}

private void opcode__0xCE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ACI
tmp2=((int)memoryBus.read(PC++));
write(String.format("ACI %02X",tmp2));
}

private void opcode__0xCF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=1
write(String.format("RST %01X     # PC = %02X", 1 , opcodeByte1&0x38));
}

private void opcode__0xD0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=NC
write("RETC NC");
}

private void opcode__0xD1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP D
write("POP D");
}

private void opcode__0xD2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=NC
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC NC, %04X",tmp2));
}

private void opcode__0xD3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //OUT
tmp1=((int)memoryBus.read(PC++));
write(String.format("OUT %02X",tmp1));
}

private void opcode__0xD4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=NC
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC NC, %04X",tmp2));
}

private void opcode__0xD5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH D
write("PUSH D");
}

private void opcode__0xD6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SUI
tmp2=((int)memoryBus.read(PC++));
write(String.format("SUI %02X",tmp2));
}

private void opcode__0xD7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=2
write(String.format("RST %01X     # PC = %02X", 2 , opcodeByte1&0x38));
}

private void opcode__0xD8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=C
write("RETC C");
}

private void opcode__0xDA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=C
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC C, %04X",tmp2));
}

private void opcode__0xDB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //IN
tmp1=((int)memoryBus.read(PC++));
write(String.format("IN %02X",tmp1));
}

private void opcode__0xDC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=C
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC C, %04X",tmp2));
}

private void opcode__0xDE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SBI
tmp2=((int)memoryBus.read(PC++));
write(String.format("SBI %02X",tmp2));
}

private void opcode__0xDF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=3
write(String.format("RST %01X     # PC = %02X", 3 , opcodeByte1&0x38));
}

private void opcode__0xE0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=PO
write("RETC PO");
}

private void opcode__0xE1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP H
write("POP H");
}

private void opcode__0xE2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=PO
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC PO, %04X",tmp2));
}

private void opcode__0xE3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XTHL
write("XTHL");
}

private void opcode__0xE4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=PO
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC PO, %04X",tmp2));
}

private void opcode__0xE5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH H
write("PUSH H");
}

private void opcode__0xE6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ANI
tmp1=((int)memoryBus.read(PC++));
write(String.format("ANI %02X",tmp1));
}

private void opcode__0xE7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=4
write(String.format("RST %01X     # PC = %02X", 4 , opcodeByte1&0x38));
}

private void opcode__0xE8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=PE
write("RETC PE");
}

private void opcode__0xE9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PCHL
write("PCHL");
}

private void opcode__0xEA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=PE
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC PE, %04X",tmp2));
}

private void opcode__0xEB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XCHG
write("XCHG");
}

private void opcode__0xEC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=PE
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC PE, %04X",tmp2));
}

private void opcode__0xEE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //XRI
tmp1=((int)memoryBus.read(PC++));
write(String.format("XRI %02X",tmp1));
}

private void opcode__0xEF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=5
write(String.format("RST %01X     # PC = %02X", 5 , opcodeByte1&0x38));
}

private void opcode__0xF0()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=P
write("RETC P");
}

private void opcode__0xF1()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //POP PSW
write("POP PSW");
}

private void opcode__0xF2()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=P
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC P, %04X",tmp2));
}

private void opcode__0xF3()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //DI
write("DI");
}

private void opcode__0xF4()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=P
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC P, %04X",tmp2));
}

private void opcode__0xF5()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //PUSH PSW
write("PUSH PSW");
}

private void opcode__0xF6()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //ORI
tmp1=((int)memoryBus.read(PC++));
write(String.format("ORI %02X",tmp1));
}

private void opcode__0xF7()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=6
write(String.format("RST %01X     # PC = %02X", 6 , opcodeByte1&0x38));
}

private void opcode__0xF8()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RETC CCC=M
write("RETC M");
}

private void opcode__0xF9()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //SPHL
write("SPHL");
}

private void opcode__0xFA()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //JC CCC=M
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("JC M, %04X",tmp2));
}

private void opcode__0xFB()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //EI
write("EI");
}

private void opcode__0xFC()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CALLC CCC=M
tmp1=((int)memoryBus.read(PC++));
tmp2=((((int)memoryBus.read(PC++)))<<8)|tmp1;
write(String.format("CALLC M, %04X",tmp2));
}

private void opcode__0xFE()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //CPI
tmp1=((int)memoryBus.read(PC++));
write(String.format("CPI %02X",tmp1));
}

private void opcode__0xFF()  throws MemoryAccessException, ControlBusUnknownSignalException, CPUInvalidOpcodeException {
 //RST AAA=7
write(String.format("RST %01X     # PC = %02X", 7 , opcodeByte1&0x38));
}

}