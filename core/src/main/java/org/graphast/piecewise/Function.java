package org.graphast.piecewise;

public class Function {
	
	private long y;
	
	private long x;

	public Function(long y, long x) {
		super();
		this.y = y;
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public long getX() {
		return x;
	}

	public void setY(long y) {
		this.y = y;
	}

	public void setX(long x) {
		this.x = x;
	}
	
	
	public long getValue(long timestamp) {
		return 0;
	}
}
