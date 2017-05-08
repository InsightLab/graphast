package org.graphast.query.model;

import org.graphast.query.route.shortestpath.model.EntryImpl;

public interface Entry {

	public abstract boolean equals(EntryImpl o);

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getParent();

	public abstract void setParent(Long parent);

	public abstract String toString();

}