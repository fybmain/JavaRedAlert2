package redAlert.renderer.gl2;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.util.List;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GL2;

import redAlert.ShapeUnitFrame;
import redAlert.renderer.IShpSequence;
import redAlert.renderer.ShpSequenceInfo;

public class ShpSequence implements IShpSequence {
	GLAutoDrawable drawable;
	int frameCount, width, height;
	int centerOffX, centerOffY;
	int groundSizeX, groundSizeY;

	int[][] states;
	int[] textureIds;

	ShpSequence(GLAutoDrawable drawable, ShpSequenceInfo info) {
		this.drawable = drawable;
		List<ShapeUnitFrame> frames = info.frames;
		this.centerOffX = info.centerOffX;
		this.centerOffY = info.centerOffY;
		this.groundSizeX = info.groundSizeX;
		this.groundSizeY = info.groundSizeY;

		int n = frames.size();
		assert n>0;
		this.frameCount = n;

		this.width = frames.get(0).getImg().getWidth();
		this.height = frames.get(0).getImg().getHeight();
		assert (width>0) && (height>0);
		
		states = new int[info.states.length][];
		for(int i=0;i<states.length;i++) {
			assert info.states[i].length > 0;
			states[i] = info.states[i].clone();
		}
		
		IntBuffer[] intBuffers = new IntBuffer[n];
		for(int i=0;i<n;i++) {
			BufferedImage image = frames.get(i).getImg();
			assert (image.getWidth() == width) && (image.getHeight() == height);
	    	DataBufferInt dataBuffer = (DataBufferInt)(image.getRaster().getDataBuffer());
	    	int[] pixels = dataBuffer.getData();
			intBuffers[i] = IntBuffer.wrap(pixels);
		}

		textureIds = new int[n];
		drawable.invoke(true, d -> {
			GL2 gl = d.getGL().getGL2();
			gl.glGenTextures(n, textureIds, 0);
			for(int i=0;i<n;i++) {
	        	gl.glBindTexture(GL2.GL_TEXTURE_2D, textureIds[i]);
				gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 4, width, height, 0,
						GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, intBuffers[i]);
				gl.glFlush(); //刷新命令队列
			}
			return true;
		});
	}
	
	final int getTextureId(int stateId, long relativeFrameCount) {
		assert relativeFrameCount >= 0;
		int frameIndex = (int) (relativeFrameCount % states[stateId].length);
		int texIndex = states[stateId][frameIndex];
		assert (texIndex>=0) && (texIndex<frameCount);
		return textureIds[texIndex];
	}
	
	final float getTexCoordU(float virtualU, float virtualV) {
		return virtualU;
	}
	
	final float getTexCoordV(float virtualU, float virtualV) {
		return virtualV;
	}
	
    @Override
    protected void finalize() {
		drawable.invoke(false, d -> {
			GL2 gl = d.getGL().getGL2();
			gl.glDeleteTextures(frameCount, textureIds, 0);
			return true;
		});
    }
}
