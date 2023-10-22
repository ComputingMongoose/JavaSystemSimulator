package jss.devices.peripherals.TerminalUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class TerminalFont {

	protected byte[] fontData;
	
	protected int[][] charImages;
	
	protected int fontWidth;
	protected int fontHeight;
	
	protected int color;
	protected int bgcolor;
	
	protected boolean[] charImageEmpty;
	
	public TerminalFont(String resourcePath, int numChars, int fontWidth, int fontHeight, int color, int backgroundColor) throws IOException {
		InputStream in=new BufferedInputStream(getClass().getResource(resourcePath).openStream());
		fontData=IOUtils.toByteArray(in);
		in.close();
		
		this.fontHeight=fontHeight;
		this.fontWidth=fontWidth;
		this.bgcolor=-1;
		this.color=-1;
		
		charImages=new int[numChars][];
		charImageEmpty=new boolean[numChars];
		for(int i=0;i<charImages.length;i++) {
			charImages[i]=getCharImage(i,color,backgroundColor);
		}

		this.bgcolor=backgroundColor;
		this.color=color;
		
	}
	
	public int[] getCharImage(int chr, int color, int bgcolor) {
		if(chr>=this.charImages.length)chr=0;

		if(color==this.color && bgcolor==this.bgcolor)return this.charImages[chr];
		
		int a=(color>>24)&255;
		int r=(color>>16)&255;
		int g=(color>>8)&255;
		int b=color&255;

		int ba=(bgcolor>>24)&255;
		int br=(bgcolor>>16)&255;
		int bg=(bgcolor>>8)&255;
		int bb=bgcolor&255;
		
		int []ret=new int[4*8*16];
		boolean empty=true;
		for(int row=0;row<16;row++) {
			int address=(chr<<4)|row;
			int bdata=fontData[address];
			for(int bit=0;bit<8;bit++) {
				if((bdata&(1<<bit))!=0) {
					ret[row*8*4+(7-bit)*4]=r;
					ret[row*8*4+(7-bit)*4+1]=g;
					ret[row*8*4+(7-bit)*4+2]=b;
					ret[row*8*4+(7-bit)*4+3]=a;
					empty=false;
				}else {
					ret[row*8*4+(7-bit)*4]=br;
					ret[row*8*4+(7-bit)*4+1]=bg;
					ret[row*8*4+(7-bit)*4+2]=bb;
					ret[row*8*4+(7-bit)*4+3]=ba;
				}
			}
		}
		charImageEmpty[chr]=empty;
		return ret;
	}

	public int getFontWidth() {
		return fontWidth;
	}

	public int getFontHeight() {
		return fontHeight;
	}
	
	public int[] getCharImage(int chr) {
		if(chr>=this.charImages.length)chr=0;
		return this.charImages[chr];
	}
	
	public boolean isCharImageEmpty(int chr) {
		if(chr>=this.charImages.length)return true;
		return this.charImageEmpty[chr];
	}
}

