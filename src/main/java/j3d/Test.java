package j3d;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 读取Vxl文件
 */
public class Test {

	/**
	 * 读取
	 */
	public static void test1() throws Exception{
		File shpFile = new File("E:/trucka.vxl");
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
		
		
		//无用的16字节
		byte [] name = new byte [16];
		raf.read(name);
		int index = read4Byte(raf);
		int unknown1 = read4Byte(raf);
		int unknown2 = read4Byte(raf);
		
		byte [] body = new byte [bodySize];
		raf.read(body);
		
		//92字节的文件尾
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
		
		int printnum = 0;
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
							if(printnum<100) {
								
									printnum++;
									System.out.println("se"+vox.color);
									System.out.println(normal);
								
							}
							ls.add(vox);
						}
					}
				}
			}
		}
		
		System.out.println("体素个数=="+ls.size());
		
		raf.close();
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
		
		test1();
		
	}
}
