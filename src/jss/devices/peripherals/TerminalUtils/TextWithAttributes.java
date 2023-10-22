package jss.devices.peripherals.TerminalUtils;

public class TextWithAttributes {

	protected int bgcolor;
	protected int color;
	protected char text;
	protected TerminalStatus status;
	
	public TextWithAttributes(TerminalStatus status) {
		this.status=status;
		this.clear();
	}
	
	public synchronized void copyFrom(TextWithAttributes t1) {
		this.bgcolor=t1.bgcolor;
		this.color=t1.color;
		this.text=t1.text;
	}
	
	public synchronized void clear() {
		text=' ';
		if(status!=null) {
			bgcolor=status.getCurrentBgColor();
			color=status.getCurrentColor();
		}
	}

	public synchronized int getBgcolor() {
		return bgcolor;
	}

	public synchronized void setBgcolor(int bgcolor) {
		this.bgcolor = bgcolor;
	}

	public synchronized int getColor() {
		return color;
	}

	public synchronized void setColor(int color) {
		this.color = color;
	}

	public synchronized TerminalStatus getStatus() {
		return status;
	}

	public synchronized void setStatus(TerminalStatus status) {
		this.status = status;
	}

	public synchronized char getText() {
		return text;
	}

	public synchronized void setTextOnly(char text) {
		this.text = text;
	}
	
	public synchronized void setText(char text) {
		this.text = text;
		this.bgcolor=status.getCurrentBgColor();
		this.color=status.getCurrentColor();
	}
	
	public synchronized int[] getCharImage() {
		TerminalFont font=this.status.getRenderer().getFont();
		if(font.isCharImageEmpty(text))return null;
		return font.getCharImage(text,color,bgcolor);
	}
	
}
