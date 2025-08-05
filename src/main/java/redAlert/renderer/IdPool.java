package redAlert.renderer;

import java.util.LinkedList;
import java.util.Queue;

public class IdPool {
	private final int maxAllowedId;
	private final Queue<Integer> freeIdList = new LinkedList<>();
	private int nextId = 1, counter = 0;
	
	public IdPool(int maxAllowedId) {
		this.maxAllowedId = maxAllowedId;
	}
	
	public int allocate() {
		int id;
		synchronized(this) {
			if(freeIdList.isEmpty()) {
				if(nextId > maxAllowedId) {
					throw new IllegalStateException("ID Pool Exhausted");
				}
				id = nextId++;
			}else {
				id = freeIdList.remove();
			}
			counter++;
		}
		return id;
	}
	
	public void free(int id) {
		synchronized(this) {
			freeIdList.add(id);
			counter--;
		}
	}
	
	public int idRange() {
		synchronized(this) {
			return nextId;
		}
	}
	
	public int countIds() {
		synchronized(this) {
			return counter;
		}
	}
}
