package redAlert;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import redAlert.enums.MouseStatus;
import redAlert.shapeObjects.ShapeUnit;
import redAlert.task.ShapeUnitCalculateTask;
import redAlert.utilBean.CenterPoint;

/**
 * 运行时参数
 * 游戏过程中会不断变化的参数
 */
public class RuntimeParameter {
	/**
	 * 视口在地图上偏移量
	 * 视野偏移量,用于确认渲染的范围
	 */
	public static int viewportOffX = 0;
	public static int viewportOffY = 0;
	/**
	 * 帧率
	 * 
	 * 红警2游戏速度5时帧率为60帧/秒
	 */
	public static int fps = 60;
	
	/**
	 * 游戏逻辑线程
	 */
	public static ShapeUnitCalculateTask gameLogicThread = new ShapeUnitCalculateTask();
	/**
	 * 逻辑线程队列中添加方块
	 */
	public static void addUnitToQueue(ShapeUnit unit) {
		gameLogicThread.addUnitToLogicQueue(unit);
	}
	/**
	 * 通知逻辑线程队列结束
	 */
	public static void finishUnitQueue() {
		gameLogicThread.finishLogicQueue();
	}
	
	/**
	 * 用于逻辑线程向渲染线程传递SHP绘制队列的过程
	 */
	public static final ArrayBlockingQueue<PriorityQueue<ShapeUnit>> unitQueueGate = new ArrayBlockingQueue<>(1);
	/**
	 * 获取当前SHP绘制队列
	 */
	public static PriorityQueue<ShapeUnit> takeDrawShapeUnitList() {
		while(true) {
			try { return unitQueueGate.take(); }
			catch(InterruptedException e){ continue; }
		}
	}
	/**
	 * 向当前SHP缓存队列中添加建筑
	 */
	public static void addBuildingToQueue(ShapeUnit unit) {
		gameLogicThread.addToCacheQueue(unit);
	}
	
	/**
	 * 上次鼠标指针在MainPanel的中心点
	 */
	public static CenterPoint lastMoveCenterPoint = null;
	/**
	 * 上次鼠标指针在MainPanel的坐标
	 */
	public static int lastMoveX,lastMoveY;
	/**
	 * 帧计数
	 */
	public static long frameCount = 0;
	/**
	 * 鼠标在游戏场景界面按下时坐标
	 * 触发mousePressed事件时更新
	 */
	public static int pressX=0,pressY=0;
	/**
	 * 当前鼠标状态
	 */
	public static MouseStatus mouseStatus = MouseStatus.Idle;
	
	/**
	 * 随机数生成器
	 */
	public static Random random = new Random();
	
	public static final boolean debugMode =
		java.lang.management.ManagementFactory.getRuntimeMXBean()
			.getInputArguments().toString().contains("jdwp");
}
