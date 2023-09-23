package jss.devices.cpu.tests;

import jss.configuration.ConfigurationValue;
import jss.configuration.DeviceConfiguration;
import jss.devices.bus.ControlBus;
import jss.devices.bus.DataBus;
import jss.devices.bus.impl.ControlBusBasic;
import jss.devices.bus.impl.DataBusNoError;
import jss.devices.cpu.CPUInvalidOpcodeException;
import jss.devices.cpu.impl.Intel8088;
import jss.devices.memory.MemoryAccessException;
import jss.devices.memory.impl.MemoryW8D8;

public class TestIntel8088 {

	public static void check(long actual, long desired) throws Exception {
		if (actual != desired)
			throw new Exception();
	}
	
	// Flags = ----|O|D|I|T|S|Z|-|A|-|P|-|C
	static final int F_C=0x0001;
	static final int F_P=0x0004;
	static final int F_A=0x0010;
	static final int F_Z=0x0040;
	static final int F_S=0x0080;
	static final int F_T=0x0100;
	static final int F_I=0x0200;
	static final int F_D=0x0400;
	static final int F_O=0x0800;
	
	static byte[] mov_cs_ax=new byte[] { (byte) 0x8E, (byte) 0xC8 };
	static byte[] mov_ah_0=new byte[] { (byte) 0xB4, 0x00 };
	static byte[] mov_ah_1=new byte[] { (byte) 0xB4, 0x01 };
	static byte[] mov_ax_5=new byte[] { (byte) 0xB8, 0x05, 0x00 };
	static byte[] mov_ax_m5=new byte[] { (byte) 0xB8, (byte) 0xFB, (byte) 0xFF };
	static byte[] mov_ax_13=new byte[] { (byte) 0xB8, 0x13, 0x00 };
	static byte[] mov_ax_4000=new byte[] { (byte) 0xB8, 0x00, 0x40 };
	static byte[] mov_ax_8000=new byte[] { (byte) 0xB8, 0x00, (byte)0x80 };
	static byte[] mov_ax_b800=new byte[] { (byte) 0xB8, 0x00, (byte)0xb8 };
	static byte[] mov_ax_aaaa=new byte[] { (byte) 0xB8, (byte)0xAA, (byte)0xAA };
	static byte[] mov_cx_1=new byte[] { (byte) 0xB9, 0x01, 0x00 };
	static byte[] mov_cx_3=new byte[] { (byte) 0xB9, 0x03, 0x00 };
	static byte[] mov_cx_ff=new byte[] { (byte) 0xB9, (byte)0xFF, 0x00 };
	static byte[] mov_cx_bbbb=new byte[] { (byte) 0xB9, (byte)0xBB, (byte)0xBB };
	static byte[] mov_cx_m3=new byte[] { (byte) 0xB9, (byte) 0xFD, (byte) 0xFF };
	static byte[] mov_cx_e=new byte[] { (byte) 0xB9, (byte)0x0E, 0x00 };
	static byte[] mov_dx_0=new byte[] { (byte) 0xBA, 0x00, 0x00 };
	static byte[] mov_si_0=new byte[] { (byte) 0xBE, 0x00, 0x00 };
	static byte[] mov_di_0=new byte[] { (byte) 0xBF, 0x00, 0x00 };
	static byte[] mov_di_4=new byte[] { (byte) 0xBF, 0x04, 0x00 };
	static byte[] mov_ax_cs=new byte[] { (byte) 0x8C, (byte) 0xC8 };
	static byte[] mov_ds_ax=new byte[] { (byte) 0x8E, (byte) 0xD8 };
	static byte[] mov_es_ax=new byte[] { (byte) 0x8E, (byte) 0xC0 };
	static byte[] mov_es_di_11=new byte[] { (byte) 0x26, (byte) 0xC6, 0x05, 0x11 }; // ES: MOV BYTE PTR [DI],11
	static byte[] mov_es_di_1_12=new byte[] { (byte) 0x26, (byte) 0xC6, 0x45, 0x01, 0x12 }; // ES: MOV BYTE PTR [DI+1],12
	static byte[] mov_es_di_2_13=new byte[] { (byte) 0x26, (byte) 0xC6, 0x45, 0x02, 0x13 }; // ES: MOV BYTE PTR [DI+2],13
	
