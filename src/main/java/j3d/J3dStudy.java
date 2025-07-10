package j3d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import redAlert.enums.UnitColor;
import redAlert.utils.PalFileReader;

/**
 * 学习J3D的基础用法和实验
 */
public class J3dStudy {

	/**
	 * TODO 从最简单的学起
	 * 展示一个静态彩色正方体
	 */
	public static void test() {
		//1.创建一个简单宇宙空间
		SimpleUniverse universe = new SimpleUniverse();
		//2.创建根组,并设置根组的变换权限
		BranchGroup rootGroup = new BranchGroup();
		//3.创建整体变换组,并设置整体变换组的变换权限,整体变换组可以实施整个空间的3D变换
		TransformGroup topTransGroup = new TransformGroup();
		topTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//4.创建整体变换组的3D变换(可以用来做一些初始化的工作,也可以不做此步骤)
		Transform3D topTransform3D = new Transform3D();
		topTransform3D.setTranslation(new Vector3d(0,0,0));//设置3D变换的位移分量
		//5.设置整体变换组的3D变换
		topTransGroup.setTransform(topTransform3D);
		
		//6.创建一个用于正方体的变换组
		TransformGroup cubeGroup = new TransformGroup();
		cubeGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//7.创建一个彩色正方体,并放入正方体变换组
		ColorCube cube = new ColorCube(0.3);
		cubeGroup.addChild(cube);
		//8.将正方体变换组放入整体变换组
		topTransGroup.addChild(cubeGroup);
		
		//由于空间中必须有一个线性插值对象,空间中的图像才能不断显示，所以必须放入一个，否则只显示一帧就不显示了
		//9.创建一个位移线性插值器，并放入整体变换组
		Alpha alpha = new Alpha(-1,Alpha.INCREASING_ENABLE,0,0,4000,0,0,0,0,0);
		PositionInterpolator positionInterpolator = new PositionInterpolator(alpha,cubeGroup);
		positionInterpolator.setStartPosition(0);
		positionInterpolator.setEndPosition(0);//不设置的话,PositionInterpolator默认在横坐标上从0挪到1,这样就不静态了
		positionInterpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
		topTransGroup.addChild(positionInterpolator);
		
		//添加辅助工具
		addSupport(topTransGroup);//增加辅助工具
		
		//10.将整体变换组放入根组
		rootGroup.addChild(topTransGroup);
		//11.将根组放入宇宙
		universe.addBranchGraph(rootGroup);
		
		//12.设置观察视角
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.getViewer().getView().setBackClipDistance(100);
		universe.getViewer().getView().setFrontClipDistance(0.1);
		universe.getViewer().getView().setFieldOfView(Math.PI*2/3);//视角
		
		
		//*13.获取正方体的平移变换坐标
		Transform3D boxTransform3D2 = new Transform3D();
		cube.getLocalToVworld(boxTransform3D2);
		Vector3d translation = new Vector3d();//创建向量以存储平移分量
		boxTransform3D2.get(translation);
		System.out.println(translation);//打印位置
	}
	/**
	 * universe结合Swing的基础用法
	 */
	public static void test22() throws Exception{
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas.setBounds(0, 0, 1000, 500);
		canvas.setVisible(true);
		SimpleUniverse universe = new SimpleUniverse(canvas);
		
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
		mp.setOpaque(false);
		
		Robot robot = new Robot();
        Rectangle captureSize = canvas.getBounds();
        BufferedImage screenshot = robot.createScreenCapture(captureSize);
        ImageIO.write(screenshot, "png", new File("screenshot.png"));
		
		jf.add(mp);
		jf.pack();
	}
	
