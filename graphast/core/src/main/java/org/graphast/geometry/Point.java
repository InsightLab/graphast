package org.graphast.geometry;


public class Point {

	private double latitude;
	private double longitude;

	public Point(){}
	
	public Point(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		String s = "";
		s += "lat:" + this.latitude + " long:" + this.longitude;
		return s;
	}

	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final Point newPoint = (Point) obj;
	    
	    if ((this.getLatitude() == newPoint.getLatitude()) &&(this.getLongitude()==newPoint.getLongitude())) {
	    	return true;
	    }
	    
	    return false;
		
	}

}
