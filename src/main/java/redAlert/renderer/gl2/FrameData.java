package redAlert.renderer.gl2;

import java.util.ArrayList;
import java.util.BitSet;

import redAlert.enums.UnitColor;

class ShpDrawState implements Cloneable {
	ShpSequence shpSequence;
	UnitColor campColor; // TODO 上阵营色
	long startMoment;
	int state, posX, posY, height, vlayer; //动画编号，地面X坐标，地面Y坐标，单位高度，图层号
	
	@Override
	public ShpDrawState clone() {
		try {
			return (ShpDrawState) (super.clone());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Impossible CloneNotSupportedException in ShpDrawState");
		}
	}
}

class FrameData {
	FrameData prevFrameData = null;
	int viewportOffsetX, viewportOffsetY;
	
	boolean cursorVisible = false;
	int cursorPosX = 0, cursorPosY = 0;
		
	int rhombusXNum, rhombusYNum;
	
	BitSet shpUpdateMap = new BitSet();
	BitSet shpEnableMap = new BitSet();
	// TODO 引入fastutil库，优化为Struct of Arrays
	ArrayList<ShpDrawState> shpDrawStates = new ArrayList<>();
	
	FrameData(){
		shpDrawStates.add(new ShpDrawState()); // 0号DrawState放弃不用
	}
	
	final void copyFrom(FrameData prev) {
		prevFrameData = prev;
		viewportOffsetX = prev.viewportOffsetX;
		viewportOffsetY = prev.viewportOffsetY;
		cursorVisible = prev.cursorVisible;
		cursorPosX = prev.cursorPosX;
		cursorPosY = prev.cursorPosY;
		rhombusXNum = prev.rhombusYNum;
		
		shpUpdateMap.clear();
		shpEnableMap = (BitSet) (prev.shpEnableMap.clone());
		shpDrawStates = (ArrayList<ShpDrawState>) (prev.shpDrawStates.clone());
	}
}
