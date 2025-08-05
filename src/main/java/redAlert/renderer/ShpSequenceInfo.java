package redAlert.renderer;

import java.util.List;

import redAlert.ShapeUnitFrame;

/*
 * 用于提供信息构建ShpSequence的结构体
 */
public class ShpSequenceInfo {
	public List<ShapeUnitFrame> frames;
	public int[][] states;
	public int centerOffX, centerOffY;
	public int groundSizeX, groundSizeY;
}
