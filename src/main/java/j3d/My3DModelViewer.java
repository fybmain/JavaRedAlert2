package j3d;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

//知乎上的例子  只能显示一下
public class My3DModelViewer extends javax.swing.JFrame{

	public My3DModelViewer() {
	        initComponents();
	    }

	    private void initComponents() {
	        // create a canvas to display the 3D scene
	        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

	        // create a simple universe and attach the canvas to it
	        SimpleUniverse universe = new SimpleUniverse(canvas);

	        // create a branch group to hold the objects in the scene
	        BranchGroup scene = new BranchGroup();

	        // create a transform group to control the position of the objects
	        TransformGroup tg = new TransformGroup();
	        Transform3D transform = new Transform3D();
	        transform.setTranslation(new Vector3d(0.0, 0.0, -3.0));
	        tg.setTransform(transform);

	        // load a 3D model from a file
//	        Loader loader = new Loader();
//	        Scene model = loader.load("my3dmodel.obj");

	        // add the 3D model to the scene
//	        tg.addChild(model.getSceneGroup());
	      //创建一个正方体
			ColorCube cube = new ColorCube(0.3);
			tg.addChild(cube);
	        
	        scene.addChild(tg);

	        // create a point light to illuminate the scene
	        PointLight light = new PointLight();
	        light.setPosition(new Point3f(1.0f, 1.0f, 1.0f));
	        light.setInfluencingBounds(new BoundingSphere(new Point3d(), 100.0));

	        // add the point light to the scene
	        scene.addChild(light);

	        // add the scene to the universe
	        universe.addBranchGraph(scene);

	        // add the canvas to the JFrame
	        getContentPane().add(canvas, java.awt.BorderLayout.CENTER);

	        // set the size of the JFrame
	        setSize(500, 500);

	        // display the JFrame
	        setVisible(true);
	        
	}

	public static void main(String args[]) {
	    // create and display the JFrame
	    java.awt.EventQueue.invokeLater(new Runnable() {
	        public void run() {
	        	My3DModelViewer mm  = new My3DModelViewer();
	        	mm.setVisible(true);
	        	/*
	        	try {
	        		while(true) {
		        		mm.repaint();
		        		Thread.sleep(50);
		        	}
        		}catch (Exception e) {
					e.printStackTrace();
				}
				*/
	        	
	        }
	    });
	}
}
