package redAlert.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

import redAlert.RuntimeParameter;
import redAlert.resourceCenter.RenderResourceCenter;
import redAlert.shapeObjects.ShapeUnit;
import redAlert.shapeObjects.VirtualShapeUnit;
import redAlert.utilBean.FrameCalculateThread;

/**
 * 方块帧计算线程
 * 
 * 由此线程进行逻辑计算,计算后,再由场景渲染任务根据计算结果提交绘制数据
 * 
 * 也可以说是游戏场景内物品计算线程
 */
public class ShapeUnitCalculateTask extends Thread{

	/**
	 * 串行计算游戏逻辑
	 */
	public final boolean serial = true;
    /**
     * 前一帧时间戳
     */
    private volatile long prevFrameTime = 0;
    
	/**
	 * SHP方块阻塞队列
	 * 注意:此队列只用于逻辑计算
	 * 为什么用阻塞队列:
	 *   当队列中的元素处理完毕放入规划队列时，此时会阻塞，避免建筑规划线程不停的轮询，减少CPU占用
	 */
	private final ArrayBlockingQueue<ShapeUnit> shapeUnitBlockingQueue = new ArrayBlockingQueue<ShapeUnit>(150);
	/**
	 * 附加任务队列（其他线程操作单位列表）
	 */
	public ConcurrentLinkedQueue<Runnable> remoteTaskQueue = new ConcurrentLinkedQueue<Runnable>();
	
	/**
	 * 用于标记队列末尾
	 */
	private final VirtualShapeUnit virtualShapeUnit = new VirtualShapeUnit();
	
	private PriorityQueue<ShapeUnit> cacheQueue = new PriorityQueue<>(150);
	
    public ShapeUnitCalculateTask() {
    	super("游戏逻辑线程");
    	shapeUnitBlockingQueue.offer(virtualShapeUnit);
    }
    
    /**
     * 计算线程启动方法
     */
    public void startCalculateTask() {
    	this.start();
    }
    
    public void remoteInvoke(Runnable task) {
    	remoteTaskQueue.offer(task);
        LockSupport.unpark(this); // 唤醒逻辑计算线程
    }
    
	/**
	 * 添加ShapeUnit
	 */
	public void addUnitToLogicQueue(ShapeUnit unit) {
		shapeUnitBlockingQueue.offer(unit);
	}
	
	public void finishLogicQueue() {
		shapeUnitBlockingQueue.offer(virtualShapeUnit);
	}
	
	public void addToCacheQueue(ShapeUnit unit) {
		Runnable task = () -> {
			if(cacheQueue.contains(unit)) {
				System.out.println("有重复SHP对象, 忽略");
			}else {
				cacheQueue.offer(unit);
			}
		};
		if(Thread.currentThread()==this) {
			task.run();
		} else {
			remoteInvoke(task);
		}
	}
    
	private void handleShapeUnits() throws InterruptedException {
		ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
		FrameCalculateThread worker = new FrameCalculateThread(queue);
		queue.offer(worker);
		
		while(true) {
			ShapeUnit shapeUnit = shapeUnitBlockingQueue.take();
			if(shapeUnit==virtualShapeUnit) {
				return;
			}
			queue.take();
			worker.setShp(shapeUnit); //分配任务
			worker.run();
		}
	}

    /**
     * 计算线程执行过程
     * 
     * 计算线程扫描SHP方块队列,执行逻辑计算任务
     */
	@Override
	public void run() {
		assert serial;

		this.prevFrameTime = System.nanoTime();
		while(true) {
			try {
				handleShapeUnits();
				RuntimeParameter.unitQueueGate.offer(cacheQueue);
				RenderResourceCenter.renderer.commitGameFrame();
				cacheQueue = new PriorityQueue<ShapeUnit>(150);
				
				long frameInterval = 1000_000_000l / RuntimeParameter.fps;
				long targetTime = Long.max(this.prevFrameTime + frameInterval, System.nanoTime());
				while(true) {
					while(!remoteTaskQueue.isEmpty()) {
						remoteTaskQueue.poll().run(); // 处理remoteInvoke
						System.gc();
					}
					
					// 延迟一段时间，控制帧率
					long currentTime = System.nanoTime();
					if(currentTime < targetTime) {
						LockSupport.parkNanos(targetTime - currentTime); // 等待下一帧或被remoteInvoke唤醒
					}else {
						break;
					}
				}
				this.prevFrameTime = targetTime;
			}catch(InterruptedException e) {
				continue;
			}catch(Exception e) {
				e.printStackTrace();
				if(RuntimeParameter.debugMode) {
					throw e;
				}else {
					continue;
				}
			}
		}
	}
}
