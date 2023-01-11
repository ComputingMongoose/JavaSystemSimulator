package jss.configuration;

@SuppressWarnings("serial")
public class ConfigurationValueOptionException extends Exception {

	private String key;
	private String value;
	
	public ConfigurationValueOptionException(String key, String value) {
		this.key=key;
		this.value=value;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getValue() {
		return this.value;
	}
	
}
