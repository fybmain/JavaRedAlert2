package redAlert.renderer;

import java.awt.Component;
import java.util.List;

import redAlert.ShapeUnitFrame;

/**
 * 游戏画面渲染器接口
 */
public interface IRenderer {
	
	public IShpSequence registerShpSequence(ShpSequenceInfo info);
	
	public IShpRenderProxy registerShpRenderProxy();
	
	public void commitGameFrame();
	
	public Component getTargetComponent();
}
