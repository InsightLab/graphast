package org.graphast.importer;

import org.graphast.config.Configuration;
import org.graphast.importer.Importer;
import org.graphast.importer.OSMImporterImpl;
import org.graphast.model.Graph;
import org.graphast.model.GraphImpl;

public class Principal {

	public static void main(String[] args) {
		System.out.println(Math.round(10.4));
		System.out.println(Math.round(10.5));
		//Importer importer = new OSMImporterImpl(Configuration.USER_HOME + "/graphhopper/osm/berlin-latest.osm.pbf", Configuration.USER_HOME + "/graphhopper/berlin", Configuration.USER_HOME + "/graphast/berlin");
		Importer importer = new OSMImporterImpl(Configuration.USER_HOME + "/graphhopper/osm/monaco-150112.osm.pbf", Configuration.USER_HOME + "/graphast/monaco");
		Graph g = importer.execute();
 	}

}

