package org.graphast.model.contraction;

import org.graphast.model.Node;

public interface CHNode extends Node {

	/**
	 * Blocksize length of the complement for a CHNode on IntBigArrayBigList.  
	 */
	public static final short NODE_BLOCKSIZE = 2;
	
	public int getPriority();
	
	public int getLevel();
	
	public void setPriority(int priority);
	
	public void setLevel(int level);
	
}
