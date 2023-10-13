package jss.configuration;

import java.util.HashMap;
import java.util.Map;

public class DeviceConfiguration implements Cloneable {

	private String name;
	private String type;
	
	private HashMap<String,ConfigurationValue> config;
	
	public DeviceConfiguration clone() {
		DeviceConfiguration d=new DeviceConfiguration(this.name,this.type);
		
		for(Map.Entry<String,ConfigurationValue> e : this.config.entrySet()) {
			this.config.put(e.getKey(),e.getValue());
		}
		
		return d;
	}
	
	public DeviceConfiguration(String name, String type) {
		this.config=new HashMap<>(100);
		this.name=name;
		this.type=type;
	}
	
	public boolean contains(String name) {
		return this.config.containsKey(name);
	}
	
	public void set(String name, ConfigurationValue value) {
		this.config.put(name, value);
	}
	
	public long getLong(String name) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(!this.config.containsKey(name)) 
			throw new DeviceConfigurationException();
		//return this.config.get(name).getLongValue();
		if(this.config.get(name).getType()==ConfigurationValueType.LONG)
			return this.config.get(name).getLongValue();
		
		String s=this.config.get(name).getStringValue();
		if(s.isEmpty())return 0;
		
		boolean hex=false;
		s=s.toLowerCase();
		
		// Check for HEX
		if(s.startsWith("0x") || s.startsWith("0h") || s.startsWith("$0")) {hex=true; s=s.substring(2);}
		if(s.startsWith("h")) {hex=true; s=s.substring(1);}
		if(s.endsWith("h")) {hex=true;s=s.substring(0,s.length()-1);}
		if(hex)return Long.parseLong(s,16);
		
		// Check for OCTAL
		boolean octal=false;
		if(s.startsWith("0q") || s.startsWith("0o")) {octal=true; s=s.substring(2);}
		if(s.startsWith("q") || s.startsWith("o")) {octal=true; s=s.substring(1);}
		if(s.endsWith("q") || s.endsWith("o")) {octal=true;s=s.substring(0,s.length()-1);}
		if(octal)return Long.parseLong(s,8);

		// Check for BINARY
		boolean binary=false;
		if(s.startsWith("0b") || s.startsWith("0y")) {binary=true; s=s.substring(2);}
		if(s.endsWith("b") || s.endsWith("y")) {binary=true;s=s.substring(0,s.length()-1);}
		if(binary)return Long.parseLong(s,2);
		
		// one more try for hex
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)>='a' && s.charAt(i)<='f') {hex=true; break;}
		}
		if(hex)return Long.parseLong(s,16);
		
		return Long.parseLong(s);
	}
	
	public String getString(String name) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(!this.config.containsKey(name)) 
			throw new DeviceConfigurationException();
		return this.config.get(name).getStringValue();
	}
	
	public long getOptLong(String name, long def) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(
				!contains(name) || 
				(this.config.get(name).getType()==ConfigurationValueType.STRING && this.config.get(name).getStringValue().isEmpty())
		)return def;
		return getLong(name);
	}
	
	public String getOptString(String name, String def) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(!contains(name))return def;
		return getString(name);
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
