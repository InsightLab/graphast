package org.graphast.query.knn.model;

	public class QueueEntry implements Comparable<QueueEntry> {
		private long id;
		private int travelTime;

		public QueueEntry(long id, int tt) {
			this.id = id;
			this.travelTime = tt;
		}

		public int compareTo(QueueEntry another) {
			return new Integer(this.travelTime).compareTo(another.getTravelTime());
		}
		
		@Override
		public boolean equals(Object o){
			return this.id == ((QueueEntry)o).id;
		}
		
		public String toString(){
			return "( ID:"+id+" TT:"+travelTime+" )";
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getTravelTime() {
			return travelTime;
		}

		public void setTravelTime(int tt) {
			this.travelTime = tt;
		}
	}
