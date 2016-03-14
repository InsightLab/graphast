package org.graphast.query.postgis;

public interface QueryPostgis {

	String QUERY_ALL_DATE_TIME_WITH_DURATE = "SELECT date_time, duracao FROM tester;";
	String QUERY_CLOSER_LINESTRING = null;
<<<<<<< HEAD
	String INSERT_PIECEWISE = "INSERT INTO public.piecewise(edgeId, timeDay, totalTime) VALUES (?, ?,?);";
=======
	String INSERT_PIECEWISE = "INSERT INTO public.tester(edgeId, timeDay, totalTime) VALUES (?, ?,?);";
>>>>>>> Insert values para piecewise table.

}
