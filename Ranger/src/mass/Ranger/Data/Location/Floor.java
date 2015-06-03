package mass.Ranger.Data.Location;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.Point;

@SuppressWarnings("UnusedDeclaration")
public class Floor implements Parcelable {
    public static Creator<Floor> CREATOR = new Creator<Floor>() {
        @Override
        public Floor createFromParcel(Parcel source) {
            return new Floor(
                    (String) source.readValue(String.class.getClassLoader()),
                    (String) source.readValue(String.class.getClassLoader()),
                    (String) source.readValue(String.class.getClassLoader()),
                    (String) source.readValue(String.class.getClassLoader()));
        }

        @Override
        public Floor[] newArray(int size) {
            return new Floor[size];
        }
    };
    @SerializedName("MinLongitude")
    private double minLongitude;
    @SerializedName("MinLatitude")
    private double minLatitude;
    @SerializedName("MaxLongitude")
    private double maxLongitude;
    @SerializedName("MaxLatitude")
    private double maxLatitude;
    @SerializedName("FloorID")
    private String floorID;
    @SerializedName("Name")
    private String name;
    @SerializedName("Prefix")
    private String prefix;
    @SerializedName("LatestMetaMapID")
    private String latestMetaMapID;
    @SerializedName("LatestFloorModelID")
    private String latestFloorModelID;
    @SerializedName("TileUrl")
    private String tileUrl;
    @SerializedName("TileZip")
    private String tileZip;

    public Floor(String name, String latestModelID, String floorID, String prefix) {
        this.name = name;
        this.latestMetaMapID = latestModelID;
        this.floorID = floorID;
        this.prefix = prefix;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(getName());
        dest.writeValue(getLatestMetaMapID());
        dest.writeValue(getFloorID());
        dest.writeValue(getPrefix());
    }

    public Point getSize() {
        return new GeoCoordinate(maxLatitude, maxLongitude).relativeTo(new GeoCoordinate(minLatitude, minLongitude));
    }

    public IBoundary getBoundary() {
        Point size = getSize();
        return new Rectangle(0, 0, size.x, size.y);
    }

    public String getFloorID() {
        return floorID;
    }

    public String getLatestMetaMapID() {
        return latestMetaMapID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getTileZip() {
        return tileZip;
    }

    public String getLatestFloorModelID() {
        return latestFloorModelID;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }
}
