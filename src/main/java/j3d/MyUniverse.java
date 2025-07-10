package j3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class MyUniverse {

	public static void main(String[] args) throws Exception{
	    
		
		//创建投影平面
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		System.out.println(canvas.getWidth());
		//创建简单宇宙  包含一个视图平台
		SimpleUniverse universe = new SimpleUniverse(canvas);
		
	  //定义BranchGroup对象
		BranchGroup branchGroup = new BranchGroup();
		//定义TransformGroup
		TransformGroup transGroup = new TransformGroup();
		//定义TransformGroup的读写能力
		transGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		branchGroup.addChild(transGroup);
		
		//创建一个正方体
		ColorCube cube = new ColorCube(0.3);
		transGroup.addChild(cube);
		//创建一个变换对象
		Transform3D yAxis = new Transform3D();
		//定义运动次数和周期  9000表示正方形旋转一周期的时间是9秒
		Alpha rotationAlpha = new Alpha(-1, 3000);//-1 6000   可以控制旋转的速度
		//旋转插值器
		RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,transGroup, yAxis, 0.0f, (float) Math.PI * 2.0f);
		//中心和边界
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),100.0);
		rotator.setSchedulingBounds(bounds);
		
		branchGroup.addChild(rotator);
		branchGroup.compile();
		
		
		universe.addBranchGraph(branchGroup);
		
		
		
		canvas.setBounds(0, 0, 1000, 500);
		canvas.setVisible(true);
		
		BufferedImage iamge = new BufferedImage(canvas.getWidth(),canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
		canvas.paint(iamge.getGraphics());
		ImageIO.write(iamge, "png", new File("E:/outxxx.png"));
		
		
		
		
		
		//GUI相关
		/*
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
		mp.setLayout(new BorderLayout());
		mp.setMinimumSize(new Dimension(1000,500));
		mp.setPreferredSize(new Dimension(1000,500));
		canvas.setVisible(true);
		mp.add("Center",canvas);
		
		jf.add(mp);
		jf.pack();
		
		try {
			while(true) {
    			mp.repaint();
    			Thread.sleep(1000);
    		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
//		Thread.sleep(5000);
		/*
		BufferedImage image = new BufferedImage(1000, 500, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2d = image.createGraphics();
	    canvas.paint(g2d); // 从Canvas3D绘制到BufferedImage
	    g2d.dispose();
	    try {
	        ImageIO.write(image, "PNG", new File("E:/output.png")); // 保存图片到文件
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		*/
	    
	    
	}
}
