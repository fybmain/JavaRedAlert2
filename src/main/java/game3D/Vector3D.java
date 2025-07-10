package game3D;

public class Vector3D {
	//矢量在x,y,z轴上的分量
	public float x, y, z;
	
	//构造方法
	public Vector3D(float x, float y, float z){ 
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * 矢量相加
	 */
	public void add(Vector3D v){
		x+=v.x;
		y+=v.y;
		z+=v.z;
	}
	/**
	 * 矢量相减
	 */
	public void subtract(Vector3D v){
		x-=v.x;
		y-=v.y;
		z-=v.z;
	}
	
	//矢量点积，结果代表两个矢量之间的相似程度
	public float dot(Vector3D v){
		return x*v.x + y*v.y + z*v.z;
	}
	//矢量差积，求一个与两个矢量都垂直的矢量
	public void cross(Vector3D v1, Vector3D v2){
		x = v1.y*v2.z - v1.z*v2.y;
		y = v1.z*v2.x - v1.x*v2.z;
		z = v1.x*v2.y - v1.y*v2.x;
	}
	
	//返回矢量长度
	public float getLength(){
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	//矢量单位化
	public void unit(){
		float length = getLength();
		x = x/length;
		y = y/length;
		z = z/length;
	}
	
	//矢量放大缩小
	public void scale(float d){
		x*=d;
		y*=d;
		z*=d;
	}
	
	//绕Y轴旋转矢量
	public void  rotate_Y(int angle){
		float sin = LookupTables.sin[angle];
		float cos = LookupTables.cos[angle];
		float old_X = x;
		float old_Z = z;
		x = cos*old_X - sin*old_Z;
		z = sin*old_X + cos*old_Z;
	}
	
	//绕X轴旋转矢量
	public void rotate_X(int angle){
		float sin = LookupTables.sin[angle];
		float cos = LookupTables.cos[angle];
		float old_Y = y;
		float old_Z = z;
		y = cos*old_Y - sin*old_Z;
		z = sin*old_Y + cos*old_Z;
	}
	
	//绕Z轴旋转矢量
	public void rotate_Z(int angle){
		float sin = LookupTables.sin[angle];
		float cos = LookupTables.cos[angle];
		float old_X = x;
		float old_Y = y;
		x = cos*old_X - sin*old_Y;
		y = sin*old_X + cos*old_Y;
	}
}
