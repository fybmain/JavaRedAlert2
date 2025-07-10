package j3d;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class Test2 {
	
	
	
	
	
	
    public static void main(String[] args) {
    	new Line3DShape();
    }
}



class LineShape extends Shape3D{
	// 直线的定点坐标
    private float vert[ ]={
                0.5f,0.5f,0.0f, -0.5f,0.5f,0.0f,
                0.3f,0.0f,0.0f, -0.3f,0.0f,0.0f,
                -0.5f,-0.5f,0.0f, 0.5f,-0.5f,0.0f};
    // 各定点的颜色
    private float color[ ]={
               0.0f,0.5f,1.0f,  0.0f,0.5f,1.0f,
               0.0f,0.8f,2.0f,  1.0f,0.0f,0.3f,
               0.0f,1.0f,0.3f,  0.3f,0.8f,0.0f};
    
    public LineShape( ) {
        // 创建直线数组对象
        LineArray line=new LineArray(6,LineArray.COORDINATES|LineArray.COLOR_3);
        // 设置直线对象的坐标数组
        line.setCoordinates(0,vert);
        // 设置直线对象的颜色数组
        line.setColors(0,color);
        // 创建直线属性对象
        LineAttributes linea=new LineAttributes( );
        // 设置线宽
        linea.setLineWidth(10.0f);
        // 设置直线的渲染效果
        linea.setLineAntialiasingEnable(true);
 
        Appearance app=new Appearance( );  
        app.setLineAttributes(linea);
        this.setGeometry(line);
        this.setAppearance(app);
    }
}

class Line3DShape{
	public Line3DShape(){
		//构建空间 和物体
        
        // 创建一个虚拟空间
        SimpleUniverse universe = new  SimpleUniverse();
        // 创建一个用来包含对象的数据结构
        BranchGroup group = new BranchGroup();
        // 创建直线形状对象把它加入到group中
        Shape3D shape=new LineShape();
        group.addChild(shape);
        
        //灯光构造
        Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);
        // 设置光线的颜色
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        // 设置光线的作用范围
        Vector3f light1Direction  = new Vector3f(4.0f, -7.0f, -12.0f);
        // 设置光线的方向
        DirectionalLight light1= new DirectionalLight(light1Color, light1Direction);
          // 指定颜色和方向，产生单向光源
        light1.setInfluencingBounds(bounds);
        // 把光线的作用范围加入光源中
        group.addChild(light1);
        // 将光源加入group组
        // 安放观察点
        universe.getViewingPlatform().setNominalViewingTransform();
        // 把group加入到虚拟空间中
        universe.addBranchGraph(group); 
        
        
    }
}
