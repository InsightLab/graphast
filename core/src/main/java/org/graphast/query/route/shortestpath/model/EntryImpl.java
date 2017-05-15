package org.graphast.query.route.shortestpath.model;

import org.graphast.query.model.Entry;

public class EntryImpl implements Entry{

	private Long id;
	private Long parent;

	public EntryImpl(long id, long parent) {
		this.id = id;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#equals(org.graphast.query.route.shortestpath.EntryImpl)
	 */
	@Override
	public boolean equals(EntryImpl o) {
		return this.id == o.id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#setId(long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#getParent()
	 */
	@Override
	public Long getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.graphast.query.route.shortestpath.Entry#setParent(long)
	 */
	@Override
	public void setParent(Long parent) {
		this.parent = parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (parent ^ (parent >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EntryImpl other = (EntryImpl) obj;
		if (id != other.id)
			return false;
//		if (parent != other.parent)
//			return false;
		return true;
	}
	
}
