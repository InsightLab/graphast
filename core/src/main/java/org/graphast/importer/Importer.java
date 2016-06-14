package org.graphast.importer;

import org.graphast.model.Graph;
import org.graphast.model.contraction.CHGraph;

public interface Importer {

	public abstract Graph execute();
	public CHGraph executeCH();

}