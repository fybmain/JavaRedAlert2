package redAlert.renderer.gl2;

import java.awt.Component;

import com.jogamp.opengl.GLAutoDrawable;

import redAlert.renderer.IRenderer;
import redAlert.renderer.IdPool;
import redAlert.renderer.ShpSequenceInfo;
import redAlert.renderer.TripleBufferIndex;

/**
 * 基于OpenGL 2渲染游戏画面
 */
public final class GL2Renderer implements IRenderer {
	final GLAutoDrawable drawable;
	final RenderPanel panel;
	
	private final FrameData[] frameData = new FrameData[] {
			new FrameData(), new FrameData(), new FrameData(),
	};
	private final TripleBufferIndex frameDataIndex = new TripleBufferIndex((byte)0, (byte)1, (byte)2);
	final IdPool shpIdPool = new IdPool(100_000);
	
	public GL2Renderer() {
		this.panel = new RenderPanel(this);
		
		this.drawable = panel;
		this.frameData[0].prevFrameData = this.frameData[2];
	}
	
	public void scanInput() {
		FrameData frameData = getFrameDataForProxy();
	}
	
	public ShpSequence registerShpSequence(ShpSequenceInfo info) {
		return new ShpSequence(drawable, info);
	}
	
	public ShpRenderProxy registerShpRenderProxy() {
		int id = shpIdPool.allocate();
		FrameData frameData = getFrameDataForProxy();
		frameData.shpUpdateMap.set(id);
		frameData.shpEnableMap.clear(id);
		if(id<frameData.shpDrawStates.size()) {
			frameData.shpDrawStates.set(id, new ShpDrawState());
		}else {
			frameData.shpDrawStates.add(new ShpDrawState());
		}
		return new ShpRenderProxy(this, id);
	}
	
	public void commitGameFrame() {
		byte prevIndex = frameDataIndex.writerSwap();
		FrameData prevData = frameData[prevIndex];
		FrameData newData = frameData[frameDataIndex.getWriteBuffer()];
		newData.copyFrom(prevData);
	}

	public Component getTargetComponent() {
		return this.panel;
	}
	
	void rendererSwapGameFrame() {
		frameDataIndex.readerSwap();
	}
	
	FrameData getFrameDataForProxy() {
		return frameData[frameDataIndex.getWriteBuffer()];
	}
	
	FrameData getFrameDataForRenderer() {
		return frameData[frameDataIndex.getReadBuffer()];
	}
}
