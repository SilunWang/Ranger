package mass.Ranger.Algorithm.Localization;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import mass.Ranger.Data.Location.IBoundary;
import mass.Ranger.Util.ThreadSafeRandom;

import java.util.List;

/**
 * Location class has 2D location and a floor information
 * User: v-hepang
 * Date: 8/29/13
 * Time: 5:38 PM
 */
public class Location extends Point implements Parcelable {
    public final static Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            double x = source.readDouble();
            double y = source.readDouble();
            String floor = (String) source.readValue(String.class.getClassLoader());
            return new Location(x, y, floor);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    @SerializedName("Floor")
    private String floor;

    public Location(double x, double y, String floor) {
        super(x, y);
        this.floor = floor;
    }

    public Location(Point location2D, String floor) {
        if (location2D == null) {
            this.x = Double.NaN;
            this.y = Double.NaN;
        } else {
            this.x = location2D.x;
            this.y = location2D.y;
        }
        this.floor = floor;
    }

    public Location(Location copySource) {
        this(copySource.x, copySource.y, copySource.floor);
    }

    public static Location generateRandomLocation(IBoundary boundary, String floor) {
        Point point = boundary.randomPosition();
        return new Location(point, floor);
    }

    //IBoundary limits, String floor
    public static Location generateRandomLocation(List<AccessPointAnnotatedRSSMeasurement> signals) {
        String floor = signals.get(0).getAccessPoint().getLocation().getFloor();
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (AccessPointAnnotatedRSSMeasurement signal : signals) {
            Location loc = signal.getAccessPoint().getLocation();
            minX = Math.min(loc.getX(), minX);
            maxX = Math.max(loc.getX(), maxX);
            minY = Math.min(loc.getY(), minY);
            maxY = Math.max(loc.getY(), maxY);
        }
        Point point = new Point(ThreadSafeRandom.nextDouble(minX, maxX), ThreadSafeRandom.nextDouble(minY, maxY));
        return new Location(point, floor);
    }

    /**
     * Generate a random location inside the building
     *
     * @param limits   NorthWest location(<code>(limits[0], limits[1])</code>) and SouthEast location(<code>(limits[2],
     *                 limits[3])</code>) of the building
     * @param noFloors Total floor number
     * @return
     */
    public static Location generateRandomLocation(double[] limits, int noFloors) {
        double x = ThreadSafeRandom.nextDouble() * (limits[2] - limits[0]) + limits[0];
        double y = ThreadSafeRandom.nextDouble() * (limits[3] - limits[1]) + limits[1];
        int floor = (int) Math.floor(ThreadSafeRandom.nextDouble() * ((double) noFloors));
        return new Location(x, y, Integer.toString(floor));
    }

    public static Vector minus(Location l1, Location l2) {
        return new Vector(l1.x - l2.x, l1.y - l2.y);
    }

    @Override
    public String toString() {
        return String.format("{{X: %f, Y: %f, Floor: %s}}", this.x, this.y, this.floor);
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeValue(floor);
    }
}
