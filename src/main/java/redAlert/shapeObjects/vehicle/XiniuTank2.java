package redAlert.shapeObjects.vehicle;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import redAlert.enums.UnitColor;
import redAlert.shapeObjects.Vehicle;
import redAlert.utils.CanvasPainter;

/**
 * 犀牛坦克
 * 
 * 图片资源文件是128*128像素  中心点为64,64
 */
public class XiniuTank2 extends Vehicle{
	
	public XiniuTank2() {
		
	}
	
	/**
	 * 坦克的curFrame对象不直接用帧图
	 * 而是利用车身、炮塔结合绘制出来的图片,因为车身和炮塔应该有一样的绘制优先级
	 * 
	 */
	public XiniuTank2(int positionX,int positionY,UnitColor color) {
		super.initVehicleParam(positionX,positionY, unitColor, "htnk");
		//定义名称
		super.unitName = "犀牛坦克";
		//移动时说的话
		super.moveSounds = new String[]{"vgrsmoa","vgrsmob","vgrsmoc"};
		//选中时说的话
		super.selectSounds = new String []{"vgrssea","vgrsseb","vgrssec"};
		
		//犀牛坦克炮塔
		turret = new XiniuTankTurret(this);
		
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
