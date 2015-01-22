package org.graphast.model;

public interface Node {

	public abstract void validate();

	public abstract int getCategory();

	public abstract long getExternalId();

	public abstract double getLatitude();

	public abstract double getLongitude();

	public abstract Long getId();

	public abstract String getLabel();

	public abstract String toString();

}