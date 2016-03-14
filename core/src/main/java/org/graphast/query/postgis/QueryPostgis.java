package org.graphast.query.postgis;

public interface QueryPostgis {

	String QUERY_ALL_DATE_TIME_WITH_DURATE = "SELECT date_time, duracao FROM tester;";
	String QUERY_CLOSER_LINESTRING = null;
	String INSERT_PIECEWISE = "INSERT INTO public.tester(edgeId, timeDay, totalTime) VALUES (?, ?,?);";

}
