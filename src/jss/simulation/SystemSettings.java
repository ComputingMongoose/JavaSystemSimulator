package jss.simulation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class SystemSettings {

	private static SystemSettings obj=null;
	private static String version;
	
	private SystemSettings() {
		try (BufferedReader in=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/res/_system.json")));){
			StringBuffer jsonBuff=new StringBuffer();
			String line;
			while((line=in.readLine())!=null) {jsonBuff.append(line);jsonBuff.append("\n");}
			in.close();
			JSONObject json = new JSONObject(jsonBuff.toString());
			version=json.optString("version","UNKNOWN");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static SystemSettings getSystemSettings() {
		if(obj==null)obj=new SystemSettings();
		return obj;
	}
	
	public String getVersion() {
		return version;
	}
}
