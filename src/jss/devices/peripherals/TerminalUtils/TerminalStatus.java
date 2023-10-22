package jss.devices.peripherals.TerminalUtils;

import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;

import jss.configuration.DeviceConfiguration;
import jss.devices.peripherals.PeripheralSwitch;
import jss.simulation.Simulation;

public class TerminalStatus {

	protected TerminalTextRenderer renderer;
	protected Window win;
	protected HashMap<String,PeripheralSwitch> switches;
	protected ArrayList<Character> transmit;
	
	protected boolean kbLock;
	
	protected TextWithAttributes[][] text;
	
	int cur_x;
	int cur_y;
	int rows,columns;
	int currentColor;
	int currentBgColor;
	
	int baseWidth;
	int baseHeight;
	
	boolean insertCharacterMode;
	int cursorCharacter;
	
	boolean autoLF; // auto LF on CR
	boolean autoCR; // auto CR on LF
	int characterSet;
	
	int eolBehavior; // 0 = auto next line, 1 = wrap around, 2 = discard
	
	public static final int EOL_NEXT=0;
	public static final int EOL_WRAP=1;
	public static final int EOL_DISCARD=2;
	
	protected DeviceConfiguration config;
	protected Simulation sim;
	
	boolean cur_ctl; // true if cursor control is enabled
	boolean enableKbLock; // true if keyboard locking is enabled
	
	boolean needsUpdate;
	
	public TerminalStatus(DeviceConfiguration config, Simulation sim) {
		this.renderer=null;
		this.win=null;
		this.switches=null;
		this.kbLock=false;
		this.transmit=new ArrayList<>(100);
		text=null;
		setCur_x(0);
		setCur_y(0);
		this.config=config;
		this.sim=sim;
		this.cur_ctl=true;
		this.enableKbLock=false;
		needsUpdate=false;
		insertCharacterMode=false;
		cursorCharacter=219;
		autoLF=false;
		autoCR=false;
		characterSet=0;
		eolBehavior=EOL_NEXT;
	}
	
	public void transmitClear() {
		synchronized(transmit) {
			transmit.clear();
		}
	}
	
	public boolean isTransmitDataAvailable() {
		boolean ret=false;
		synchronized(transmit) {
			ret=!transmit.isEmpty();
		}
		return ret;
	}
	
	public void transmitChar(char c) {
		synchronized(transmit) {
			transmit.add(c);
		}
	}
	
	public void transmitChar(int c) {
		transmitChar((char)c);
	}

	public synchronized void transmitChars(char[] sequence) {
		synchronized(transmit) {
			for(char c:sequence)transmit.add(c);
		}
	}
	
	public void transmitChars(byte[] sequence) {
		synchronized(transmit) {
			for(byte c:sequence)transmit.add((char)c);
		}
	}

	public void transmitChars(byte[] sequence, int num) {
		synchronized(transmit) {
			for(int i=0;i<num;i++)transmit.add((char)sequence[i]);
		}
	}

	public int getNextTransmitChar() {
		int ret=0;
		synchronized(transmit) {
			if(transmit.size()>0) {
				ret=(int)transmit.get(0);
				transmit.remove(0);
			}
		}
		return ret;
	}
	
	
	public synchronized void advanceCursorY() {
		cur_y++;
		if(cur_y>=rows) {
			cur_y--;
			for(int i=0;i<rows-1;i++) {
				for(int j=0;j<columns;j++) {
					text[i][j].copyFrom(text[i+1][j]);
				}
			}
			for(int i=0;i<columns;i++) {
				text[rows-1][i].clear();
			}
		}
		needsUpdate=true;
	}
	
	public void advanceCursorX() {
		advanceCursorX(true);
	}
	
	public synchronized void advanceCursorX(boolean advY) {
		cur_x++;
		if(cur_x>=columns) {
			if(advY) {
				// check EOL behavior
				switch(eolBehavior) {
				case EOL_NEXT:
					cur_x=0;
					advanceCursorY();
					break;
				case EOL_WRAP:
					cur_x=0;
					break;
				case EOL_DISCARD:
					cur_x=columns-1;
					break;
				}
			}else cur_x--;
		}
		needsUpdate=true;
	}	

