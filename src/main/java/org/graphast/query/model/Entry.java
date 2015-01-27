package org.graphast.query.model;

import org.graphast.query.route.shortestpath.model.EntryImpl;

public interface Entry {

	public abstract boolean equals(EntryImpl o);

	public abstract long getId();

	public abstract void setId(long id);

	public abstract long getParent();

	public abstract void setParent(long parent);

	public abstract String toString();

}