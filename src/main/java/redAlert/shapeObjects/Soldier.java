package redAlert.shapeObjects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import redAlert.Constructor;
import redAlert.RuntimeParameter;
import redAlert.ShapeUnitFrame;
import redAlert.enums.Direction;
import redAlert.enums.UnitColor;
import redAlert.other.SoldierBloodBar;
import redAlert.renderer.ShpSequenceInfo;
import redAlert.renderer.IShpRenderProxy;
import redAlert.renderer.IShpSequence;
import redAlert.resourceCenter.RenderResourceCenter;
import redAlert.resourceCenter.ShpResourceCenter;
import redAlert.shapeObjects.Building.SceneType;
import redAlert.utilBean.CenterPoint;
import redAlert.utilBean.LittleCenterPoint;
import redAlert.utilBean.SoldierXunLuAdapter;
import redAlert.utilBean.SoldierXunLuBean;
import redAlert.utils.CanvasPainter;
import redAlert.utils.PointUtil;

/**
 * 士兵的超类
 */
public abstract class Soldier extends MovableUnit{
	
	/**
	 * 人物状态
	 */
	public enum SoldierStatus{
		Standing("立正"),
		Moving("移动"),
		Ease1("稍息动作1"),
		Ease2("稍息动作2");
		
		private final String cnDesc;
		
		SoldierStatus(String cnDesc){
			this.cnDesc = cnDesc;
		}
	}
	
	/**
	 * 步兵动画状态编号
	 */
	protected enum InfantryAnimState {
		StandingUp(0, 0, 0, 1),
		StandingLeftUp(1, 1, 1, 1),
		StandingLeft(2, 2, 2, 1),
		StandingLeftDown(3, 3, 3, 1),
		StandingDown(4, 4, 4, 1),
		StandingRightDown(5, 5, 5, 1),
		StandingRight(6, 6, 6, 1),
		StandingRightUp(7, 7, 7, 1),

		MovingUp(8, 8, 13, 3),
		MovingLeftUp(9, 14, 19, 3),
		MovingLeft(10, 20, 25, 3),
		MovingLeftDown(11, 26, 31, 3),
		MovingDown(12, 32, 37, 3),
		MovingRightDown(13, 38, 43, 3),
		MovingRight(14, 44, 49, 3),
		MovingRightUp(15, 50, 55, 3),
		
		AtEase1(16, 56, 71, 5),
		AtEase2(17, 72, 86, 5);
		
		public final int stateId;
		public final int animStart, animEnd, slowRate;
		
		InfantryAnimState(int stateId, int animStart, int animEnd, int slowRate) {
			this.stateId = stateId;
			this.animStart = animStart;
			this.animEnd = animEnd;
			this.slowRate = slowRate;
		}
		
		public static final InfantryAnimState[] mapping = {
				StandingUp, StandingLeftUp, StandingLeft, StandingLeftDown, StandingDown, StandingRightDown, StandingRight, StandingRightUp,
				MovingUp, MovingLeftUp, MovingLeft, MovingLeftDown, MovingDown, MovingRightDown, MovingRight, MovingRightUp,
				AtEase1, AtEase2,
		};
	}
	
	/**
	 * 最大血量  步兵固定1000
	 */
	public final static int maxHp = 1000;
	/**
	 * 最大血块个数  士兵固定16  但表示16个明暗条纹 知乎上称这是8格血
	 */
	public final static int maxBloodNum = 16;
	/**
	 * 当前血量
	 */
	public int curHp = 1000;
	/**
	 * 目标朝向
	 */
	public Direction targetDirection;
	/**
	 * 当前朝向
	 */
	public Direction curDirection;
	/**
	 * 所有SHP帧的渲染资源
	 */
	protected final IShpSequence shpSequence;
	/**
	 * SHP渲染代理对象
	 */
	protected IShpRenderProxy renderProxy = null;
	
