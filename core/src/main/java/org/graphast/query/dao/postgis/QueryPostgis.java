package org.graphast.query.dao.postgis;

public interface QueryPostgis {

	String QUERY_ALL_DATE_TIME_WITH_DURATE = "SELECT date_time, duracao FROM tester;";
	
	String QUERY_POINT_ROAD = "SELECT ro.gid, ST_AsText(st_makeline(st_linemerge(ro.geom))) "
			+ "FROM TABLE_NAME ro GROUP BY ro.gid ORDER BY ro.gid;";
	
	String QUERY_POINT_TAXI = "SELECT t.id, ST_AsText(t.point_geo), t.gid FROM view_taxi_close_linestring t";

}
