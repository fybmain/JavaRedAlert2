package game3D;

//只用于三角形的渲染
public class Rasterizer {

	//设置屏幕的分辨率
	public static int screen_w = MainThread.screen_w;
	public static int screen_h = MainThread.screen_h;
	public static int half_screen_w = MainThread.half_screen_w;
	public static int half_screen_h = MainThread.half_screen_h;;
	
	//屏幕的像素组
	public static int[] screen = MainThread.screen;
	//视角原点到屏幕的距离  通常为屏宽的一半
	public static int screenDistance = screen_w/2;
	
	//未经变换的三角形顶点
	public static Vector3D [] triangleVertices;
	//变换后的三角形顶点
	public static Vector3D [] updatedVertices;
	
	public static void main(String[] args) {
		String a = null;
		
		String b = a;
		
		a = "xxx";
		
		System.out.println(b);
	}
}
