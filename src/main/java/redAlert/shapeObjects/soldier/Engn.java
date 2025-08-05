package redAlert.shapeObjects.soldier;

import java.util.LinkedList;
import java.util.Random;

import redAlert.enums.UnitColor;
import redAlert.shapeObjects.Soldier;
import redAlert.utilBean.LittleCenterPoint;

public class Engn extends Soldier{
	
	public Engn(LittleCenterPoint lcp,UnitColor color) {
		
		super(lcp,"engineer",color, 36, 39);
		
		//定义唯一编号
		super.unitName = "攻城狮";
		
		//移动时说的话
		super.moveSounds = new String[] {"ienamoa","ienamob","ienamoc"};
		//选中时说的话
		super.selectSounds = new String []{"ienasea","ienaseb","ienasec","ienased"};
	}

	/**
	 * 当一帧绘完  building会被扔入BuildingDrawer的队列中调用此方法算下一帧画面
	 * 计算下一帧画面
	 */
	public void calculateNextFrame() {
		
		super.calculateNextFrame();
		
	}

	@Override
	public void moveToTarget(LittleCenterPoint moveTarget, LinkedList<LittleCenterPoint> path) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
