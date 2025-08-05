package redAlert.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import redAlert.RuntimeParameter;
import redAlert.resourceCenter.RenderResourceCenter;
import redAlert.shapeObjects.ShapeUnit;
import redAlert.shapeObjects.VirtualShapeUnit;
import redAlert.utilBean.FrameCalculateThread;

/**
 * 方块帧计算调度任务
 * 
 * 由此类管理的帧计算线程进行逻辑计算,计算后,再由场景渲染任务根据计算结果绘制画面
 * 
 * 也可以说是游戏场景内物品计算任务
 */
public class ShapeUnitCalculateTask extends Thread{

	/**
	 * 串行计算游戏逻辑
	 */
	public boolean serial = true;
	/**
	 * 方块(ShapeUnit)帧计算线程数量
	 */
	public int workerNum = 10;
	/**
	 * 方块(ShapeUnit)帧计算线程队列
	 */
	public ArrayBlockingQueue<Runnable> workersQueue = new ArrayBlockingQueue<Runnable>(workerNum);
	/**
	 * 线程池中的阻塞队列
	 * 几乎无实际作用
	 */
	public ArrayBlockingQueue<Runnable> abq = new ArrayBlockingQueue<>(3);
	/**
	 * 方块(ShapeUnit)帧计算线程池
	 */
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(workerNum,workerNum+5,60, TimeUnit.SECONDS,abq,new ThreadPoolExecutor.CallerRunsPolicy());
    /**
     * SHP方块阻塞队列的引用
     */
    public ArrayBlockingQueue<ShapeUnit> shapeUnitBlockingQueue = null;
	/**
	 * 附加任务队列（其他线程操作单位列表）
	 */
	public ConcurrentLinkedQueue<Runnable> remoteTaskQueue = new ConcurrentLinkedQueue<Runnable>();
    /**
     * 标记队列末尾
     */
    public VirtualShapeUnit virtualShapeUnit = new VirtualShapeUnit();
    /**
     * 前一帧时间戳
     */
    private volatile long prevFrameTime = 0;
    
    
    public ShapeUnitCalculateTask(ArrayBlockingQueue<ShapeUnit> shapeUnitBlockingQueue) {
    	shapeUnitBlockingQueue.offer(virtualShapeUnit);
    	this.shapeUnitBlockingQueue = shapeUnitBlockingQueue;
    	initCalculateQueue();
    }
    
    /**
     * 初始化方块帧计算线程队列
     * 
     * 生成若干个方块帧计算线程worker
     * worker循环使用,方块的逻辑计算结束后再放回workersQueue
     *  
     */
    public void initCalculateQueue() {
		
		for(int i=0;i<workerNum;i++) {
			FrameCalculateThread worker = new FrameCalculateThread(workersQueue);
			workersQueue.offer(worker);
		}
    }
    
    /**
     * 启动方法
     */
    public void startCalculateTask() {
    	this.start();
    }
    
    public void remoteInvoke(Runnable task) {
    	remoteTaskQueue.add(task);
        LockSupport.unpark(this); // 唤醒逻辑计算线程
    }

    /**
     * 调度线程启动
     * 
     * 调度线程监测SHP方块阻塞队列,若发现有方块计算任务,则取出;若没有,则阻塞
     * 调度线程若发现帧计算线程队列中有空闲的worker,则分配worker进行计算
     * 若没有空闲的worker则阻塞直到有空闲的worker
     */
	@Override
	public void run() {
		if(serial) {
			serialRun();
			return;
		}
		while(true) {
			try {
				ShapeUnit shp = shapeUnitBlockingQueue.take();//当队列为空时,会阻塞,降低CPU占用
				if(shp==virtualShapeUnit) {
					// 等待逻辑帧所有任务完成
					while(workersQueue.size()<workerNum) {
						Thread.sleep(0);
					}
					// 向渲染线程提交当前帧用于绘制
					RenderResourceCenter.renderer.commitGameFrame();
					shapeUnitBlockingQueue.offer(virtualShapeUnit);
				}else {
					FrameCalculateThread worker = (FrameCalculateThread)workersQueue.take();//当没有空闲的帧计算线程时,会阻塞
					worker.setShp(shp);//分配任务
					threadPoolExecutor.execute(worker);
				}
			}catch(InterruptedException e) {
				continue;
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
    
    /**
     * 逻辑线程
     */
	public void serialRun() {
		ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
		FrameCalculateThread worker = new FrameCalculateThread(queue);
		queue.offer(worker);
		this.prevFrameTime = System.nanoTime();
		while(true) {
			try {
				ShapeUnit shp = shapeUnitBlockingQueue.poll();
				if(shp==virtualShapeUnit) {
					// 向渲染线程提交当前帧用于绘制
					RenderResourceCenter.renderer.commitGameFrame();
					shapeUnitBlockingQueue.offer(virtualShapeUnit);

					while(true) {
						while(!remoteTaskQueue.isEmpty()) {
							remoteTaskQueue.poll().run(); // 处理remoteInvoke
						}
						// 延迟一段时间，控制帧率
						long frameInterval = 1000_000_000l / RuntimeParameter.fps;
						long remainingTime = this.prevFrameTime + frameInterval - System.nanoTime();
						if(remainingTime > 0) {
							LockSupport.parkNanos(remainingTime); // 等待下一帧或被remoteInvoke唤醒
						}else {
							break;
						}
					}
				}else {
					queue.take();
					worker.setShp(shp);//分配任务
					worker.run();
				}
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
