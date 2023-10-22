package jss.devices.peripherals.TerminalUtils;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.Timer;

import jss.devices.peripherals.PeripheralSwitch;

@SuppressWarnings("serial")
public class TerminalFrontWindow extends JFrame{
	protected TerminalStatus status;
	protected AbstractTerminal terminal;
	
	public TerminalFrontWindow(TerminalStatus status, AbstractTerminal terminal) {
		super(terminal.getTitle());

		this.status=status;
		this.terminal=terminal;
		
		this.setLayout(null);
		
		status.setWin(this);
		status.setBaseWidth(terminal.getBaseWidth());
		status.setBaseHeight(terminal.getBaseHeight());
		
		setSize(terminal.getStartWidth(),terminal.getStartHeight());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		this.setCursor(cur);
		this.addMouseListener(terminal.getMouseListener());
		this.addKeyListener(terminal.getKeyListener());
		
		setVisible(true);
		
		Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(status.getAndResetNeedsUpdate())repaint();
            }
        });	
		timer.start();
	}
	
	@Override
	public void paint(Graphics g) {
		//super.paint(g);

		BufferedImage textImage=status.getRenderer().renderText(status.getText());
		
		BufferedImage back=null;
		if(terminal.getTerminalImage()!=null) {
			ColorModel cm = terminal.getTerminalImage().getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = terminal.getTerminalImage().copyData(null);
			back=new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}else {
			int w=textImage.getWidth()+60;
			int h=textImage.getHeight()+160;			
			back=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB); 			
		}
		
		Graphics2D g2=(Graphics2D)back.getGraphics();

		Rectangle r=terminal.getTextRect();
		Image drawTextImage=textImage;
		if(textImage.getWidth()!=r.width || textImage.getHeight()!=r.height)
			drawTextImage=textImage.getScaledInstance(r.width, r.height,Image.SCALE_SMOOTH);
		
		g2.drawImage(drawTextImage, r.x, r.y,null);
		
		for(Entry <String,PeripheralSwitch> e:terminal.getSwitches().entrySet()) {
			PeripheralSwitch s=e.getValue();
			if(s.swOn!=null)g2.drawImage(s.swOn, s.x, s.y, s.w, s.h,null);
		}
		
		
		Insets in=this.getInsets();
		Image img=back.getScaledInstance(
				this.getWidth()-in.left-in.right, 
				this.getHeight()-in.top-in.bottom, 
				Image.SCALE_SMOOTH);
		g.drawImage(img,in.left,in.top,null);
	}
}
