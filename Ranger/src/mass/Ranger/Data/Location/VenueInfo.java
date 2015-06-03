package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration")
public class VenueInfo {
    @SerializedName("Floors")
    private Floors floors;
    @SerializedName("FloorHeader")
    private String floorHeader;
    @SerializedName("LatestVenueModelID")
    private String lastVenueModelID;
    @SerializedName("OrionName")
    private String orionName;
    @SerializedName("MinLongitude")
    private double minLongitude;
    @SerializedName("MinLatitude")
    private double minLatitude;
    @SerializedName("MaxLongitude")
    private double maxLongitude;
    @SerializedName("MaxLatitude")
    private double maxLatitude;

    public String getLastVenueModelID() {
        return lastVenueModelID;
    }

    public void setLastVenueModelID(String lastVenueModelID) {
        this.lastVenueModelID = lastVenueModelID;
    }

    public String getFloorHeader() {
        return floorHeader;
    }

    public void setFloorHeader(String floorHeader) {
        this.floorHeader = floorHeader;
    }

    public Floors getFloors() {
        return floors;
    }

    public void setFloors(Floors floors) {
        this.floors = floors;
    }

    public String getOrionName() {
        return orionName;
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
