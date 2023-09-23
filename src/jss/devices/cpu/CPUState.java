package jss.devices.cpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CPUState {

	private HashMap<String,CPURegister> registers;
	
	public CPUState() {
		registers=new HashMap<>(100);
	}
	
	public CPUState clone() {
		CPUState c=new CPUState();
		for(Map.Entry<String, CPURegister> entry:registers.entrySet()) {
			c.registers.put(entry.getKey(), entry.getValue().clone());
		}
		return c;
	}
	
	public void setRegister(String name, int szBits, long value) {
		if(!registers.containsKey(name)) {
			registers.put(name, new CPURegister(name,szBits,value));
		}else {
			registers.get(name).setValue(value);
		}
	}
	
	public CPURegister getRegister(String name) {
		if(!registers.containsKey(name))return null;
		return registers.get(name);
	}
	
	public Set<String> getRegisterNames(){
		return registers.keySet();
	}
	
	public String getStateString() {
		StringBuffer buff=new StringBuffer();
		
		List<String> sortedList = new ArrayList<>(getRegisterNames());
		Collections.sort(sortedList);
		
		int n=0;
		for(String name:sortedList) {
			n++;
			buff.append(name);
			buff.append("=");
			buff.append(getRegister(name).getValueHex());
			if(n%16==0)buff.append("\n");
			else buff.append(" ");
		}
		
		return buff.toString();
	}
}
