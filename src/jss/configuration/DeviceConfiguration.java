package jss.configuration;

import java.util.HashMap;

public class DeviceConfiguration {

	private String name;
	private String type;
	
	private HashMap<String,ConfigurationValue> config;
	
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
		return this.config.get(name).getLongValue();
	}
	
	public String getString(String name) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(!this.config.containsKey(name)) 
			throw new DeviceConfigurationException();
		return this.config.get(name).getStringValue();
	}
	
	public long getOptLong(String name, long def) throws DeviceConfigurationException, ConfigurationValueTypeException {
		if(!contains(name))return def;
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
