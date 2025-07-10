package j3d;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;

public class HelloUniverse extends Applet {

	private static final long serialVersionUID = 41293118815177506L;

	private com.sun.j3d.utils.universe.SimpleUniverse u = null;

	public BranchGroup createSceneGraph() {

		BranchGroup objRoot = new BranchGroup();

		TransformGroup objTrans = new TransformGroup();

		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		objRoot.addChild(objTrans);

		// 创建一个3D对象，正方体

		objTrans.addChild(new com.sun.j3d.utils.geometry.ColorCube(0.3));

		Transform3D yAxis = new Transform3D();

		//-1表示循环次数,9000表示正方形旋转一周的时间是9秒
		Alpha rotationAlpha = new Alpha(-1, 9000);//-1 6000   可以控制旋转的速度
		//旋转插值器
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,objTrans, yAxis, 0.0f, (float) Math.PI * 2.0f);

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),100.0);

		rotator.setSchedulingBounds(bounds);

		objRoot.addChild(rotator);

		objRoot.compile();
		
		

		return objRoot;

	}

	public HelloUniverse() {
	
	}

	@Override
	public void init() {
	
		setLayout(new BorderLayout());
	
		GraphicsConfiguration config =  com.sun.j3d.utils.universe.SimpleUniverse.getPreferredConfiguration();
		
		Canvas3D c = new Canvas3D(config);
		
		add("Center", c);
		
		BranchGroup scene = createSceneGraph();
		
		u = new  com.sun.j3d.utils.universe.SimpleUniverse(c);
		
		//设置视点位置
		u.getViewingPlatform().setNominalViewingTransform();
		
		u.addBranchGraph(scene);
	
	}

	@Override
	public void destroy() {
	
	u.cleanup();
	
	}

	public static void main(String[] args) {
	
		new com.sun.j3d.utils.applet.JMainFrame(new HelloUniverse(), 512, 512);
	
	}

}
