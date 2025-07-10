package redAlert.ui;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import redAlert.ShapeUnitFrame;
import redAlert.resourceCenter.ShpResourceCenter;

/**
 * 修理
 */
public class RepairLabel extends JLabel{

	private static final long serialVersionUID = 1L;
	
	public BufferedImage repair = null;
	public BufferedImage repair2 = null;
	
	public RepairLabel() {
		List<ShapeUnitFrame> repairFrame = ShpResourceCenter.loadShpResource("repair", "sidebar", false);
		repair = repairFrame.get(0).getImg();
		repair2 = repairFrame.get(1).getImg();
		
		this.setBounds(20,8,repair.getWidth(), repair.getHeight());
		Icon repairIcon = new ImageIcon(repair);
		this.setOpaque(true);
		this.setIcon(repairIcon);
	}
}
