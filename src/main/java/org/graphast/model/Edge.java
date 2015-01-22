package org.graphast.model;

import it.unimi.dsi.fastutil.BigArrays;

import java.util.List;

import org.graphast.geometry.Point;

public interface Edge {

	public static final short EDGE_BLOCKSIZE = 17;

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#validate()
	 */
	public abstract void validate();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getDistance()
	 */
	public abstract int getDistance();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getId()
	 */
	public abstract Long getId();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getFromNode()
	 */
	public abstract long getFromNode();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getToNode()
	 */
	public abstract long getToNode();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getFromNodeNextEdge()
	 */
	public abstract long getFromNodeNextEdge();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getToNodeNextEdge()
	 */
	public abstract long getToNodeNextEdge();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getToNodeSegment()
	 */
	public abstract int getToNodeSegment();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getCostsSegment()
	 */
	public abstract int getCostsSegment();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getGeometrySegment()
	 */
	public abstract int getGeometrySegment();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getCostsIndex()
	 */
	public abstract long getCostsIndex();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getGeometryIndex()
	 */
	public abstract long getGeometryIndex();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getCosts()
	 */
	public abstract short[] getCosts();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getGeometry()
	 */
	public abstract List<Point> getGeometry();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#getLabel()
	 */
	public abstract String getLabel();

	/* (non-Javadoc)
	 * @see org.graphast.model.Edge#toString()
	 */
	public abstract String toString();
	
	public int getLabelIndexSegment();

}