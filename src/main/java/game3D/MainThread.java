package game3D;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainThread {
	public static int screen_w = 1024;
	public static int screen_h = 682;
	public static int half_screen_w = screen_w/2;
	public static int half_screen_h = screen_h/2;
	
	public static JPanel panel;
	
	public static int [] screen;
	
	public static BufferedImage screenBuffer;
	
	public static void main(String[] args) throws Exception{
		JFrame jf = new JFrame("3D学习");
		jf.setSize(screen_w,screen_h);
		jf.setResizable(false);
		jf.setAlwaysOnTop(false);//总是在最上层
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLocationRelativeTo(null);//屏幕居中
		jf.setVisible(true);
		
		JPanel mp = new JPanel();
		mp.setLocation(0, 0);
		mp.setVisible(true);
		mp.setLayout(null);
		mp.setMinimumSize(new Dimension(screen_w,screen_h));
		mp.setPreferredSize(new Dimension(screen_w,screen_h));
		jf.add(mp);
		jf.pack();
		
		LookupTables.init();
		
		
		screenBuffer = new BufferedImage(screen_w,screen_h,BufferedImage.TYPE_INT_RGB);
		DataBuffer dest = screenBuffer.getRaster().getDataBuffer();
		screen = ((DataBufferInt) dest).getData();
		
		mp.getGraphics().drawImage(screenBuffer, 0,	0, jf);
		
	}
}