	public synchronized TerminalTextRenderer getRenderer() {
		return renderer;
	}

	public synchronized Window getWin() {
		return win;
	}

	public synchronized HashMap<String, PeripheralSwitch> getSwitches() {
		return switches;
	}

	public synchronized boolean isKbLock() {
		return kbLock;
	}

	public synchronized void setKbLock(boolean kbLock) {
		this.kbLock = kbLock;
	}

	public synchronized void setWin(Window win) {
		this.win = win;
	}

	public synchronized void setRenderer(TerminalTextRenderer renderer) {
		renderer.setStatus(this);
		this.renderer = renderer;
		rows=renderer.getRows();
		columns=renderer.getColumns();
		text=new TextWithAttributes[rows][columns];
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				text[i][j]=new TextWithAttributes(this);
			}
		}
		currentColor=renderer.font.color;
		currentBgColor=renderer.font.bgcolor;
		controlClearScreen();
		cur_x=0;
		cur_y=0;
	}

	public synchronized void setSwitches(HashMap<String, PeripheralSwitch> switches) {
		this.switches = switches;
	}

	public synchronized TextWithAttributes [][] getText() {
		return text;
	}

	public synchronized int getCur_y() {
		return cur_y;
	}

	public synchronized void setCur_y(int cur_y) {
		this.cur_y = cur_y;
		needsUpdate=true;
	}

	public synchronized int getCur_x() {
		return cur_x;
	}

	public synchronized void setCur_x(int cur_x) {
		this.cur_x = cur_x;
		needsUpdate=true;
	}
	
	public synchronized void setCurrentChar(int data) {
		if(insertCharacterMode) {
			for(int i=columns-1;i>cur_x;i--)text[cur_y][i]=text[cur_y][i-1];
		}
		text[cur_y][cur_x].setText((char)data);
		needsUpdate=true;
	}

	public synchronized boolean isCur_ctl() {
		return cur_ctl;
	}

	public synchronized void setCur_ctl(boolean cur_ctl) {
		this.cur_ctl = cur_ctl;
	}

	public synchronized boolean isNeedsUpdate() {
		return needsUpdate;
	}

	public synchronized void setNeedsUpdate(boolean needsUpdate) {
		this.needsUpdate = needsUpdate;
	}
	
	public synchronized boolean getAndResetNeedsUpdate() {
		boolean ret=needsUpdate;
		if(ret) {needsUpdate=false;}
		return ret;
	}
	
	public void controlBackSpace() {
		controlBackSpace(true);
	}
	
	public synchronized void controlBackSpace(boolean advY) {
		if(cur_x>0)cur_x--;
		else if(cur_ctl && cur_y>0 && advY) {cur_x=columns-1;cur_y--;}
		needsUpdate=true;
	}

	public synchronized void controlVerticalBackSpace() {
		if(cur_ctl && cur_y>0) {
			cur_y--;
			needsUpdate=true;
		}
	}
	
	public synchronized void controlClearScreen() {
		for(int i=0;i<rows;i++) {
			for(int j=0;j<columns;j++) {
				text[i][j].clear();
			}
		}
	}
	
	public synchronized void controlHome() {
		cur_x=0;
		if(cur_ctl) cur_y=0;
		needsUpdate=true;
	}

	public synchronized boolean isEnableKbLock() {
		return enableKbLock;
	}

	public synchronized void setEnableKbLock(boolean enableKbLock) {
		this.enableKbLock = enableKbLock;
	}

	public synchronized int getRows() {
		return rows;
	}

	public synchronized void setRows(int rows) {
		this.rows = rows;
	}
	
	public synchronized void eraseFromBegin() {
		for(int i=cur_x;i>0;i--)text[cur_y][i].clear();
		for(int i=cur_y-1;i>0;i--) {
			for(int j=0;j<columns;j++)text[i][j].clear();
		}
	}
	
	public synchronized void eraseToEnd() {
		for(int i=cur_x;i<columns;i++)text[cur_y][i].clear();
		for(int i=cur_y+1;i<rows;i++) {
			for(int j=0;j<columns;j++)text[i][j].clear();
		}
	}

	public synchronized void eraseLine() {
		for(int j=0;j<columns;j++)text[cur_y][j].clear();
	}

	public synchronized void eraseLineFromBegin() {
		for(int j=0;j<=cur_x;j++)text[cur_y][j].clear();
	}
	
	public synchronized void eraseLineToEnd() {
		for(int j=cur_x;j<columns;j++)text[cur_y][j].clear();
	}
	
	public synchronized void controlInsertLine() {
		for(int i=rows-1; i>cur_y;i--) {
			for(int j=0;j<columns;j++)text[i][j].copyFrom(text[i-1][j]);
		}
		eraseLine();
		cur_x=0;
	}
	
	public synchronized void controlInsertCharacter() {
		for(int i=columns-1;i>cur_x;i--) text[cur_y][i].copyFrom(text[cur_y][i-1]);
		text[cur_y][cur_x].clear();
	}

	public synchronized void controlDeleteLine() {
		for(int i=cur_y; i<rows-1;i--) {
			for(int j=0;j<columns;j++)text[i][j].copyFrom(text[i+1][j]);
		}
		for(int j=0;j<columns;j++)text[rows-1][j].clear();
		cur_x=0;
	}

	public synchronized void controlDeleteCharacter() {
		for(int j=cur_x;j<columns-1;j++)text[cur_y][j].copyFrom(text[cur_y][j+1]);
		text[cur_y][columns-1].clear();
	}

	public synchronized boolean isInsertCharacterMode() {
		return insertCharacterMode;
	}

	public synchronized void setInsertCharacterMode(boolean insertCharacterMode) {
		this.insertCharacterMode = insertCharacterMode;
	}

	public synchronized int getCursorCharacter() {
		return cursorCharacter;
	}

	public synchronized void setCursorCharacter(int cursorCharacter) {
		this.cursorCharacter = cursorCharacter;
	}

	public synchronized int getColumns() {
		return columns;
	}

	public synchronized void setColumns(int columns) {
		this.columns = columns;
	}

	public synchronized boolean isAutoLF() {
		return autoLF;
	}

	public synchronized void setAutoLF(boolean autoLF) {
		this.autoLF = autoLF;
	}

	public synchronized boolean isAutoCR() {
		return autoCR;
	}

	public synchronized void setAutoCR(boolean autoCR) {
		this.autoCR = autoCR;
	}

	public synchronized int getCharacterSet() {
		return characterSet;
	}

	public synchronized void setCharacterSet(int characterSet) {
		this.characterSet = characterSet;
	}

	public synchronized int getEolBehavior() {
		return eolBehavior;
	}

	public synchronized void setEolBehavior(int eolBehaviour) {
		this.eolBehavior = eolBehaviour;
	}
	
	public synchronized void controlTab() {
		System.out.println(cur_x);
		if(cur_x>=columns-8)this.advanceCursorX();
		else cur_x=((cur_x/8)+1)*8;
		needsUpdate=true;
	}

	public synchronized int getCurrentColor() {
		return currentColor;
	}

	public synchronized void setCurrentColor(int currentColor) {
		this.currentColor = currentColor;
	}

	public synchronized int getCurrentBgColor() {
		return currentBgColor;
	}

	public synchronized void setCurrentBgColor(int currentBgColor) {
		this.currentBgColor = currentBgColor;
	}
	
	public synchronized void controlReverseVideo() {
		int tmp=this.currentBgColor;
		this.currentBgColor=this.currentColor;
		this.currentColor=tmp;
	}

	public synchronized int getBaseWidth() {
		return baseWidth;
	}

	public synchronized void setBaseWidth(int baseWidth) {
		this.baseWidth = baseWidth;
	}

	public synchronized int getBaseHeight() {
		return baseHeight;
	}

	public synchronized void setBaseHeight(int baseHeight) {
		this.baseHeight = baseHeight;
	}

}