	/**
	 * 移动帧图下标
	 */
	public int umoveIndex = 0;
	/**
	 * 下一个目标位
	 */
	public LittleCenterPoint nextTarget;
	/**
	 * 移动终点
	 */
	public LittleCenterPoint endTarget;
	/**
	 * 当前所在小中心点
	 * 当前所在中心点必须是移动路径上的点
	 */
	public LittleCenterPoint curLittleCenterPoint;
	/**
	 * 移动路径  静止时为null
	 */
	public List<LittleCenterPoint> movePath;
	/**
	 * 重定位
	 */
	public boolean resetTarget = false;
	/**
	 * 寻路锁,避免AWT线程和规划线程同时进行寻路
	 */
	public ReentrantLock xunluLock = new ReentrantLock(true);
	/**
	 * 步兵状态默认是站立
	 */
	public SoldierStatus status = SoldierStatus.Standing;
	/**
	 * 稍息下标
	 */
	public int easeIndex = 0;
	/**
	 * 站立时间
	 */
	public int standingTime = 0;//保持站立需要一定时间后才能稍息
	
	/**
	 * 构造方法
	 * @param shpPrefix
	 * @param color
	 */
	public Soldier(LittleCenterPoint lcp,String shpPrefix,UnitColor color, int centerOffX, int centerOffY) {
		super.unitColor = color;
		List<ShapeUnitFrame> frames = ShpResourceCenter.loadShpResource(shpPrefix, SceneType.TEM.getPalPrefix());
        shpSequence = RenderResourceCenter.shpSequences.computeIfAbsent(this.getClass(), clazz -> {
			int[][] states = new int[InfantryAnimState.mapping.length][];
			for(int i=0;i<states.length;i++) {
				InfantryAnimState anim = InfantryAnimState.mapping[i];
				states[i] = IntStream.rangeClosed(anim.animStart, anim.animEnd) // 生成 start~end
						.flatMap(n -> IntStream.generate(() -> n).limit(anim.slowRate)) // 每个数字重复 slowRate 次
						.toArray();
			}
			ShpSequenceInfo seqInfo = new ShpSequenceInfo();
			seqInfo.frames = frames;
			seqInfo.centerOffX = centerOffX;
			seqInfo.centerOffY = centerOffY;
			seqInfo.groundSizeX = 1;
			seqInfo.groundSizeY = 1;
			seqInfo.states = states;
			return RenderResourceCenter.renderer.registerShpSequence(seqInfo);
        });
        renderProxy = RenderResourceCenter.renderer.registerShpRenderProxy();
        
        this.relativeMinX = frames.get(0).getMinX();
        this.relativeMinY = frames.get(0).getMinY();
        this.centerOffX = centerOffX;
        this.centerOffY = centerOffY;
        this.positionX = lcp.x - centerOffX;
        this.positionY =  lcp.y - centerOffY;
        this.positionMinX = positionX + relativeMinX;
        this.positionMinY = positionY + relativeMinY;

		curDirection = Direction.Down;//默认步兵朝向下方
		calculateFirstFrame();
		curFrame = frames.get(0); // TODO 删除残留设计
		curLittleCenterPoint = lcp;
		curCenterPoint = PointUtil.getCenterPoint(lcp.getX(), lcp.getY());
		curCenterPoint.addSoldier(this);
		curLittleCenterPoint.soldier = this;
		if(curCenterPoint.isInShadow()) {
			this.isHided();
		}
		//步兵的血条
		SoldierBloodBar bar = new SoldierBloodBar(this);
		super.bloodBar = bar;
		Constructor.putOneShapeUnit(bar);
	}
	
