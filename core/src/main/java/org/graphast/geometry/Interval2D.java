package org.graphast.geometry;

public class Interval2D<Key extends Comparable<Key>> { 
    public final Interval<Key> intervalX;   // x-interval
    public final Interval<Key> intervalY;   // y-interval
   
    public Interval2D(Interval<Key> intervalX, Interval<Key> intervalY) {
        this.intervalX = intervalX;
        this.intervalY = intervalY;
    }

    // does this 2D interval a intersect b?
    public boolean intersects(Interval2D<Key> b) {
        if (intervalX.intersects(b.intervalX)) return true;
        if (intervalY.intersects(b.intervalY)) return true;
        return false;
    }

    // does this 2D interval contain (x, y)?
    public boolean contains(Key x, Key y) {
        return intervalX.contains(x) && intervalY.contains(y);
    }

    // return string representation
    public String toString() {
        return intervalX + " x " + intervalY;
    }



    // test client
    public static void main(String[] args) {
        Interval<Double> intervalX = new Interval<Double>(0.0, 1.0);
        Interval<Double> intervalY = new Interval<Double>(5.0, 6.0);
        Interval2D<Double> box1 = new Interval2D<Double>(intervalX, intervalY);
        intervalX = new Interval<Double>(-5.0, 5.0);
        intervalY = new Interval<Double>(3.0, 7.0);
        Interval2D<Double> box2 = new Interval2D<Double>(intervalX, intervalY);
        System.out.println("box1 = " + box1);
        System.out.println("box2 = " + box2);
        System.out.println(box1.contains(0.5, 5.5));
        System.out.println(!box1.contains(1.5, 5.5));
        System.out.println(box1.intersects(box2));
        System.out.println(box2.intersects(box1));
    }
}
