package mass.Ranger.Data.Location;


import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.Point;
import mass.Ranger.Util.Utils;

public class GeoCoordinate {
    @SerializedName("Latitude")
    private double latitude;
    @SerializedName("Longitude")
    private double longitude;

    public GeoCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SuppressWarnings("UnusedDeclaration")
    public GeoCoordinate() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (latitude > 90.0 || latitude < -90.0) {
            throw new IllegalArgumentException(
                    "latitude" +
                            "The value of the parameter must be from -90.0 to 90.0.");
        }

        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (longitude > 180.0 || longitude < -180.0) {
            throw new IllegalArgumentException(
                    "Latitude" +
                            "The value of the parameter must be from -180.0 to 180.0.");
        }

        this.longitude = longitude;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Point relativeTo(GeoCoordinate origin) {
        double x = /* X */Utils.degreeToRadian(this.longitude - origin.longitude)
                * Math.cos(Utils.degreeToRadian(this.latitude))
                * Utils.EarthRadiusInMeter;

        double y =   /* Y */Utils.degreeToRadian(this.latitude - origin.latitude)
                * Utils.EarthRadiusInMeter;

        return new Point(x, y);
    }

    public GeoCoordinate shift(Point relativePoint) {
        double latitude = this.latitude + Utils.radianToDegree(
                relativePoint.getY()
                        / Utils.EarthRadiusInMeter);
        double longitude = this.longitude + Utils.radianToDegree(
                relativePoint.getX()
                        / (Utils.EarthRadiusInMeter * Math.cos(Utils.degreeToRadian(latitude))));
        return new GeoCoordinate(latitude, longitude);
    }
}
