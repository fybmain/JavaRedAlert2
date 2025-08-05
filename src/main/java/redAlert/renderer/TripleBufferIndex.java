package redAlert.renderer;

import java.util.concurrent.atomic.AtomicInteger;

public final class TripleBufferIndex {
	private final AtomicInteger state;
	
	private static final int encode(boolean updated, byte write, byte middle, byte read) {
		int upd = updated ? 1 : 0;
		return (((int)upd) << 24) | (((int)write) << 16) | (((int)middle) << 8) | ((int)read);
	}
	
	public TripleBufferIndex(byte write, byte middle, byte read) {
		this.state = new AtomicInteger(encode(false, write, middle, read));
	}
	
	public TripleBufferIndex() {
		this((byte)0, (byte)1, (byte)2);
	}
	
	public byte getReadBuffer() {
		int value = state.get();
		return (byte) (value);
	}
	
	public byte getWriteBuffer() {
		int value = state.get();
		return (byte) (value >> 16);
	}
	
	public byte writerSwap() {
		while(true) {
			int value = state.get();
			byte write = (byte) (value >> 16);
			byte middle = (byte) (value >> 8);
			byte read = (byte) (value);
			int newValue = encode(true, middle, write, read);
			if(state.compareAndSet(value, newValue)) {
				return write;
			}
		}
	}
	
	public boolean readerSwap() {
		while(true) {
			int value = state.get();
			if((value >> 24) == 0) {
				return false;
			}
			
			byte write = (byte) (value >> 16);
			byte middle = (byte) (value >> 8);
			byte read = (byte) (value);
			int newValue = encode(false, write, read, middle);
			if(state.compareAndSet(value, newValue)) { break; }
		}
		return true;
	}
}