	static byte[] sahf=new byte[] { (byte) 0x9E };
	static byte[] div_cl=new byte[] { (byte) 0xF6, (byte) 0xF1 };
	static byte[] idiv_cx=new byte[] { (byte) 0xF7, (byte) 0xF9 };
	static byte[] idiv_cl=new byte[] { (byte) 0xF6, (byte) 0xF9 };
	static byte[] not_ax=new byte[] { (byte) 0xF7, (byte) 0xD0 };
	static byte[] shl_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xE0 };
	static byte[] shr_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xE8 };
	static byte[] sar_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xF8 };
	static byte[] rol_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xC0 };
	static byte[] ror_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xC8 };
	static byte[] rcl_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xD0 };
	static byte[] rcr_ax_cl=new byte[] { (byte) 0xD3, (byte) 0xD8 };
	static byte[] and_ax_ax=new byte[] { (byte) 0x21, (byte) 0xC0 };
	static byte[] and_ax_cx=new byte[] { (byte) 0x21, (byte) 0xC8 };
	static byte[] and_ax_5555=new byte[] { (byte) 0x25, (byte) 0x55, (byte) 0x55 };
	static byte[] test_ax_ax=new byte[] { (byte) 0x85, (byte) 0xC0 };
	static byte[] test_ax_cx=new byte[] { (byte) 0x85, (byte) 0xC1 };
	static byte[] test_ax_5555=new byte[] { (byte) 0xa9, (byte) 0x55, (byte) 0x55 };
	static byte[] or_ax_ax=new byte[] { (byte) 0x09, (byte) 0xC0 };
	static byte[] or_ax_cx=new byte[] { (byte) 0x09, (byte) 0xC8 };
	static byte[] or_ax_5555=new byte[] { (byte) 0x0D, (byte) 0x55, (byte) 0x55 };
	static byte[] xor_ax_ax=new byte[] { (byte) 0x31, (byte) 0xC0 };
	static byte[] xor_ax_cx=new byte[] { (byte) 0x31, (byte) 0xC8 };
	static byte[] xor_ax_5555=new byte[] { (byte) 0x35, (byte) 0x55, (byte) 0x55 };
	static byte[] repz=new byte[] { (byte) 0xF3 };
	static byte[] repnz=new byte[] { (byte) 0xF2 };
	static byte[] movsb=new byte[] { (byte) 0xA4 };
	static byte[] movsw=new byte[] { (byte) 0xA5 };
	static byte[] cmpsb=new byte[] { (byte) 0xA6 };
	static byte[] cmpsw=new byte[] { (byte) 0xA7 };
	static byte[] scasb=new byte[] { (byte) 0xAE };
	static byte[] scasw=new byte[] { (byte) 0xAF };
	static byte[] lodsb=new byte[] { (byte) 0xAC };
	static byte[] lodsw=new byte[] { (byte) 0xAD };
	static byte[] stosb=new byte[] { (byte) 0xAA };
	static byte[] stosw=new byte[] { (byte) 0xAB };
	
	public static void writeProgram(MemoryW8D8 mem, int offset, byte[][]prog) throws MemoryAccessException {
		int addr=offset;
		
		for(int i=0;i<prog.length;i++) {
			for(int j=0;j<prog[i].length;j++) {
				mem.write(addr++, prog[i][j]);
			}
		}
	}
	
	public static void printCPUState(Intel8088 cpu) {
		System.out.println(String.format("AX=%04X  BX=%04X  CX=%04X  DX=%04X  SP=%04X  BP=%04X  SI=%04X  DI=%04X\nDS=%04X  ES=%04X  SS=%04X  CS=%04X  IP=%04X  FLAGS=%04X %s",
				cpu.getRegister("AX"),cpu.getRegister("BX"),cpu.getRegister("CX"),cpu.getRegister("DX"),
				cpu.getRegister("SP"),cpu.getRegister("BP"),cpu.getRegister("SI"),cpu.getRegister("DI"),
				cpu.getRegister("DS"),cpu.getRegister("ES"),cpu.getRegister("SS"),cpu.getRegister("CS"),
				cpu.getRegister("IP"),cpu.getRegister("FLAGS"),getFlagsString(cpu.getRegister("FLAGS"))));
	}
	
	public static String getFlagsString(long flags) {
		char[] s= "----ODITSZ-A-P-C".toCharArray();
		if((flags & F_C)==0) s[15]='c';
		if((flags & 0x2)!=0) s[14]='X';
		if((flags & F_P)==0) s[13]='p';
		if((flags & 0x8)!=0) s[12]='X';
		if((flags & F_A)==0) s[11]='a';
		if((flags & 0x20)!=0) s[10]='X';
		if((flags & F_Z)==0) s[9]='z';
		if((flags & F_S)==0) s[8]='s';
		if((flags & F_T)==0) s[7]='t';
		if((flags & F_I)==0) s[6]='i';
		if((flags & F_D)==0) s[5]='d';
		if((flags & F_O)==0) s[4]='o';
		if((flags & 0x1000)!=0) s[3]='X';
		if((flags & 0x2000)!=0) s[2]='X';
		if((flags & 0x4000)!=0) s[1]='X';
		if((flags & 0x8000)!=0) s[0]='X';
		
		return String.valueOf(s);
	}
	
	public static void runExternalTest(String testRes, String validateRes, MemoryW8D8 rom, MemoryW8D8 mem, Intel8088 cpu,MemoryW8D8 memValidate, int flags)   throws MemoryAccessException, Exception {
		runExternalTest(testRes, validateRes, rom, mem, cpu, memValidate, flags,null); 
	}
	
	public static void runExternalTest(String testRes, String validateRes, MemoryW8D8 rom, MemoryW8D8 mem, Intel8088 cpu,MemoryW8D8 memValidate, int flags, DataBus memBus)   throws MemoryAccessException, Exception {
		System.out.println("Running test ["+testRes+"]");
		
		mem.initialize();
		rom.initialize();
		cpu.initialize();
		
		cpu.setRegister("FLAGS", flags);
		
		// If not set, the tests execute a jump to INT 0
		cpu.setRegister("CS",0xF000);
		cpu.setRegister("IP",0xFFF0);
		
		printCPUState(cpu);
		
		rom.loadBin(rom.getClass().getResource(testRes), false, 0);
		int stepNum=0;
		while(!cpu.isFlag_halt() && stepNum<1000) {
			stepNum++;
			try {
				if(memBus!=null) {
					printCPUState(cpu);
					System.out.println(String.format("%x %x %x %x", 
							memBus.read((cpu.getRegister("CS")<<4)+cpu.getRegister("IP")),
							memBus.read((cpu.getRegister("CS")<<4)+cpu.getRegister("IP")+1),
							memBus.read((cpu.getRegister("CS")<<4)+cpu.getRegister("IP")+2),
							memBus.read((cpu.getRegister("CS")<<4)+cpu.getRegister("IP")+3)
					));
				}
				cpu.step();
			}catch(CPUInvalidOpcodeException ex) {
				System.out.println("Invalid opcode: "+ex.getOpcode()+" at step: "+stepNum);
				//continue;
				throw ex;
			}
		}
		System.out.println("Test ended at step ["+stepNum+"]");
		printCPUState(cpu);
		if(!cpu.isFlag_halt()) {
			throw new Exception("Not halted");
		}
		
		memValidate.initialize();
		memValidate.loadBin(rom.getClass().getResource(validateRes), false,0);
		
		System.out.println("Validating memory");
		long error=0;
		for(int i=0;i<1024;i++) {
			if(mem.read(i)!=memValidate.read(i)) {
				System.out.println(String.format("Error at byte (%d)[%04x]! Expected: [%02x][%04x][%s] Actual: [%02x][%04x][%s]",
						i,i,memValidate.read(i),memValidate.read(i&0xFFFE) | (memValidate.read((i&0xFFFE)+1)<<8),
						getFlagsString(memValidate.read(i&0xFFFE) | (memValidate.read((i&0xFFFE)+1)<<8)),
						mem.read(i),mem.read(i&0xFFFE) | (mem.read((i&0xFFFE)+1)<<8),
						getFlagsString(mem.read(i&0xFFFE) | (mem.read((i&0xFFFE)+1)<<8))
				));
				error=1;
			}
		}
		
		// Certain flags are "undefined" for certain instructions in 8088
		if(!testRes.endsWith("bcdcnv.bin") && 
				!testRes.endsWith("sub.bin") && 
				!testRes.endsWith("div.bin") && 
				!testRes.endsWith("shifts.bin") &&
				!testRes.endsWith("mul.bin") &&
				!testRes.endsWith("rotate.bin") &&
				!testRes.endsWith("segpr.bin")
		) {
			check(error,0);
			System.out.println("OK");
		}else {
			if(error!=0)System.out.println("*************** ERRROR *******************");
			else System.out.println("OK");
		}
	}
	
	public static void doExternalTestSuite()  throws MemoryAccessException, Exception {
		// ROM
		DeviceConfiguration configROM = new DeviceConfiguration("","");
		configROM.set("size", new ConfigurationValue(64*1024)); // 64Kb
		configROM.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW8D8 rom = new MemoryW8D8();
		rom.configure(configROM,null);
		rom.initialize();

		// Memory
		DeviceConfiguration configMEM = new DeviceConfiguration("","");
		configMEM.set("size", new ConfigurationValue(1024*1024-64*1024)); // 1Mb - 64Kb (ROM)
		configMEM.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW8D8 mem = new MemoryW8D8();
		mem.configure(configMEM,null);
		mem.initialize();
		
		// Memory BUS
		DeviceConfiguration configMemoryBus = new DeviceConfiguration("","");
		DataBus memoryBus=new DataBusNoError();
		memoryBus.configure(configMemoryBus,null);
		memoryBus.initialize();
		memoryBus.attachDataDevice(rom, 0xF0000, 0xFFFFF, 0xF0000,"",true); // last 64k
		memoryBus.attachDataDevice(mem, 0, mem.getMem().length, 0,"",true);
		
		// Control BUS
		DeviceConfiguration configControlBus = new DeviceConfiguration("","");
		configControlBus.set("signals", new ConfigurationValue("INT"));
		ControlBus controlBus=new ControlBusBasic();
		controlBus.configure(configControlBus,null);
		controlBus.initialize();
		
		// CPU
		DeviceConfiguration configCPU = new DeviceConfiguration("","");
		Intel8088 cpu = new Intel8088();
		cpu.configure(configCPU,null);
		cpu.initialize();
		cpu.attachToDataBus(memoryBus);
		cpu.attachToControlBus(controlBus);
		
		MemoryW8D8 memValidate = new MemoryW8D8();
		memValidate.configure(configMEM,null);
		memValidate.initialize();

		// CPU differences are described in the 80386 Programmer's Reference Manual, Section 15.6: 
		// https://pdos.csail.mit.edu/6.828/2018/readings/i386/s15_06.htm 
		runExternalTest("/res/8086Tests/80186_tests/add.bin","/res/8086Tests/80186_tests/res_add.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/bcdcnv.bin","/res/8086Tests/80186_tests/res_bcdcnv.bin",rom,mem,cpu,memValidate,0x0046);
		runExternalTest("/res/8086Tests/80186_tests/bitwise.bin","/res/8086Tests/80186_tests/res_bitwise.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/cmpneg.bin","/res/8086Tests/80186_tests/res_cmpneg.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/control.bin","/res/8086Tests/80186_tests/res_control.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/datatrnf.bin","/res/8086Tests/80186_tests/res_datatrnf.bin",rom,mem,cpu,memValidate,0x0002);
		
		// There was an error in the original test:
		// On 8086, 8088, 80186, and 80188 processors, the return address on the stack points at the next instruction after the divide instruction. On the 80286 and later processors, the
		// return address points at the beginning of the divide instruction (include any prefix bytes that appear).
		runExternalTest("/res/8086Tests/80186_tests/div.bin","/res/8086Tests/80186_tests/res_div.bin",rom,mem,cpu,memValidate,0x0002);
		//runExternalTest("/res/8086Tests/80186_tests/interrupt.bin","/res/8086Tests/80186_tests/res_interrupt.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/jump1.bin","/res/8086Tests/80186_tests/res_jump1.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/jump2.bin","/res/8086Tests/80186_tests/res_jump2.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/mul.bin","/res/8086Tests/80186_tests/res_mul.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/rep.bin","/res/8086Tests/80186_tests/res_rep.bin",rom,mem,cpu,memValidate,0x0002);
		
		// There was an error in the original test:
		// On 8088 there is no restriction on bit rotations; on newer CPUs only 5 bits are used in CL (it's a mod 32)
		runExternalTest("/res/8086Tests/80186_tests/rotate.bin","/res/8086Tests/80186_tests/res_rotate.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/segpr.bin","/res/8086Tests/80186_tests/res_segpr.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/shifts.bin","/res/8086Tests/80186_tests/res_shifts.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/strings.bin","/res/8086Tests/80186_tests/res_strings.bin",rom,mem,cpu,memValidate,0x0002);
		runExternalTest("/res/8086Tests/80186_tests/sub.bin","/res/8086Tests/80186_tests/res_sub.bin",rom,mem,cpu,memValidate,0x0002);
	}

	public static void main(String[] args) throws MemoryAccessException, Exception {
		System.out.println("Testing Intel8088");

		System.out.println("Basic Tests");
		
		// ROM
		DeviceConfiguration configROM = new DeviceConfiguration("","");
		configROM.set("size", new ConfigurationValue(10));
		configROM.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW8D8 rom = new MemoryW8D8();
		rom.configure(configROM,null);
		rom.initialize();
		System.arraycopy(mov_cs_ax, 0, rom.getMem(), 0, mov_cs_ax.length);
		
		// Memory
		DeviceConfiguration configMEM = new DeviceConfiguration("","");
		configMEM.set("size", new ConfigurationValue(4096));
		configMEM.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW8D8 mem = new MemoryW8D8();
		mem.configure(configMEM,null);
		mem.initialize();

		DeviceConfiguration configMEMVideo = new DeviceConfiguration("","");
		configMEMVideo.set("size", new ConfigurationValue(4096));
		configMEMVideo.set("initialization_policy", new ConfigurationValue("ZERO"));
		MemoryW8D8 memVideo = new MemoryW8D8();
		memVideo.configure(configMEMVideo,null);
		memVideo.initialize();

		// Memory BUS
		DeviceConfiguration configMemoryBus = new DeviceConfiguration("","");
		DataBus memoryBus=new DataBusNoError();
		memoryBus.configure(configMemoryBus,null);
		memoryBus.initialize();
		memoryBus.attachDataDevice(rom, 0xFFFF0, 0xFFFF0+10, 0xFFFF0,"",true);
		memoryBus.attachDataDevice(mem, 0, 4096, 0,"",true);
		memoryBus.attachDataDevice(memVideo, 0xB0000,  0xB0000+4096, 0xB0000,"",true); // B000:0 MDA Display RAM
		memoryBus.attachDataDevice(memVideo, 0xB8000,  0xB8000+4096, 0xB8000,"",true); // B800:0 CGA Display RAM
		
		// Control BUS
		DeviceConfiguration configControlBus = new DeviceConfiguration("","");
		configControlBus.set("signals", new ConfigurationValue("INT"));
		ControlBus controlBus=new ControlBusBasic();
		controlBus.configure(configControlBus,null);
		controlBus.initialize();
		
		// CPU
		DeviceConfiguration configCPU = new DeviceConfiguration("","");
		Intel8088 cpu = new Intel8088();
		cpu.configure(configCPU,null);
		cpu.initialize();
		cpu.attachToDataBus(memoryBus);
		cpu.attachToControlBus(controlBus);
		
		check(cpu.getRegister("CS"),0xFFFF);
		check(cpu.getRegister("IP"),0);
		check(cpu.getRegister("AX"),0);
		
		cpu.step();

		check(cpu.getRegister("CS"),0);
		check(cpu.getRegister("IP"),2);

		writeProgram(mem,2,new byte[][] {mov_ax_5, mov_cx_3, div_cl});
		cpu.step();
		check(cpu.getRegister("AX"),5);
		cpu.step();
		check(cpu.getRegister("CX"),3);
		cpu.step();
		check(cpu.getRegister("AX"),0x0201);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_m3, idiv_cx});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xFFFF);
		check(cpu.getRegister("DX"),0x0002);
		
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_m3, mov_dx_0, idiv_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x02FF);
		check(cpu.getRegister("DX"),0x0000);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {not_ax});
		cpu.step();
		check(cpu.getRegister("AX"),0xFD00);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_3, shl_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x28);
		check(cpu.getRegister("FLAGS") & F_C,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_e, shl_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x4000);
		check(cpu.getRegister("FLAGS") & F_C,F_C);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_1, shr_ax_cl, shr_ax_cl, shr_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x2);
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,0);
		cpu.step();
		check(cpu.getRegister("AX"),0x0);
		check(cpu.getRegister("FLAGS") & F_C,F_C);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_m5, mov_cx_1, shr_ax_cl, shr_ax_cl, shr_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x1FFF);
		check(cpu.getRegister("FLAGS") & F_C,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_1, sar_ax_cl, sar_ax_cl, sar_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x2);
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,0);
		cpu.step();
		check(cpu.getRegister("AX"),0x0);
		check(cpu.getRegister("FLAGS") & F_C,F_C);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_m5, mov_cx_1, sar_ax_cl, sar_ax_cl, sar_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xFFFF);
		check(cpu.getRegister("FLAGS") & F_C,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_m5, mov_cx_ff, sar_ax_cl, sar_ax_cl, sar_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xFFFF);
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xFFFF);
		check(cpu.getRegister("FLAGS") & F_C,F_C);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_5, mov_cx_3, rol_ax_cl, mov_ax_m5, rol_ax_cl, mov_ax_4000, mov_cx_1, rol_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x28);
		check(cpu.getRegister("CX"),0x03);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,0);
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xFFDF);
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		check(cpu.getRegister("FLAGS") & F_O,0);
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x8000);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,F_O);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_4000, mov_cx_1, ror_ax_cl, mov_ax_5, ror_ax_cl});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x2000);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,0);
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x8002);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		check(cpu.getRegister("FLAGS") & F_O,F_O);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ah_0, sahf, mov_ax_5, mov_cx_1, rcl_ax_cl});
		cpu.step();
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,0);
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x0A);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ah_1, sahf, mov_ax_5, mov_cx_1, rcl_ax_cl});
		cpu.step();
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x0B);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ah_0, sahf, mov_ax_8000, mov_cx_1, rcl_ax_cl,rcl_ax_cl});
		cpu.step();
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,0);
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x0000);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		check(cpu.getRegister("FLAGS") & F_O,F_O);
		cpu.step();
		check(cpu.getRegister("AX"),0x0001);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ah_0, sahf, mov_ax_5, mov_cx_1, rcr_ax_cl,rcr_ax_cl});
		cpu.step();
		cpu.step();
		check(cpu.getRegister("FLAGS") & F_C,0);
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x0002);
		check(cpu.getRegister("CX"),0x01);
		check(cpu.getRegister("FLAGS") & F_C,F_C);
		check(cpu.getRegister("FLAGS") & F_O,0);
		cpu.step();
		check(cpu.getRegister("AX"),0x8001);
		check(cpu.getRegister("FLAGS") & F_C,0);
		check(cpu.getRegister("FLAGS") & F_O,F_O);
		
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_aaaa, mov_cx_bbbb, and_ax_ax, and_ax_cx, and_ax_5555});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS"),0x86);
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS"),0x86);
		cpu.step();
		check(cpu.getRegister("AX"),0x0);
		check(cpu.getRegister("FLAGS") & F_Z, F_Z);
		check(cpu.getRegister("FLAGS") & F_S, 0);
		
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_aaaa, mov_cx_bbbb, test_ax_ax, test_ax_cx, test_ax_5555});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS"),0x86);
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS"),0x86);
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS") & F_Z, F_Z);
		check(cpu.getRegister("FLAGS") & F_S, 0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_aaaa, mov_cx_bbbb, or_ax_ax, or_ax_cx, or_ax_5555});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("FLAGS") & F_S,F_S);
		cpu.step();
		check(cpu.getRegister("AX"),0xbbbb);
		check(cpu.getRegister("FLAGS") & F_S,F_S);
		cpu.step();
		check(cpu.getRegister("AX"),0xffff);
		check(cpu.getRegister("FLAGS") & F_S,F_S);
		check(cpu.getRegister("FLAGS") & F_Z,0);

		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_aaaa, mov_cx_bbbb, xor_ax_ax, xor_ax_cx, xor_ax_5555});
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("AX"),0x00);
		check(cpu.getRegister("FLAGS") & F_Z,F_Z);
		check(cpu.getRegister("FLAGS") & F_S,0);
		cpu.step();
		check(cpu.getRegister("AX"),0xbbbb);
		check(cpu.getRegister("FLAGS") & F_Z,0);
		check(cpu.getRegister("FLAGS") & F_S,F_S);
		cpu.step();
		check(cpu.getRegister("AX"),0xeeee);
		check(cpu.getRegister("FLAGS") & F_S,F_S);
		check(cpu.getRegister("FLAGS") & F_Z,0);

		writeProgram(mem,0,new byte[][] {{(byte)0xAB,0x12,0x00,0x00,(byte)0xBA,0x21,0x00,0x00}});
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_si_0, mov_di_4, mov_ax_cs, mov_ds_ax, mov_ax_b800,mov_es_ax, mov_ax_aaaa,movsb,movsw});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("SI"),0x00);
		check(cpu.getRegister("DI"),0x04);
		check(cpu.getRegister("CS"),cpu.getRegister("DS"));
		check(cpu.getRegister("ES"),0xB800);
		check(cpu.getRegister("AX"),0xB800);
		cpu.step();
		check(cpu.getRegister("AX"),0xaaaa);
		cpu.step(); // MOVSB
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("SI"),0x01);
		check(cpu.getRegister("DI"),0x05);
		check(memVideo.read(4),mem.read(0));
		check(mem.read(4),0xBA);
		cpu.step(); // MOVSW
		check(cpu.getRegister("AX"),0xaaaa);
		check(cpu.getRegister("SI"),0x03);
		check(cpu.getRegister("DI"),0x07);
		check(memVideo.read(5),mem.read(1));
		check(memVideo.read(6),mem.read(2));
		check(mem.read(5),0x21);
		
		writeProgram(mem,0,new byte[][] {{(byte)0xAB,0x12,0x00,0x00,(byte)0xBA,0x21,0x00,0x00}});
		writeProgram(memVideo,0,new byte[][] {{0,0,0,0,0,0,0,0}});
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_si_0, mov_di_4, mov_ax_cs, mov_ds_ax, mov_ax_b800,mov_es_ax, mov_ax_aaaa,mov_cx_ff,repz,movsb,mov_si_0, mov_di_4,mov_cx_ff,repz,cmpsb});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		for(int IP=cpu.getRegister("IP");IP==cpu.getRegister("IP");)
			cpu.step(); // repz movsb
		check(cpu.getRegister("SI"),0xFF);
		check(cpu.getRegister("DI"),0x103);
		check(cpu.getRegister("CS"),cpu.getRegister("DS"));
		check(cpu.getRegister("ES"),0xB800);
		check(cpu.getRegister("AX"),0xAAAA);
		for(int i=0;i<0xFF;i++)check(memVideo.read(i+4),mem.read(i));
		check(cpu.getRegister("CX"),0x0000);
		cpu.step();
		cpu.step();
		cpu.step();
		for(int IP=cpu.getRegister("IP");IP==cpu.getRegister("IP");)
			cpu.step(); // repz cmpsb
		check(cpu.getRegister("CX"),0x0000);
		check(cpu.getRegister("FLAGS") & F_Z,F_Z);
		check(cpu.getRegister("SI"),0xFF);
		check(cpu.getRegister("DI"),0x103);

		writeProgram(memVideo,0,new byte[][] {{0,0,0,0,0,0,0,0}});
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_b800, mov_es_ax, mov_di_0, mov_es_di_11, mov_es_di_1_12, mov_es_di_2_13,mov_cx_ff,mov_ax_13,repnz,scasb});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(memVideo.read(0),0x11);
		check(memVideo.read(1),0x12);
		check(memVideo.read(2),0x13);
		check(cpu.getRegister("ES"),0xB800);
		check(cpu.getRegister("DI"),0x0);
		check(cpu.getRegister("AX"),0x13);
		check(cpu.getRegister("CX"),0xFF);
		for(int IP=cpu.getRegister("IP");IP==cpu.getRegister("IP");)
			cpu.step(); // repz scasb
		check(cpu.getRegister("CX"),0xFC);
		check(cpu.getRegister("DI"),0x03);
		
		writeProgram(mem,0,new byte[][] {{0x45}});
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_cs, mov_ds_ax, mov_si_0, lodsb});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		check(cpu.getRegister("SI"),0x01);
		check(cpu.getRegister("AX"),0x45);

		writeProgram(memVideo,0,new byte[][] {{0,0,0,0,0,0,0,0}});
		writeProgram(mem,cpu.getRegister("IP"),new byte[][] {mov_ax_b800, mov_es_ax, mov_di_0, mov_cx_3, mov_ax_13, repz, stosw});
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		cpu.step();
		for(int IP=cpu.getRegister("IP");IP==cpu.getRegister("IP");)
			cpu.step(); // repz stosw
		check(cpu.getRegister("DI"),0x06);
		for(int i=0;i<3;i++) {
			check(memVideo.read(i*2),0x13);
			check(memVideo.read(i*2+1),0);
		}
		
		System.out.println("    OK");
		
		System.out.println("Running test suite");
		doExternalTestSuite();
	}

}
