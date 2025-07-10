package redAlert.shapeObjects.vehicle;

import redAlert.Constructor;
import redAlert.GameContext;
import redAlert.MainPanel;
import redAlert.shapeObjects.Building;
import redAlert.shapeObjects.TankTurret;
import redAlert.shapeObjects.animation.TankShell;
import redAlert.utilBean.CenterPoint;

/**
 * 灰熊坦克的炮塔
 */
public class GrizTankTurret extends TankTurret{

	public GrizTankTurret(GrizTank tank) {
		super(tank, "gtnktur");
	}

	
	boolean firstFire = true;
	long lastFireFrameIndex = 0;
	TankShell lastShell = null;//上一发炮弹
	
	public final int fireFrameInterval = 200;//开火帧间隔  控制开火的速度
	
	/**
	 * 灰熊坦克的开火方法
	 */
	@Override
	public void attack(Building targetBuilding) {
		
		super.attackBuilding = targetBuilding;
		
		//攻击目标坐标
		CenterPoint target = targetBuilding.getCurCenterPoint();
		//载具当前所在中心点
		CenterPoint curCenterPoint = super.vehicle.getCurCenterPoint();
		
		
		if(checkFireCondition(targetBuilding)) {
			lastShell = new TankShell(curCenterPoint.getX(),curCenterPoint.getY(),vehicle,targetBuilding);
			Constructor.putOneShapeUnit(lastShell, GameContext.scenePanel);//炮弹
			long curFrameIndex = MainPanel.frameCount;
			lastFireFrameIndex = curFrameIndex;
			
			Constructor.playOneMusic("bgraatta");
			
		}
		
	}
	
	/**
	 * 是否满足开火条件
	 * 
	 * 不存在上一发炮弹或者上一发炮弹的伤害已结算,则可以开火
	 */
	public boolean checkFireCondition(Building targetBuilding) {
		
		if(lastShell!=null) {
			if(!lastShell.isDamageSettled()) {//上一发炮弹的伤害未结算
				return false;
			}
		}
		
		
		if(targetBuilding.isEnd()) {//建筑物已经被摧毁
			return false;
		}
		
		if(!targetBuilding.isVisible()) {//建筑物不可见
			return false;
		}
		
		long curFrameIndex = MainPanel.frameCount;
		if(curFrameIndex-lastFireFrameIndex<fireFrameInterval) {//开火帧间隔太短
			return false;
		}
		
		
		
		return true;
	}
	
	
}
