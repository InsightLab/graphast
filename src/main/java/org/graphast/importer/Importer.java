package org.graphast.importer;

import org.graphast.model.Graph;

public interface Importer {

	public abstract Graph execute();

	public abstract int getDirection(long flags);

	public abstract String getOsmFile();

	public abstract String getGraphHopperDir();

	public abstract String getGraphastDir();

}