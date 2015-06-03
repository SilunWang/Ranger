package mass.Ranger.Algorithm.Localization;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Point implements Parcelable {
    public static final Point          ZERO    = new Point(0, 0);
    public final static Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel source) {
            return new Point(source.readDouble(), source.readDouble());
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
    @SerializedName("X")
    public double x;
    @SerializedName("Y")
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public static Vector minus(Point end, Point start) {
        return new Vector(end.x - start.x, end.y - start.y);
    }

    public static Point plus(Point p, Vector v) {
        return new Point(p.x + v.getX(), p.y + v.getY());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void copyFrom(Point p) {
        x = p.x;
        y = p.y;
    }

    public Point deepClone() {
        return new Point(x, y);
    }

    /**
     * Transform a point based on the given transform matrix
     * See: http://en.wikipedia.org/wiki/Transformation_matrix
     *
     * @param matrix a 3X3 transformation matrix
     * @return a new point transformed by the transformation matrix
     */
    public Point transform(double[] matrix) {
        double y = this.x * matrix[0] + this.y * matrix[1] + matrix[2];
        double x = this.x * matrix[3] + this.y * matrix[4] + matrix[5];
        return new Point(x, y);
    }

    public Point shift(Point point) {
        this.x += point.x;
        this.y += point.y;
        return this;
    }

    /**
     * shift the current Point
     */
    public Point shift(double dx, double dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    public Point scale(double ratio) {
        return new Point(x * ratio, y * ratio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
    }

    @Override
    public String toString() {
        return String.format("Point{particleX=%s, particleY=%s}", x, y);
    }
}
