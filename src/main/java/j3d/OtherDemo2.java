package j3d;
 
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
 
/**
 * 可以运行的
 * 有鼠标拖动  缩放  中键移动位置等功能  十分全面
 */
public class OtherDemo2 extends Applet {
 
    public OtherDemo2() {
        this.setLayout(new BorderLayout());
        GraphicsConfiguration configuration =
                SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        this.add(canvas3D);
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        universe.addBranchGraph(getBranchGroup());
    }
 
    BranchGroup getBranchGroup() {
        BranchGroup branchGroup = new BranchGroup();
        BoundingSphere bounds =
                new BoundingSphere(new Point3d(0, 2.0, 7.0), 1000.0);
        //set coordinates
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(0, 0, -7));
        TransformGroup transformGroup = new TransformGroup();
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setTransform(transform3D);
 
        //set back color
        Color3f backgroudColor3f = new Color3f(Color.GRAY);
        Background background = new Background(backgroudColor3f);
        background.setApplicationBounds(bounds);
        branchGroup.addChild(background);
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
 
        //throw in some light so we aren't stumbling
        //around in the dark
        Color3f lightColor = new Color3f(Color.green);
        AmbientLight ambientLight = new AmbientLight(lightColor);
        ambientLight.setInfluencingBounds(bounds);
        branchGroup.addChild(ambientLight);
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setColor(lightColor);
        directionalLight.setInfluencingBounds(bounds);
        branchGroup.addChild(directionalLight);
 
        //add new cube
        TransformGroup cubeGroup = new TransformGroup();
        cubeGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D cubeTransform3D = new Transform3D();
        cubeTransform3D.setTranslation(new Vector3d(-2, 2, 2));
        cubeGroup.setTransform(cubeTransform3D);
        ColorCube cube = new ColorCube(0.5f);
        Alpha cubeapAlpha=new  Alpha(-1, Alpha.INCREASING_ENABLE |
                Alpha.DECREASING_ENABLE,
                0, 0,
                5000, 5000, 200,
                5000, 5000, 200);
        PositionInterpolator cubePositionInterpolator=
                new  PositionInterpolator(cubeapAlpha, cubeGroup,cubeTransform3D , 0 ,(float) Math.PI );
        cubePositionInterpolator.setSchedulingBounds(bounds);
 
        cubeGroup.addChild(cubePositionInterpolator);
        cubeGroup.addChild(cube);
 
        transformGroup.addChild(cubeGroup);
 
 
        //add box
        TransformGroup boxGroup=new TransformGroup();
        boxGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D boxTransform3D=new Transform3D();
        boxTransform3D.setTranslation(new Vector3d(-2, -2, 2));
        boxGroup.setTransform(boxTransform3D);
        Box box=new Box( 0.5f, 0.5f , 0.5f , null);
        boxGroup.addChild(box);
 
        Alpha boxAlpha=new  Alpha(-1, Alpha.INCREASING_ENABLE |
                Alpha.DECREASING_ENABLE,
                0, 0,
                5000, 5000, 200,
                5000, 5000, 200);
        RotationInterpolator boxInterpolator=
                new RotationInterpolator(boxAlpha, boxGroup, boxTransform3D, 0 , (float)Math.PI );
        boxInterpolator.setSchedulingBounds(bounds);
        boxGroup.addChild( boxInterpolator);
        
        transformGroup.addChild(boxGroup);
 
        //add sphere
        TransformGroup sphereGroup =new TransformGroup();
        Transform3D sphereTransform3D=new  Transform3D();
        sphereTransform3D.setTranslation(new  Vector3d(2,-2,2));
        sphereGroup.setTransform(sphereTransform3D);
        Sphere sphere=new  Sphere(0.5f, -1, 80);
        sphereGroup.addChild(sphere);
        transformGroup.addChild(sphereGroup);
 
        //add linesphere
        TransformGroup linesphereGroup=new TransformGroup();
        Transform3D linesphereTransform3D=new Transform3D();
        linesphereTransform3D.setTranslation( new Vector3d(-2, 2, -2));
        linesphereGroup.setTransform(linesphereTransform3D);
        
        Sphere lineSphere=new Sphere(0.8f);
 
        Appearance linesphereAppearance=new Appearance();
        Material linesphereMaterial=new Material();
        linesphereMaterial.setDiffuseColor(new  Color3f(Color.PINK));
        linesphereAppearance.setMaterial(linesphereMaterial);
 
 
        LineAttributes linesphereLineAttributes=new  LineAttributes();
        linesphereLineAttributes.setLineWidth(0.001f);
        linesphereAppearance.setLineAttributes(linesphereLineAttributes);
 
 
        PolygonAttributes linespherepPolygonAttributes=new PolygonAttributes();
        linespherepPolygonAttributes.setPolygonMode(PolygonAttributes.CULL_BACK);
        linesphereAppearance.setPolygonAttributes(linespherepPolygonAttributes);
 
        lineSphere.setAppearance(linesphereAppearance);
 
        linesphereGroup.addChild(lineSphere);
        transformGroup.addChild(linesphereGroup);
 
        //add cone
        TransformGroup coneGroup=new  TransformGroup();
        Transform3D coneTransform3D=new Transform3D();
        coneTransform3D.setTranslation(new Vector3d(2,2,-2));
        coneGroup.setTransform(coneTransform3D);
        Cone cone=new  Cone(0.5f,0.5f);
        coneGroup.addChild(cone);
        transformGroup.addChild(coneGroup);
 
 
 
        // add  linecone
        TransformGroup lineconeGroup=new TransformGroup();
        Transform3D  lineconeTransform3D=new Transform3D();
        lineconeTransform3D.setTranslation(new Vector3d(-2,-2,-2));
        lineconeGroup.setTransform(lineconeTransform3D);
        Cone lineCone=new Cone(0.5f, 1.5f);
 
        Appearance lineconeAppearance=new Appearance();
 
        PolygonAttributes lineconepolygonAttributes=new  PolygonAttributes();
        lineconepolygonAttributes.setPolygonMode(PolygonAttributes.CULL_BACK);
        lineconeAppearance.setPolygonAttributes(lineconepolygonAttributes);
 
        lineCone.setAppearance(lineconeAppearance);
 
        lineconeGroup.addChild(lineCone);
        transformGroup.addChild(lineconeGroup);
 
 
 
        //add cylinder
        TransformGroup cylinderGroup=new TransformGroup();
        Transform3D cylinderTransform3D=new Transform3D();
        cylinderTransform3D.setTranslation(new Vector3d(2,-2,-2));
        cylinderGroup.setTransform(cylinderTransform3D);
        Cylinder cylinder=new Cylinder(0.5f ,1.0f);
        cylinderGroup.addChild(cylinder);
        transformGroup.addChild(cylinderGroup);
 
 
 
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
 
        branchGroup.addChild(transformGroup);
 
        return branchGroup;
    }
 
    public static void main(String[] argsSes) throws Exception{
    	OtherDemo2 od = new OtherDemo2();
    	MainFrame mf = new MainFrame(od, 256, 256);
    	Thread.sleep(5000);
    	BufferedImage iamge = new BufferedImage(mf.getWidth(),mf.getHeight(),BufferedImage.TYPE_INT_RGB);
    	mf.paint(od.getGraphics());
		ImageIO.write(iamge, "png", new File("E:/outxxx1.png"));
    }
}