	/**
	 * TODO 展示一个旋转的正方体
	 */
	public static void test2() {
		//1.创建一个简单宇宙空间
		SimpleUniverse universe = new SimpleUniverse();
		//2.创建根组,并设置根组的变换权限
		BranchGroup rootGroup = new BranchGroup();
		//3.创建整体变换组,并设置整体变换组的变换权限,整体变换组可以实施整个空间整体的3D变换
		TransformGroup topTransGroup = new TransformGroup();
		topTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//4.创建整体变换组的3D变换(可以用来做一些初始化的工作,也可以不做此步骤)
		Transform3D topTransform3D = new Transform3D();
		topTransform3D.setTranslation(new Vector3d(0,0,0));//设置3D变换的位移分量
		//5.设置整体变换组的3D变换
		topTransGroup.setTransform(topTransform3D);
		
		//6.创建一个用于正方体的变换组
		TransformGroup cubeGroup = new TransformGroup();
		cubeGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//7.创建一个彩色正方体,并放入正方体变换组
		ColorCube cube = new ColorCube(0.3);
		cubeGroup.addChild(cube);
		//8.将正方体变换组放入整体变换组
		topTransGroup.addChild(cubeGroup);
		//9.创建一个3D变换，并设置变换的角度
		Transform3D cubeTransform3D = new Transform3D();//这个3D变换放在旋转线性插值器里默认是绕Y轴做旋转的,相当于一个指向Y轴的单位向量,下边的方法可以把这个向量进行旋转,使其绕别的方向转
		Matrix3d m1 = new Matrix3d();//x 方向
//		m1.rotX(Math.toRadians(90));// 绕X轴旋转90度,结果是绕Z轴转
//		m1.rotY(Math.toRadians(90));// 绕Y轴旋转90度,结果还是绕Y轴转
		m1.rotZ(Math.toRadians(45));// 绕Z轴旋转90度，结果是绕X轴转
		cubeTransform3D.setRotation(m1);
		
		//10.创建一个旋转线性插值器，并放入整体变换组
		Alpha alpha = new Alpha(-1,4000);
		RotationInterpolator rotator = new RotationInterpolator(alpha,cubeGroup,cubeTransform3D,0.0f, (float) Math.PI * 2.0f);//旋转
		rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
		topTransGroup.addChild(rotator);
		
		addSupport(topTransGroup);//增加辅助工具
		//11.将整体变换组放入根组
		rootGroup.addChild(topTransGroup);
		//12.将根组放入宇宙
		universe.addBranchGraph(rootGroup);
		//13.设置观察视角
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.getViewer().getView().setBackClipDistance(100);
		universe.getViewer().getView().setFrontClipDistance(0.1);
		universe.getViewer().getView().setFieldOfView(Math.PI*2/3);//视角
		
	}
	
	/**
	 * TODO 展示一个边旋转、边移动的正方体
	 */
	public static void test3() {
		//创建简单宇宙  包含一个视图平台
		SimpleUniverse universe = new SimpleUniverse();
		//根组
		BranchGroup rootGroup = new BranchGroup();
		//创建整体变换组,整体变换组中加入的3D变换,将对整个空间中的物体做变换
		TransformGroup topTransGroup = new TransformGroup();
		topTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		topTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//创建一个3D变换
		Transform3D topTransform3D = new Transform3D();
		topTransform3D.setTranslation(new Vector3d(0,0,0));//位移
		topTransGroup.setTransform(topTransform3D);
		
		//正方体放入旋转变换组2  变换组2放入位移变换组1  变换组2实现旋转变换   变换组1实现位移变换   
		/**
		 * 实现边旋转边移动的效果
		 * 1.创建一个正方体
		 * 2.创建一个3D变换,并设置变换为绕坐标轴旋转
		 * 3.创建一个旋转变换组
		 * 4.设置旋转变换组的3D变换
		 * 5.将正方体放入旋转变换组
		 * 6.创建一个Alhpa对象
		 * 7.创建一个旋转线性插值器
		 * 8.创建一个位移变换
		 * 9.创建一个位移变换组
		 * 10.设置位移变换组的3D变换
		 * 11.创建一个Alhpa对象
		 * 12.创建一个位移线性插值器
		 * 13.将位移变换组放入整体变换组
		 * 14.将旋转变换组放入位移变换组
		 * 15.在位移变换组添加旋转线性插值器
		 * 16.在整体变换组添加位移线性插值器
		 * 最后
		 * 在根组添加整体变换组
		 * 
		 * 根组-->整体变换组-->位移变换组-->旋转变换组-->正方体
		 *                             -->旋转线性插值器
		 *                 -->位移线性插值器     
		 *                 
		 *  也就是说,通过多层包含关系,实现多种运动的结合
		 *  就像月球绕地球转，地球和月球再一起绕太阳转
		 *  整体变换组里包含了位移变换组和位移线性插值器
		 *  位移变换组里包含了旋转变换组和旋转线性插值器
		 */
		ColorCube cube = new ColorCube(0.3);
		Transform3D cubeRotTransform3D = new Transform3D();
		Matrix3d m1 = new Matrix3d();
		m1.rotZ(Math.toRadians(90));// 绕Z轴旋转一个角度
		cubeRotTransform3D.setRotation(m1);
		TransformGroup cubeRotTransGroup = new TransformGroup();
		cubeRotTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		cubeRotTransGroup.setTransform(cubeRotTransform3D);
		cubeRotTransGroup.addChild(cube);
		Alpha alpha = new Alpha(-1,4000);
		RotationInterpolator rotator = new RotationInterpolator(alpha,cubeRotTransGroup,cubeRotTransform3D,0.0f, (float) Math.PI * 2.0f);//旋转
		rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
		
		Transform3D cubeWyTransform3D = new Transform3D();//8
		cubeWyTransform3D.setTranslation(new Vector3d(1,0,0));
		TransformGroup cubeWyTransGroup = new TransformGroup();
		cubeWyTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		cubeWyTransGroup.setTransform(cubeWyTransform3D);
		Alpha cubeWyAlpha = new Alpha(-1, 4000);
        PositionInterpolator cubePositionInterpolator = new PositionInterpolator(cubeWyAlpha, cubeWyTransGroup, cubeWyTransform3D, 0, 5);//12
        cubePositionInterpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
        topTransGroup.addChild(cubeWyTransGroup);
        cubeWyTransGroup.addChild(cubeRotTransGroup);
        cubeWyTransGroup.addChild(rotator);
        topTransGroup.addChild(cubePositionInterpolator);//16
		
        
        addSupport(topTransGroup);//增加辅助工具
        
        rootGroup.addChild(topTransGroup);
		universe.addBranchGraph(rootGroup);
		//设置观察视角
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.getViewer().getView().setBackClipDistance(100);
		universe.getViewer().getView().setFrontClipDistance(0.1);
		universe.getViewer().getView().setFieldOfView(Math.PI/4);
		
	}
	