	private void calculateFirstFrame() {
		renderProxy.setAppearance(shpSequence, unitColor, 17);
		int stateId = calculateAnimState(this.status, this.curDirection).stateId;
		renderProxy.update(stateId, RuntimeParameter.frameCount, positionX+centerOffX, positionY+centerOffY, 0);
		renderProxy.setEnable(true);
	}
	
	
	/**
	 * 指令控制步兵移动方法
	 */
	@Override
	public void moveToTarget(CenterPoint moveTarget) {
		//校验这个位置是否符合条件
		if(moveTarget==null) {return;}
		//不可达地点
		if(!moveTarget.isSoldierCanOn()) {
			
			movePath=null;
			endTarget = null;
			return;
		}
		LittleCenterPoint targetLCP = PointUtil.getMinDisLCP(curLittleCenterPoint.getX(), curLittleCenterPoint.getY(), moveTarget);
		
		moveToTarget(targetLCP);
	}
	
	
	/**
	 * 移动方法
	 * @param moveTarget
	 */
	public abstract void moveToTarget(LittleCenterPoint moveTarget,LinkedList<LittleCenterPoint> path);
	/**
	 * 移动方法
	 * @param moveTarget
	 */
	public void moveToTarget(LittleCenterPoint moveTarget) {
		
		//从静止状态开始移动的
		if(!isMoving()) {
			SoldierXunLuAdapter xlb = SoldierXunLuAdapter.getInstance();
			List<LittleCenterPoint> planMovePath = xlb.xunlu(curLittleCenterPoint, moveTarget);
			
			if(planMovePath!=null && planMovePath.size()>1) {
				this.nextTarget = planMovePath.get(0);
				this.endTarget = planMovePath.get(planMovePath.size()-1);
				this.movePath = planMovePath;
				status=SoldierStatus.Moving;
				
			}else {
				
				System.out.println("指定位置不可达");
			}
			
			
		}
		//目标正在移动,然后指定新的目标位置
		else {
			stopFlag = true;
			while(true) {
				if(curLittleCenterPoint.equals(nextTarget)) {
					break;
				}else {
					try {
						Thread.sleep(0);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
			//直接从当前位置寻路就可以了
			SoldierXunLuAdapter xlb = SoldierXunLuAdapter.getInstance();
			List<LittleCenterPoint> path = xlb.xunlu(curLittleCenterPoint, moveTarget);
			if(path!=null) {
				this.nextTarget = path.get(0);
				this.movePath = path;
				this.endTarget = path.get(path.size()-1);
				resetTarget = true;
				
				
			}else {
				System.out.println("重指定位置不可达");
			}
			
			stopFlag = false;
			
		}
	
		
	}
	
	/**
	 * 是否正在移动
	 * 这个方法计划为以后移动遮挡和移动避让做准备
	 */
	public boolean isMoving() {
		if(nextTarget==null) {
			return false;
		}else {
			return true;
		}
	}
	/**
	 * 是否已经到达制定目标中心点
	 */
	public boolean isArrivedNextTarget() {
		int nextPositionX = nextTarget.getX()-centerOffX;
		int nextPositionY = nextTarget.getY()-centerOffY;
		if(nextPositionX==positionX && nextPositionY==positionY) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 是否已经到达终点
	 */
	public boolean isArrivedEndTarget() {
		int nextPositionX = endTarget.getX()-centerOffX;
		int nextPositionY = endTarget.getY()-centerOffY;
		if(nextPositionX==positionX && nextPositionY==positionY) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 设置目标方向
	 */
	public void setTargetDirection(LittleCenterPoint nextTarget) {
		int startX = positionX+centerOffX;
		int startY = positionY+centerOffY;
		int targetX = nextTarget.getX();
		int targetY = nextTarget.getY();
		
		//确定使用车身角度
		if( startX-targetX<0 && startY-targetY==0 ) {//右运动
			targetDirection = Direction.Right;
		}
		else if( startX-targetX<0 && startY-targetY<0 ) {//右下运动
			targetDirection = Direction.RightDown;
		}
		else if( startX-targetX==0 && startY-targetY<0 ) {//下运动
			targetDirection = Direction.Down;
		}
		else if( startX-targetX>0 && startY-targetY<0 ) {//左下运动
			targetDirection = Direction.LeftDown;
		}
		else if( startX-targetX>0 && startY-targetY==0 ) {//左运动
			targetDirection = Direction.Left;
		}
		else if( startX-targetX>0 && startY-targetY>0 ) {//左上运动
			targetDirection = Direction.LeftUp;
		}
		else if( startX-targetX==0 && startY-targetY>0 ) {//上运动
			targetDirection = Direction.Up;
		}
		else if( startX-targetX<0 &&  startY-targetY>0 ) {//右上运动
			targetDirection = Direction.RightUp;
		}else {
			targetDirection = curDirection;
		}
		
	}
	
	/**
	 * 走一步的方法
	 * 
	 * 对于会游泳的步兵  需要重写这个方法  以设置正确的帧图
	 */
	public void moveOneStep() {
		
		setTargetDirection(nextTarget);
		if(curDirection!=targetDirection) {
			curDirection=targetDirection;
			umoveIndex = 0;
		}
		
		/**
		 * 确定使用的帧图
		 * 部分步兵会游泳  这里需要重写moveOneStep方法 以获取合适的帧图
		 */
		/*
		List<ShapeUnitFrame> moveFrame = directionMap.get(curDirection);
		ShapeUnitFrame frame = moveFrame.get( (umoveIndex/3)   %moveFrame.size());
		transToColorful(frame); //上阵营色
		 */
		
		umoveIndex+=1;

		/*
		 * 修改位移
		 * 需要注意：当前位置可能不是中心点  所以需要比较目的地的Position坐标
		 */
		int nextTargetX = nextTarget.getX();
		int nextTargetY = nextTarget.getY();
		int nextPositionX = nextTargetX-centerOffX;
		int nextPositionY = nextTargetY-centerOffY;
		
		if(positionX!=nextPositionX) {
			if(Math.abs(positionX-nextPositionX)<2) {//修正补齐
				positionX = nextPositionX;
			}else if(positionX<nextPositionX) {
				positionX+=2;
			}else if(positionX>nextPositionX) {
				positionX-=2;
			}
		}
		
		if(positionY!=nextPositionY) {
			if(Math.abs(positionY-nextPositionY)<1) {//修正补齐
				positionY = nextPositionY;
			}else if(positionY<nextPositionY) {
				positionY+=1;
			}else if(positionY>nextPositionY) {
				positionY-=1;
			}
		}
		
		//更新格子状态
		CenterPoint newCenterPoint = PointUtil.getCenterPoint(positionX+centerOffX, positionY+centerOffY);
		LittleCenterPoint newLCP = PointUtil.getMinDisLCP(positionX+centerOffX, positionY+centerOffY, newCenterPoint);
		
		if(newLCP.equals(curLittleCenterPoint)) {
			
		}else {
			if(!newCenterPoint.isSoldierCanOn()) {
				
			}if(!movePath.contains(newLCP)) {
				
			}else {
				curCenterPoint.removeUnit(this);
				curCenterPoint = newCenterPoint;
				curLittleCenterPoint = newLCP;
				curCenterPoint.addSoldier(this);
				curLittleCenterPoint.soldier = this;//待优化
				if(curCenterPoint.isInShadow()) {
					this.setHided(true);
				}else {
					this.setHided(false);
				}
				
			}
		}
	}
	
	private void updateState() {
		if(status==SoldierStatus.Moving) {
			if(nextTarget==null) {
				status = SoldierStatus.Standing;
				return;
			}
			
			
			//判定是否已经到达下一个地点
			if(isArrivedNextTarget()) {
				if(isArrivedEndTarget()) {//判断是否已经到达终点
					movePath = null;
					nextTarget=null;
					endTarget=null;
					status = SoldierStatus.Standing;
				}else {
					//是否有临时停止标志  有的话  需要暂停计算  等待寻路完成  帧停顿
					if(stopFlag) {
						return;
					}
					
					//若发生重定位,说明movePath变量发生了变化,获取nextTarget的方式要改变
					if(resetTarget) {
						nextTarget = movePath.get(1);
						resetTarget = false;
						moveOneStep();
					}else {//根据当前位置  确认下一个位置
						int curIndex = movePath.indexOf(nextTarget);
						nextTarget = movePath.get(curIndex+1);
						
						//确认这个位置是否可达
						if(nextTarget.isSoldierCanOn()) {
							moveOneStep();
						}else {
							//实现重新规划线路
							xunluLock.lock();
							try {
								SoldierXunLuBean xlb = new SoldierXunLuBean();
								List<LittleCenterPoint> path = xlb.xunlu(curLittleCenterPoint, endTarget);
								if(path!=null) {
									this.movePath = path;
									nextTarget = movePath.get(1);
									moveOneStep();
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
				moveOneStep();
			}
			
		}
		
		
		if(status==SoldierStatus.Ease1) {
			easeIndex++;
			int animLength = InfantryAnimState.AtEase1.animEnd - InfantryAnimState.AtEase1.animStart + 1;

			if(easeIndex == (animLength-1)*InfantryAnimState.AtEase1.slowRate ) {
				status = SoldierStatus.Standing;
				curDirection=Direction.LeftDown;//细节  动作一做完了 站立朝左下
				easeIndex = 0;
			}
			return;
		}
		
		if(status==SoldierStatus.Ease2) {
			easeIndex++;
			int animLength = InfantryAnimState.AtEase2.animEnd - InfantryAnimState.AtEase2.animStart + 1;

			if(easeIndex == (animLength-1)*InfantryAnimState.AtEase2.slowRate ) {
				status = SoldierStatus.Standing;
				curDirection=Direction.RightDown;//细节  动作二做完了 站立朝右下
				easeIndex = 0;
			}
			return;
		}
		
		if(status==SoldierStatus.Standing) {
			
			standingTime++;
			if(standingTime>200) {
				int n = RuntimeParameter.random.nextInt(3);
				if(n==0) {
					status=SoldierStatus.Ease1;
				}else if(n==1){
					status=SoldierStatus.Ease2;
				}else {
					int direction = RuntimeParameter.random.nextInt(8);
					curDirection = Direction.mapping[direction];
				}
				
				standingTime = 0;
				easeIndex = 0;
			}else {
				
			}
		}
	}

	@Override
	public void calculateNextFrame() {
		this.updateState();
		int animId = calculateAnimState(status, curDirection).stateId;
		long startMoment = RuntimeParameter.frameCount;
		switch(status) {
		case Moving:
			startMoment -= umoveIndex;
			break;
		case Ease1:
		case Ease2:
			startMoment -= easeIndex;
			break;
		}
		renderProxy.update(animId, startMoment, positionX+centerOffX, positionY+centerOffY, 0);
	}

	public int getMaxHp() {
		return maxHp;
	}

	public int getCurHp() {
		return curHp;
	}

	public void setCurHp(int curHp) {
		this.curHp = curHp;
	}

	public Direction getTargetDirection() {
		return targetDirection;
	}

	public void setTargetDirection(Direction targetDirection) {
		this.targetDirection = targetDirection;
	}

	public Direction getCurDirection() {
		return curDirection;
	}

	public void setCurDirection(Direction curDirection) {
		this.curDirection = curDirection;
	}

	public LittleCenterPoint getCurLittleCenterPoint() {
		return curLittleCenterPoint;
	}

	public void setCurLittleCenterPoint(LittleCenterPoint curLittleCenterPoint) {
		this.curLittleCenterPoint = curLittleCenterPoint;
	}
	
	/**
	 * 原始帧图换成有颜色的帧图
	 */
	public void transToColorful(ShapeUnitFrame frame) {
		BufferedImage newImg = curFrame.getImg();
		CanvasPainter.clearImage(newImg);
		Graphics g2d = newImg.createGraphics();
		BufferedImage oriImage = frame.getImg();
		g2d.drawImage(oriImage, 0, 0, null);
		giveFrameUnitColor(newImg,frame);//上阵营色
	}
	
	public static InfantryAnimState calculateAnimState(SoldierStatus state, Direction dir) {
		switch(state) {
		case Standing:
			return InfantryAnimState.mapping[InfantryAnimState.StandingUp.stateId + dir.dirId];
		case Moving:
			return InfantryAnimState.mapping[InfantryAnimState.MovingUp.stateId + dir.dirId];
		case Ease1:
			return InfantryAnimState.AtEase1;
		case Ease2:
			return InfantryAnimState.AtEase2;
		default: throw new RuntimeException("Invalid SoldierStatus");
		}
	}
}
