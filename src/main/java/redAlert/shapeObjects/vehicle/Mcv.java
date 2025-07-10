package redAlert.shapeObjects.vehicle;

import java.util.ArrayList;
import java.util.List;

import redAlert.Constructor;
import redAlert.GameContext;
import redAlert.GlobalConfig;
import redAlert.enums.UnitColor;
import redAlert.militaryBuildings.AfCnst;
import redAlert.resourceCenter.ShapeUnitResourceCenter;
import redAlert.shapeObjects.Expandable;
import redAlert.shapeObjects.Vehicle;
import redAlert.utilBean.CenterPoint;
import redAlert.utilBean.XunLuBean;
import redAlert.utils.PointUtil;

/**
 * 盟军基地车
 */
public class Mcv extends Vehicle implements Expandable{

	//------------基地车独有的变量
	public final static int MCV_STATUS_NORMAL = 0;
	public final static int MCV_STATUS_EXPANDING = 1;
	
	public int status = MCV_STATUS_NORMAL;//正常状态
	//---------------
	/**
	 * 
	 */
	public Mcv(int positionX,int positionY,UnitColor unitColor) {
		super.initVehicleParam(positionX,positionY, unitColor, "mcv");
		//定义名称
		super.unitName = "盟军基地车";
		//降低基地车的移动速度
		this.frameSpeed = 4;
	}
	
	/**
	 * 这个代码真的是写的一坨
	 * 一定要优化  让逻辑更清晰，步骤更合理
	 */
	@Override
	public void calculateNextFrame() {
		
		if(status==MCV_STATUS_NORMAL) {
			//先给出几种否决条件
			if(nextTarget==null) {//无下一个位置  不移动
				return;
			}
			
			if(endTarget==null) {//无终点 不移动
				return;
			}
			
			if(movePath==null || movePath.size()<1) {
				return;
			}
			
			//判定是否已经到达下一个地点
			if(isArrivedNextTarget()) {
				if(isArrivedEndTarget()) {//判断是否已经到达终点
					movePath = null;
					nextTarget=null;
					endTarget=null;
				}else {
					
					//是否有临时停止标志  有的话  需要暂停计算  等待寻路完成  帧停顿
					if(stopFlag) {
						return;
					}
					
					
					//若发生重定位,说明movePath变量发生了变化,获取nextTarget的方式要改变
					if(resetTarget) {
						nextTarget = movePath.get(0);
						resetTarget = false;
						calAndSetTargetTurn(this, nextTarget);
						
						moveOneTime();
						
						
					}else {//根据当前位置  确认下一个位置
						int curIndex = movePath.indexOf(nextTarget);
						nextTarget = movePath.get(curIndex+1);
						
						//确认这个位置是否可达
						if(nextTarget.isVehicleCanOn()) {
							calAndSetTargetTurn(this, nextTarget);
							moveOneTime();
						}else {
							//实现重新规划线路
							xunluLock.lock();
							try {
								XunLuBean xlb = new XunLuBean();
								List<CenterPoint> path = xlb.xunlu(curCenterPoint, endTarget);
								if(path!=null) {
									this.movePath = path;
									nextTarget = movePath.get(0);
									calAndSetTargetTurn(this, nextTarget);
									moveOneTime();
								}else {
									nextTarget = null;
									endTarget = null;
									movePath = null;
								}
							}catch (Exception e) {
								System.out.println("程序自动寻路异常");
								e.printStackTrace();
							}finally {
								xunluLock.unlock();
							}
						}
						
					}
				}
			}else {
				moveOneTime();
			}
			super.curFrame = bodyFrames.get(curTurn);
			this.positionMinX = positionX+curFrame.getMinX();
			this.positionMinY = positionY+curFrame.getMinY();
			
		
		
		
		
		
		
		
		}else {
			
			/**
			 * 基地车的展开逻辑
			 */
			setTargetTurn(6);
			
			/**
			 * 车体方向需旋转到位才能移动
			 */
			if(targetTurn!=curTurn) {
				turn();
				super.curFrame = bodyFrames.get(curTurn);
				return;
			}else {
				CenterPoint cp = PointUtil.getCenterPoint(positionX+centerOffX,positionY+centerOffY);
				cp.removeUnit(this);
				ShapeUnitResourceCenter.removeOneMovableUnit(this);
				ShapeUnitResourceCenter.removeOneUnit(this.getBloodBar());
				this.getBloodBar().setVisible(false);
				this.getBloodBar().setEnd(true);
				this.setVisible(false);
				this.setEnd(true);
				AfCnst afCnst = new AfCnst(cp,GlobalConfig.sceneType, GlobalConfig.unitColor);
				Constructor.putOneBuilding(afCnst,GameContext.getMainPanel());//盟军基地
				try {
					Thread.sleep(50);
					Constructor.playOneMusic("ceva049");//New construction options
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
		}
		
		
		
		
		
	}
	
	/**
	 * 展开
	 */
	@Override
	public void expand() {
		//先转到姿势6  然后移走基地车  然后放一个基地
		
		
		this.status = Mcv.MCV_STATUS_EXPANDING;
		
	}
	
	/**
	 * 判定是否满足展开的条件
	 */
	@Override
	public boolean isExpandable() {
		
		List<CenterPoint> result = new ArrayList<>();
		result.add(curCenterPoint.getLeft());
		result.add(curCenterPoint.getLeftDn());
		result.add(curCenterPoint.getDn());
		result.add(curCenterPoint.getRightDn());
		result.add(curCenterPoint.getRight());
		result.add(curCenterPoint.getRightUp());
		result.add(curCenterPoint.getUp());
		result.add(curCenterPoint.getLeftUp());
		
		result.add(curCenterPoint.getLeft().getLeftDn());
		result.add(curCenterPoint.getLeftDn().getLeftDn());
		result.add(curCenterPoint.getLeftDn().getDn());
		result.add(curCenterPoint.getRight().getRightDn());
		result.add(curCenterPoint.getRightDn().getRightDn());
		result.add(curCenterPoint.getDn().getRightDn());
		result.add(curCenterPoint.getDn().getDn());
		
		
		boolean isCanMake = true;
		for(CenterPoint cp:result) {
			if(!cp.isBuildingCanPutOn()) {
				isCanMake = false;
			}
		}
		return isCanMake;
	}
	
	
	/**
	 * 再缩回
	 */
	@Override
	public void unexpand() {
		
		
	}

}