	/**
	 * 添加鼠标辅助和坐标线工具
	 */
	public static void addSupport(TransformGroup transformGroup) {
		//辅助功能  添加鼠标操作和坐标线
		//set mouse's behavior
		 BoundingSphere bounds =
	                new BoundingSphere(new Point3d(0, 0, 0), 100.0);
		//set mouse's behavior
        MouseRotate mouseRotate = new MouseRotate();
        mouseRotate.setTransformGroup(transformGroup);
        mouseRotate.setSchedulingBounds(bounds);
        transformGroup.addChild(mouseRotate);
 
        MouseWheelZoom mouseZoom = new MouseWheelZoom();
        mouseZoom.setTransformGroup(transformGroup);
        mouseZoom.setSchedulingBounds(bounds);
        transformGroup.addChild(mouseZoom);
 
        MouseTranslate mouseTranslate = new MouseTranslate();
        mouseTranslate.setTransformGroup(transformGroup);
        mouseTranslate.setSchedulingBounds(bounds);
        transformGroup.addChild(mouseTranslate);
		
		//add coordinates  
        float[] linevertX = {
             -5.0f, 0f, 0f, 5.0f, 0f, 0f,};
        float[] linevertY = {
             0 , -5.0f, 0f, 0, 5.0f, 0f,};
        float[] linevertZ = {
             0 ,  0, -5.0f, 0, 0,5.0f,};
        float[] linecolorsX = {
            5.0f, 0f, 0f, 5.0f, 0.0f, 0f,};
        float[] linecolorsY = {
            0f, 5.0f, 0f, 0f, 5.0f, 0f,};
        float[] linecolorsZ = {
            0f, 0f, 5.0f, 0f, 0.0f, 5.0f,};
        LineArray lineX = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        lineX.setCoordinates(0, linevertX);
        lineX.setColors(0, linecolorsX);
 
        LineArray lineY = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        lineY.setCoordinates(0, linevertY);
        lineY.setColors(0, linecolorsY);
 
        LineArray lineZ = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        lineZ.setCoordinates(0, linevertZ);
        lineZ.setColors(0, linecolorsZ);
 
        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(3.0f);
 
        Appearance lineappearance = new Appearance();
        lineappearance.setLineAttributes(lineAttributes);
        
        TransformGroup lineGroupX = new TransformGroup();
        lineGroupX.setTransform(new Transform3D());
        Shape3D lineShape3DX = new Shape3D();
        lineShape3DX.setGeometry(lineX);
        lineShape3DX.setAppearance(lineappearance);
        lineGroupX.addChild(lineShape3DX);
        transformGroup.addChild(lineGroupX);
         
        TransformGroup lineGroupY= new TransformGroup();
        lineGroupY.setTransform(new Transform3D());
        Shape3D lineShape3DY = new Shape3D();
        lineShape3DY.setGeometry(lineY);
        lineShape3DY.setAppearance(lineappearance);
        lineGroupX.addChild(lineShape3DY);
        transformGroup.addChild(lineGroupY);
        
        TransformGroup lineGroupZ= new TransformGroup();
        lineGroupZ.setTransform(new Transform3D());
        Shape3D lineShape3DZ = new Shape3D();
        lineShape3DZ.setGeometry(lineZ);
        lineShape3DZ.setAppearance(lineappearance);
        lineGroupX.addChild(lineShape3DZ);
        transformGroup.addChild(lineGroupZ);
	}
	
