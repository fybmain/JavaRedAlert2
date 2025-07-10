package j3d;

import java.awt.Dimension;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

public class J3dTest {

	
	public static void main(String[] args) {
		
		// create a canvas to display the 3D scene
        Canvas3D canvas = new Canvas3D(com.sun.j3d.utils.universe.SimpleUniverse.getPreferredConfiguration());
		
        com.sun.j3d.utils.universe.SimpleUniverse universe = new com.sun.j3d.utils.universe.SimpleUniverse(canvas);
        
     // create a branch group to hold the objects in the scene
        BranchGroup scene = new BranchGroup();
        
     // create a transform group to control the position of the objects
        TransformGroup tg = new TransformGroup();
        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3d(0.0, 0.0, -3.0));
        tg.setTransform(transform);
        
        
     // add the 3D model to the scene
        
        tg.addChild(new com.sun.j3d.utils.geometry.ColorCube(0.8));
        
        scene.addChild(tg);
        
     // create a point light to illuminate the scene
        PointLight light = new PointLight();
        light.setPosition(new Point3f(1.0f, 1.0f, 1.0f));
        light.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));

        // add the point light to the scene
        scene.addChild(light);
        
        // add the scene to the universe
        universe.addBranchGraph(scene);
        
        JFrame jf = new JFrame("3D学习");
		jf.setSize(1000,500);
		jf.setResizable(false);
		jf.setAlwaysOnTop(false);//总是在最上层
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLocationRelativeTo(null);//屏幕居中
		jf.setVisible(true);
		
		JPanel mp = new JPanel();
		mp.setLocation(0, 0);
		mp.setVisible(true);
		mp.setLayout(null);
		mp.setMinimumSize(new Dimension(1000,500));
		mp.setPreferredSize(new Dimension(1000,500));
		canvas.setVisible(true);
		mp.add(canvas);
		
		jf.add(mp);
		jf.pack();
		
		
        
	}
}
