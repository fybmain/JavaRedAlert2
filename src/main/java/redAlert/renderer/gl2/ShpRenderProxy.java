package redAlert.renderer.gl2;

import java.util.concurrent.atomic.AtomicBoolean;

import redAlert.enums.UnitColor;
import redAlert.renderer.IShpRenderProxy;
import redAlert.renderer.IShpSequence;

public final class ShpRenderProxy implements IShpRenderProxy {
	private AtomicBoolean released = new AtomicBoolean(false);
	private final GL2Renderer renderer;
	private final int id;
	
	ShpRenderProxy(GL2Renderer renderer, int id) {
		this.renderer = renderer;
		this.id = id;
	}
	
	public void setEnable(boolean enable) {
		renderer.getFrameDataForProxy().shpEnableMap.set(id, enable);
	}

	public void update(int state, long startMoment, int posX, int posY, int height) {
		ShpDrawState drawState = getDrawStateForUpdate();
		drawState.state = state;
		drawState.startMoment = startMoment;
		drawState.posX = posX;
		drawState.posY = posY;
		drawState.height = height;
	}
	
	public void setAppearance(IShpSequence shp, UnitColor campColor, int vlayer) {
		assert shp instanceof ShpSequence;
		ShpSequence newSeq = (ShpSequence)shp;
		assert newSeq != null;
		assert newSeq.drawable == renderer.drawable;
		
		ShpDrawState state = getDrawStateForUpdate();
		state.shpSequence = newSeq;
		state.campColor = campColor;
		state.vlayer = vlayer;
	}
	
	public void release() {
		assert !released.get(); //不允许重复释放
        if (released.compareAndSet(false, true)) {
        	setEnable(false);
        	FrameData frame = renderer.getFrameDataForProxy();
        	frame.shpUpdateMap.set(id);
        	frame.shpDrawStates.set(id, null);
        	renderer.shpIdPool.free(id);
        }
	}
	
    @Override
    protected void finalize() {
        if (released.compareAndSet(false, true)) {
        	setEnable(false);
        	renderer.shpIdPool.free(id);
        }
    }

	private final ShpDrawState getDrawStateForUpdate() {
		assert !released.get(); //释放之后不允许再使用
		FrameData frame = renderer.getFrameDataForProxy();
		if(!frame.shpUpdateMap.get(id)) {
			ShpDrawState prevState = frame.prevFrameData.shpDrawStates.get(id);
			frame.shpDrawStates.set(id, (ShpDrawState) (prevState.clone()));
			frame.shpUpdateMap.set(id);
		}
		return frame.shpDrawStates.get(id);
	}
}
