package redAlert.resourceCenter;

import java.util.concurrent.ConcurrentHashMap;

import redAlert.renderer.IRenderer;
import redAlert.renderer.IShpSequence;

public class RenderResourceCenter {
	
	/**
	 * 渲染器
	 */
	public static IRenderer renderer;
	
	/**
	 * 注册到渲染器的SHP动画帧资源集合
	 */
	public static ConcurrentHashMap<Class, IShpSequence> shpSequences = new ConcurrentHashMap<>();
}
