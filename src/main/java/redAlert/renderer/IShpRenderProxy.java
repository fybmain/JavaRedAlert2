package redAlert.renderer;

import redAlert.enums.UnitColor;

public interface IShpRenderProxy {
	public void setEnable(boolean enable);
	public void update(int state, long startMoment, int posX, int posY, int height);
	public void setAppearance(IShpSequence shp, UnitColor campColor, int vlayer);
	public void release();
}
