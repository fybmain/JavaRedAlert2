package game3D;

public class LookupTables {

	public static float[] sin;  
	public static float[] cos;
	
	
	public static void init() {
		//Make sin and cos look up tables
		sin = new float[361];
		cos = new float[361];
		for(int i = 0; i < 361; i ++){
			sin[i] = (float)Math.sin(Math.PI*i/180);
			cos[i] = (float)Math.cos(Math.PI*i/180);
		}
	}
}