	/**
	 * 打印3D变换的4乘4旋转矩阵
	 * @param axis
	 */
	private static void printMartrix(Transform3D axis) {
		double [] martrix = {1,0,0,0,  0,1,0,0, 0,0,1,0,  0,0,0,1};
		axis.get(martrix);
		System.out.println(martrix[0]+","+martrix[1]+","+martrix[2]+","+martrix[3]);
		System.out.println(martrix[4]+","+martrix[5]+","+martrix[6]+","+martrix[7]);
		System.out.println(martrix[8]+","+martrix[9]+","+martrix[10]+","+martrix[11]);
		System.out.println(martrix[12]+","+martrix[13]+","+martrix[14]+","+martrix[15]);
	}
	
	/**
	 * 研究怎么使用方块和外观   实现红警中的体素模型展示
	 */
	public static void test4() throws Exception{
//		File shpFile = new File("E:/sam.vxl");
		File shpFile = new File("E:/zep.vxl");
//		File shpFile = new File("E:/trucka.vxl");
		RandomAccessFile raf = new RandomAccessFile(shpFile,"r");
		//前16个字节是一个字符串Voxel Animation\0  无意义
		byte [] headStr = new byte [16];
		raf.read(headStr);
		//未知
		int paletteCount = read4Byte(raf);
		//区片数量
		int sectionCount = read4Byte(raf);
		//未知
		int sectionCount2 = read4Byte(raf);
		//重要变量  数据体的大小
		int bodySize = read4Byte(raf);
		//未知
		byte startPaletteRemap = raf.readByte();
		//未知
		byte endPaletteRemap = raf.readByte();
		//读取调色盘
		byte [] pal = new byte [256*3];
		raf.read(pal);
		//返回一个int数组大小 768/3=256
		int [] colorArray = new int [256];
		
		
		
		for(int i=0,k=0;i<pal.length;i+=3,k+=1) {
			int r = (pal[i] & 0xFF);
			int g = (pal[i+1] & 0xFF);
			int b = (pal[i+2] & 0xFF);
			colorArray[k] = (255<<24) | (r<<16) | (g<<8) | b;
			//alpha如果都是0  在argb图片里  图片背景会变成黑色!!
		}
		colorArray = PalFileReader.getColorArray("E:/unittem.pal", UnitColor.Red);
		
		//无用的16字节
		byte [] name = new byte [16];
		raf.read(name);
		int index = read4Byte(raf);
		int unknown1 = read4Byte(raf);
		int unknown2 = read4Byte(raf);
		
		byte [] body = new byte [bodySize];
		raf.read(body);
		
		//92位的文件尾
		int SpanStartOfs = read4Byte(raf);
		int	SpanEndOfs = read4Byte(raf);
		int SpanDataOfs = read4Byte(raf);
		int scale = read4Byte(raf);
		byte [] transform = new byte [12*4];
		raf.read(transform);
		byte [] bounds = new byte [6*4];
		raf.read(bounds);
		int sizex = raf.readUnsignedByte();//方块区域X大小
		int sizey = raf.readUnsignedByte();
		int sizez = raf.readUnsignedByte();
		int normalsType = raf.readUnsignedByte();
		
		System.out.println("长="+sizex);
		System.out.println("宽"+sizey);
		System.out.println("高"+sizez);
		
		System.out.println("SpanStartOfs="+SpanStartOfs);
		System.out.println("SpanEndOfs="+SpanEndOfs);
		System.out.println("SpanDataOfs="+SpanDataOfs);
//		System.out.println(body.length);
		
		int spanCount = sizex*sizey;
		System.out.println("spanCount="+spanCount);
		Raf bodyRaf = new Raf(body,SpanStartOfs);
		int [] starts = bodyRaf.readBytesToInts(spanCount);
		bodyRaf.index = SpanEndOfs;
		int [] ends = bodyRaf.readBytesToInts(spanCount);
		List<Voxel> ls = new ArrayList<>(sizex*sizey*sizez);
		
		for(int y=0;y<sizey;y++) {
			for(int x=0;x<sizex;x++) {
				int i = y*sizex+x;//像素点的下标
				int start = starts[i];
				if(start>0) {
					int offset = sizez*i;
					bodyRaf.index = SpanDataOfs+start;
					int [] slice = bodyRaf.readUBytes(ends[i] - start + 1);
					int sl = slice.length;
					for (int j = 0, z = 0; j < sl && z < sizez; ++j) {
						int v = slice[j++];
						z += v;
						offset += v;
						v = slice[j++];
						for (int w = 0; w < v; ++w, ++offset) {
							int z1 = offset%sizez;
							Voxel vox = new Voxel();
							vox.x = x;
							vox.y = y;
							vox.z = z1;
							vox.color = slice[j++];
							int normal = slice[j++];
							ls.add(vox);
						}
					}
				}
			}
		}
		
		System.out.println(ls.size());
		
		raf.close();
		
		
		
		
		
		//1.创建一个简单宇宙空间
		SimpleUniverse universe = new SimpleUniverse();
		//2.创建根组,并设置根组的变换权限
		BranchGroup rootGroup = new BranchGroup();
//		rootGroup.setCapability(BranchGroup.ALLOW_DETACH);
		//3.创建整体变换组,并设置整体变换组的变换权限,整体变换组可以实施整个空间整体的3D变换
		TransformGroup topTransGroup = new TransformGroup();
		topTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//4.创建整体变换组的3D变换(可以用来做一些初始化的工作,也可以不做此步骤)
		Transform3D topTransform3D = new Transform3D();
		topTransform3D.setTranslation(new Vector3d(0,0,0));//设置3D变换的位移分量
		//5.设置整体变换组的3D变换
		topTransGroup.setTransform(topTransform3D);
		
		TransformGroup viewTransGroup = new TransformGroup();
		viewTransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		topTransGroup.addChild(viewTransGroup);
		
		//6.创建一个用于正方体的变换组
		float basicUnit = 0.05f;
		for(int i=0;i<ls.size();i++) {
			Voxel vox = ls.get(i);
			TransformGroup cubeGroup = new TransformGroup();
			cubeGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D trans3D = new Transform3D();
			trans3D.setTranslation(new Vector3d(vox.x*basicUnit,vox.y*basicUnit,vox.z*basicUnit));//设置3D变换的位移分量
			cubeGroup.setTransform(trans3D);
			
			//7.创建一个彩色正方体,并放入正方体变换组
			// 创建正方体，并设置颜色为红色（你也可以使用其他颜色）
	        Color3f color = new Color3f(new Color( colorArray[vox.color])); // 设置颜色为红色
	        Appearance app = new Appearance(); // 创建外观对象
	        PolygonAttributes pa = new PolygonAttributes(); // 设置多边形属性，例如是否显示背面等
	        pa.setCullFace(PolygonAttributes.CULL_BACK); //
	        app.setPolygonAttributes(pa); // 应用多边形属性到外观对象
	        Material material = new Material(color, 
	        		color, 
	        		new Color3f(0.2f, 0.2f, 0.2f), //漫反射
	        		color, 100); // 创建材料对象，并设置颜色和光泽度等属性
	        app.setMaterial(material); // 将材料应用到外观对象
			Box cube=new Box( basicUnit, basicUnit , basicUnit , app);
			
			cubeGroup.addChild(cube);
			
			
			
			//8.将正方体变换组放入整体变换组
			viewTransGroup.addChild(cubeGroup);
		}
		
//		TransformGroup otherGroup = new TransformGroup();
//		otherGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		topTransGroup.addChild(otherGroup);
//		//由于空间中必须有一个线性插值对象,空间中的图像才能不断显示，所以必须放入一个，否则只显示一帧就不显示了
//		//9.创建一个位移线性插值器，并放入整体变换组
//		Alpha alpha = new Alpha(-1,Alpha.INCREASING_ENABLE,0,0,4000,0,0,0,0,0);
//		PositionInterpolator positionInterpolator = new PositionInterpolator(alpha,otherGroup);
//		positionInterpolator.setStartPosition(0);
//		positionInterpolator.setEndPosition(0);//不设置的话,PositionInterpolator默认在横坐标上从0挪到1,这样就不静态了
//		positionInterpolator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
//		topTransGroup.addChild(positionInterpolator);
		
		
		
//		9.创建一个3D变换，并设置变换的角度
		Transform3D cubeTransform3D = new Transform3D();//这个3D变换放在旋转线性插值器里默认是绕Y轴做旋转的,相当于一个指向Y轴的单位向量,下边的方法可以把这个向量进行旋转,使其绕别的方向转
		Matrix3d m1 = new Matrix3d();//x 方向
		m1.rotX(Math.toRadians(90));// 绕X轴旋转90度,结果是绕Z轴转
//		m1.rotY(Math.toRadians(90));// 绕Y轴旋转90度,结果还是绕Y轴转
//		m1.rotZ(Math.toRadians(90));// 绕Z轴旋转90度，结果是绕X轴转
		cubeTransform3D.setRotation(m1);
		cubeTransform3D.setTranslation(new Vector3d(sizex*basicUnit/2,sizey*basicUnit/2,sizez*basicUnit/2));
		
		//10.创建一个旋转线性插值器，并放入整体变换组
		Alpha alpha = new Alpha(-1,8000);
		RotationInterpolator rotator = new RotationInterpolator(alpha,viewTransGroup,cubeTransform3D,0.0f, (float) Math.PI * 2.0f);//旋转
		rotator.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
		topTransGroup.addChild(rotator);
		
		//添加辅助工具
		addSupport(topTransGroup);//增加辅助工具
		
		
		//环境光
        Color3f lightColor = new Color3f(Color.gray);
        AmbientLight ambientLight = new AmbientLight(lightColor);
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
        rootGroup.addChild(ambientLight);
        //直射光
//        DirectionalLight directionalLight = new DirectionalLight();
//        directionalLight.setColor(lightColor);
//        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
//        rootGroup.addChild(directionalLight);
        //还有一个点状光  光从一个点发出
		
		//10.将整体变换组放入根组
		rootGroup.addChild(topTransGroup);
		//11.将根组放入宇宙
		/*
		int width = 600;
		int height = 300;
		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		Dimension size = new Dimension(width, height); // 设置期望的尺寸
	    canvas.setSize(size);
		SimpleUniverse universe = new SimpleUniverse(canvas);
		*/
		
		/*
		Background background = new Background();
		background.setCapability(Background.ALLOW_IMAGE_WRITE);
		BufferedImage bi = ImageIO.read(new File("E:/bluesky.jpg"));
		TextureLoader texture = new TextureLoader(bi, "jpg");
		background.setImage(texture.getImage());
		background.setApplicationBounds(new BoundingSphere(new Point3d(0, 0, 0), 1000.0));
		rootGroup.addChild(background);
		*/
		
		universe.addBranchGraph(rootGroup);
		
		//12.设置观察视角
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.getViewer().getView().setBackClipDistance(100);
		universe.getViewer().getView().setFrontClipDistance(0.1);
		universe.getViewer().getView().setFieldOfView(Math.PI*5/6);//视角
		
		Transform3D view3D = new Transform3D();
		Matrix4d m4d = new Matrix4d(0.9489674593471487, 0.3153703104277875, 0.0015259098594625625, 0.0,
				-0.16821824961620782, 0.502073911289558, 0.8483067889027318, 0.0,
				0.2667646558228386,-0.8052722240975939, 0.5295028456020202, -6.599999999999998,
				0, 0, 0, 1.0);
		view3D.set(m4d);
		topTransGroup.setTransform(view3D);
		
		//不停打印角度信息,可以保存下来供参考
//		while(true) {
//			System.out.println("BackClipDistance="+universe.getViewer().getView().getBackClipDistance());
//			System.out.println("FieldOfView="+universe.getViewer().getView().getFieldOfView());
//			System.out.println("ScreenScale="+universe.getViewer().getView().getScreenScale());
//			Transform3D t3d = new Transform3D();
//			topTransGroup.getTransform(t3d);
//			printMartrix(t3d);
//			System.out.println("----------");
//			Thread.sleep(1000);
			
//		}
		
		/*
		Thread.sleep(5000);
		JFrame jf = new JFrame("3D学习");
		jf.setSize(width,height);
		jf.setResizable(true);
		jf.setAlwaysOnTop(false);//总是在最上层
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.setLocationRelativeTo(null);//屏幕居中
		jf.setVisible(true);
		
		JPanel mp = new JPanel();
		mp.setLocation(0, 0);
		mp.setVisible(true);
		mp.setLayout(null);
		mp.setMinimumSize(new Dimension(width,height));
		mp.setPreferredSize(new Dimension(width,height));
		canvas.setVisible(true);
		mp.add(canvas);
		mp.setOpaque(false);
		
		jf.add(mp);
		jf.pack();
		
		*/
		
		Thread.sleep(3000);

		// 创建一个线程来更新纹理
		/*
		new Thread(() -> {
			
			int i = 0;
		    while (true) {
		        try {
		        	
		        	if(i%2==0) {
		        		BufferedImage biii = ImageIO.read(new File("E:/bluesky.jpg"));
		        		TextureLoader textureee = new TextureLoader(biii, BufferedImage.TYPE_INT_RGB);
		        		background.setImage(textureee.getImage());
		        	}else {
		        		BufferedImage biii = ImageIO.read(new File("E:/uuuu.jpeg"));
		        		TextureLoader textureee = new TextureLoader(biii, BufferedImage.TYPE_INT_RGB);
		        		background.setImage(textureee.getImage());
		        	}
		            // 模拟每秒更新一次纹理
		            Thread.sleep(3000);
		            // 加载新的纹理图像，例如改变文件名以获取不同的图像
		    		i++;
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}).start();
		*/
	}
	
	

	
	/**
	 * 表示一个像素点的颜色
	 */
	public static class Voxel{
		public int x;
		public int y;
		public int z;
		public int color;
	}
	
	
	/**
	 * 实现读数组像读文件一样
	 */
	public static class Raf{
		
