package mass.Ranger.Algorithm.Localization;

import mass.Ranger.Util.Utils;

public class Vector {
    private double x;

    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {

    }

    /**
     * Length Property - the length of this Vector
     *
     * @return
     */
    public double getLength() {
        return Math.sqrt((this.x * this.x) + (this.y * this.y));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    /**
     * CrossProduct - Returns the cross product: vector1.X*vector2.Y - vector1.Y*vector2.X
     *
     * @param vector1 The first Vector
     * @param vector2 The second Vector
     * @return the cross product: vector1.X*vector2.Y - vector1.Y*vector2.X
     */
    public static double crossProduct(Vector vector1, Vector vector2) {
        return vector1.x * vector2.y - vector1.y * vector2.x;
    }

    public Point toPoint() {
        return new Point(this.x, this.y);
    }

    /**
     * Compares two Vector instances for object equality.  In this equality
     * /// Double.NaN is equal to itself, unlike in numeric equality.
     * /// Note that double values can acquire error when operated upon, such that
     * /// an exact comparison between two values which
     * /// are logically equal may fail.
     *
     * @param vector1 The first Vector to compare
     * @param vector2 The second Vector to compare
     * @return true if the two Vector instances are exactly equal, false otherwise
     */
    public static boolean equals(Vector vector1, Vector vector2) {
        return Utils.equals(vector1.x, vector2.x) &&
                Utils.equals(vector1.y, vector2.y);
    }

    public static double multiply(Vector vector1, Vector vector2) {
        return vector1.x * vector2.x + vector1.y * vector2.y;
    }

    public static Vector multiply(Vector vector, double factor) {
        return new Vector(vector.x * factor, vector.y * factor);
    }

    public static Vector multiply(double factor, Vector vector) {
        return new Vector(vector.x * factor, vector.y * factor);
    }

    public Vector normalize() {
        double length = this.getLength();
        if (length == 0) {
            return new Vector();
        }

        return new Vector(this.x / length, this.y / length);
    }
}
