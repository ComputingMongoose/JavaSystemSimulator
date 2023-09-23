package jss.devices.cpu;

public class CPURegister {

	private String name;
	private int szBits;
	private long value;
	
	public CPURegister() {;}
	
	
	public CPURegister(String name, int szBits, long value) {
		this.name=name;
		this.szBits=szBits;
		this.value=value;
	}

	public CPURegister clone() {
		return new CPURegister(this.name,this.szBits,this.value);
	}

	
	public String getName() {
		return name;
	}

	public int getSzBits() {
		return szBits;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	public String getValueHex() {
		int digits=(int) Math.ceil( (double)szBits/(double)4.0 );
		return String.format("%0"+digits+"X", this.value);
	}
	
}
