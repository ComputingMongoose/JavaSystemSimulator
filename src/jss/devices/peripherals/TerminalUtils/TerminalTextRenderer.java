package jss.devices.peripherals.TerminalUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class TerminalTextRenderer {

	protected TerminalFont font;
	protected int columns;
	protected int rows;
	
	protected int width;
	protected int height;
	
	protected TerminalStatus status;
	
	Color bgColor;
	
	public TerminalTextRenderer(TerminalFont font, int columns, int rows) {
		this.font=font;
		this.columns=columns;
		this.rows=rows;
		
		this.width=this.columns*font.getFontWidth();
		this.height=this.rows*font.getFontHeight();

		status=null;
		this.bgColor=Color.BLACK;
	}
	
	public BufferedImage renderText(TextWithAttributes[][] text) {
		BufferedImage back=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2=(Graphics2D)back.getGraphics();
		g2.setColor(this.bgColor);
		g2.fillRect(0, 0, width, height);
		WritableRaster raster=back.getRaster();

		int fontwidth=font.getFontWidth();
		int fontheight=font.getFontHeight();
		
		int x=0, y=0;
		for(int r=0;r<rows && r<text.length;r++) {
			for(int c=0;c<columns && c<text[r].length;c++) {
				int[] charimg=null;
				if(status.cur_y==r && status.cur_x==c && status.getCursorCharacter()!=0) {
					int chr=status.getCursorCharacter();
					charimg=font.getCharImage(chr);
				}else {
					charimg=text[r][c].getCharImage();
				}
				
				if(charimg!=null) {
					raster.setPixels(x, y, fontwidth, fontheight, charimg);
				}
				x+=fontwidth;
			}
			y+=fontheight;
			x=0;
		}
		
		return back;
	}

	public TerminalFont getFont() {
		return font;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public TerminalStatus getStatus() {
		return status;
	}

	public void setStatus(TerminalStatus status) {
		this.status = status;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = new Color(
				(bgColor>>16)&255,
				(bgColor>>8)&255,
				(bgColor)&255,
				(bgColor>>24)&255
		);
	}
	
}
