package redAlert.shapeObjects.vehicle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import redAlert.enums.UnitColor;
import redAlert.shapeObjects.Vehicle;
import redAlert.utils.CanvasPainter;

/**
 * 多功能步兵车
 */
public class Ifv extends Vehicle{

	public Ifv() {
		
	}
	
	/**
	 * 坦克的curFrame对象不直接用帧图
	 * 而是利用车身、炮塔结合绘制出来的图片,因为车身和炮塔应该有一样的绘制优先级
	 * 
	 */
	public Ifv(int positionX,int positionY,UnitColor unitColor) {
		super.initVehicleParam(positionX,positionY, unitColor, "fv");
		//定义名称
		super.unitName = "多功能步兵车";
		//移动时说的话
		super.moveSounds = new String[] {"vifvmoa","vifvmob","vifvmoc","vifvmod","vifvmoe","vifvmof"};
		//选中时说的话
		super.selectSounds = new String []{"vifvsea","vifvseb","vifvsec","vifvsed","vifvsee","vifvsef"};
		
		
		//灰熊坦克炮塔
		turret = new IfvTurret(this);
		
		//把车身和炮塔画一块
		BufferedImage image = curFrame.getImg();
		Graphics2D g2d = image.createGraphics();
		CanvasPainter.clearImage(image);
		g2d.drawImage(bodyFrames.get(curTurn).getImg(), 0, 0, null);
		g2d.drawImage(turret.getFrames().get(turret.getCurTurn()).getImg(),0,0,null);
	}

	@Override
	public void calculateNextFrame() {
		super.calculateNextFrame();
	}
}