		public byte [] bytes;
		public int index = 0;
		
		public Raf(byte [] bytes) {
			this.bytes = bytes;
		}
		public Raf(byte [] bytes,int index) {
			this.bytes = bytes;
			this.index = index;
		}
		
		public int [] readBytesToInts(int n) {
			byte [] readBytes = Arrays.copyOfRange(bytes, index, index+n*4);
			index+=n*4;
			int [] readInts = new int [n];
			for(int i=0;i<readBytes.length;i+=4) {
				int result = 0;
				int result0 = readBytes[i] & 0xFF;
				int result1 = readBytes[i+1] & 0xFF;
				int result2 = readBytes[i+2] & 0xFF;
				int result3 = readBytes[i+3] & 0xFF;
				result = (result0 | result);
				result = (result1 <<8) | result;
				result = (result2 <<16) | result;
				result = (result3 <<24) | result;
				readInts[i/4] = result;
			}
			return readInts;
		}
		
		public int [] readUBytes(int n) {
			byte [] readBytes = Arrays.copyOfRange(bytes, index, index+n);
			int [] readInts = new int [n];
			for(int i=0;i<readBytes.length;i++) {
				readInts[i] = readBytes[i] & 0xFF;
			}
			return readInts;
		}
	}
	
	/**
	 * 读四个字节  返回一个int整数
	 */
	private static int read4Byte(RandomAccessFile raf) throws Exception{
		byte [] bytes = new byte [4];
		raf.read(bytes);
		int result = 0;
		int result0 = bytes[0] & 0xFF;
		int result1 = bytes[1] & 0xFF;
		int result2 = bytes[2] & 0xFF;
		int result3 = bytes[3] & 0xFF;
		result = (result0 | result);
		result = (result1 <<8) | result;
		result = (result2 <<16) | result;
		result = (result3 <<24) | result;
		return result;
	}
	
	public static void main(String[] args) throws Exception{
//		test();
//		test2();
//		test3();
		test4();
	}
}
