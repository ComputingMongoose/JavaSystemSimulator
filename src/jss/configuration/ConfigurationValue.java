package jss.configuration;

public class ConfigurationValue {

	private long lValue;
	private String sValue;
	private ConfigurationValueType type;
	
	public ConfigurationValue(long lValue) {
		this.lValue=lValue;
		this.type=ConfigurationValueType.LONG;
	}
	
	public ConfigurationValue(String sValue) {
		this.sValue=sValue;
		this.type=ConfigurationValueType.STRING;
	}
	
	public long getLongValue() throws ConfigurationValueTypeException {
		if(this.type!=ConfigurationValueType.LONG)
			throw new ConfigurationValueTypeException();
		return this.lValue;
	}
	
	public String getStringValue() throws ConfigurationValueTypeException {
		if(this.type!=ConfigurationValueType.STRING)
			throw new ConfigurationValueTypeException();
		return this.sValue;
	}
	
	public ConfigurationValueType getType() {
		return this.type;
	}
	
}
