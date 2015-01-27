package org.graphast.query.knn;

class UpperEntry implements Comparable<UpperEntry>{
	
	Integer unn;
	Integer  utdd;

	public UpperEntry(Integer unn, Integer utdd) {
		this.unn = unn;
		this.utdd = utdd;
	}
	
	public int compareTo(UpperEntry o) {
		return (-1)*utdd.compareTo(o.utdd);
	}


	public boolean equals(UpperEntry o){
		return unn.equals(o.unn);
	}
	
	public String toString(){
		return "( "+unn+" "+utdd+" )";
	}
}

