package redAlert.enums;

/**
 * 8个方向
 */
public enum Direction {
	Up(0),Down(4),Left(2),Right(6),LeftUp(1),RightUp(7),LeftDown(3),RightDown(5);

	public final int dirId;
	
	private Direction(int dirId) {
		this.dirId = dirId;
	}

	public static final Direction[] mapping = {
			Up, LeftUp, Left, LeftDown, Down, RightDown, Right, RightUp,
	};
	
}
