package redAlert.ui;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import redAlert.ShapeUnitFrame;
import redAlert.resourceCenter.ShpResourceCenter;

/**
 * 卖
 */
public class SellLabel extends JLabel{

	private static final long serialVersionUID = 1L;
	
	public BufferedImage sell = null;
	public BufferedImage sell2 = null;
	
	
	public SellLabel() {
		List<ShapeUnitFrame> sellFrame = ShpResourceCenter.loadShpResource("sell", "sidebar", false);
		sell = sellFrame.get(0).getImg();
		sell2 = sellFrame.get(1).getImg();
		this.setBounds(20+64,8,sell.getWidth(), sell.getHeight());
		Icon sellIcon = new ImageIcon(sell);
		this.setOpaque(true);
		this.setIcon(sellIcon);
	}
}
