package redAlert.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import redAlert.ShapeUnitFrame;

/**
 * 从网页工具中读取到的载具各个角度画面信息
 */
public class VxlFileReader {

	/**
	 * 规定导出的每个角度图片都是128*128大小
	 */
	public final static int imageLength = 128;
	
	/**
	 * SHP文件前缀和路径名称缓存
	 */
	private static HashMap<String,String> pngPrefixToPathMap = new HashMap<String,String>();
	/**
	 * 加载ClassPath目录下的所有SHP文件名和文件目录
	 */
	static {
		String classPath = VxlFileReader.class.getClassLoader().getResource(".").getPath();//当前目录  也就是与classes文件夹所在目录  变量以"/"或"\"结尾
		
		try {
			classPath= URLDecoder.decode(classPath, "UTF-8");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		File file = new File(classPath+"png");
		
		File [] files = file.listFiles();
		for(int i=0;i<files.length;i++) {
			String name = files[i].getName();
			String shpPrefix = name.substring(0, name.indexOf("."));
			String path = files[i].getAbsolutePath();
			pngPrefixToPathMap.put(shpPrefix, path);
		}
	}
	
	/**
	 * 转换png图片到ShapeUnitFrame列表
	 * x 每行有多少个贴图
	 * y 每列有多少个贴图
	 */
	public static List<ShapeUnitFrame> convertPngFileToBuildingFrames(String tankPrefix,int x,int y) {
		
		List<ShapeUnitFrame> result = null;
		try {
			result = new ArrayList<>();
			String vxlFilePath = pngPrefixToPathMap.get(tankPrefix);
			File vxlFile = new File(vxlFilePath);
			BufferedImage img = ImageIO.read(vxlFile);
			
			for(int i=0;i<y;i++) {
				for(int j=0;j<x;j++) {
					BufferedImage targetImage = new BufferedImage(imageLength,imageLength,BufferedImage.TYPE_INT_ARGB);
					
					int startx = j*imageLength;
					int starty = i*imageLength;
					
					int minX = 99999;
					int minY = 99999;
					
					for(int m=0;m<imageLength;m++) {//行 y
						for(int n=0;n<imageLength;n++) {//列 x
							int rgb = img.getRGB(startx+n, starty+m);
							if(rgb!=0) {
								if(n<=minX) {
									minX = n;
								}
								if(m<=minY) {
									minY = m;
								}
							}
							targetImage.setRGB(n, m, rgb);
						}
					}
					
					ShapeUnitFrame shapeUnitFrame = new ShapeUnitFrame();
					shapeUnitFrame.setImg(targetImage);
					shapeUnitFrame.setMinX(minX);
					shapeUnitFrame.setMinY(minY);
					result.add(shapeUnitFrame);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
//	public static void main(String[] args) throws Exception{
//		List<ShapeUnitFrame> ls = convertPngFileToBuildingFrames("E:/tanks.png",10,8);
//		System.out.println(ls.size());
//		System.out.println(ls.get(0).getMinX());
//	}
}
