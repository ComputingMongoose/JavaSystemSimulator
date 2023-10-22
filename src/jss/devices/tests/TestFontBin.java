package jss.devices.tests;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;

import jss.devices.peripherals.TerminalUtils.TerminalFont;
import jss.devices.peripherals.TerminalUtils.TerminalStatus;
import jss.devices.peripherals.TerminalUtils.TerminalTextRenderer;
import jss.devices.peripherals.TerminalUtils.TextWithAttributes;

public class TestFontBin {

	static TerminalTextRenderer renderer;
	static TextWithAttributes [][]text;
	
	@SuppressWarnings("serial")
	static class FrontWindow extends JFrame{
		public FrontWindow() {
			super("TestFontBin");
			
			this.setLayout(null);
			
			setSize(1066,796);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			this.setCursor(cur);
			
			setVisible(true);
		}
		
		@Override
		public void paint(Graphics g) {
			//super.paint(g);
			
			BufferedImage back=renderer.renderText(text);
			Insets in=this.getInsets();
			Image img=back.getScaledInstance(
					this.getWidth()-in.left-in.right, 
					this.getHeight()-in.top-in.bottom, 
					Image.SCALE_SMOOTH);
			g.drawImage(img,in.left,in.top,null);
		}
	}
	
	static FrontWindow win;
	
	public static void main(String[] args) throws IOException {
		String fontbin="/res/HeathkitH19/2716_444-29_h19font.bin";
		
		int color=0xFFBF00; // amber
		//int color=Color.GREEN.getRGB();
		
		int bgcolor=Color.BLACK.getRGB();
		//int bgcolor=0x101010;
		
		text=new TextWithAttributes[24][80];
		TerminalFont font=new TerminalFont(fontbin,128,8,16,color,bgcolor);
		renderer=new TerminalTextRenderer(font,text[0].length,text.length);
		TerminalStatus status=new TerminalStatus(null,null);
		status.setRenderer(renderer);
		
		for(int i=0;i<text.length;i++) {
			for(int j=0;j<text[i].length;j++) {
				text[i][j]=new TextWithAttributes(status);
			}
		}
		
		for(int i=0;i<256;i++) {
			text[i/text[0].length][i%text[0].length].setText((char)i);
		}
		
		
		win=new FrontWindow();
		win.setVisible(true);
	}
	
}
