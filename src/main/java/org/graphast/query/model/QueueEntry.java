package org.graphast.query.model;

	public class QueueEntry implements Comparable<QueueEntry> {
		private long id;
		private short travelTime;

<<<<<<< HEAD
		public QueueEntry(long id, int travelTime) {
=======
		public QueueEntry(long id, short travelTime) {
>>>>>>> modification on Bounds of KNN. need of cost modification
			this.id = id;
			this.travelTime = travelTime;
		}

		public int compareTo(QueueEntry another) {
			return new Short(this.travelTime).compareTo(another.getTravelTime());
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

		public short getTravelTime() {
			return travelTime;
		}

<<<<<<< HEAD
		public void setTravelTime(int travelTime) {
=======
		public void setTravelTime(short travelTime) {
>>>>>>> modification on Bounds of KNN. need of cost modification
			this.travelTime = travelTime;
		}
	}
