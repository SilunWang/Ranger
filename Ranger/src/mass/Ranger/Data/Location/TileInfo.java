package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.AccessPoint;
import mass.Ranger.Algorithm.Localization.RSSDistribution;

import java.util.HashMap;
import java.util.Map;

public class TileInfo implements IZoneInfo {
    private final static String TAG = TileInfo.class.getName();
    private final HashMap<String, RSSDistribution> RSSDistributions;
    private final HashMap<String, AccessPoint> accessPoints;
    @SerializedName("Boundary")
    private IBoundary boundary;
    @SerializedName("Floor")
    private String floor;
    @SerializedName("Root")
    private IZoneInfo root;
    @SerializedName("QuadKey")
    private String quadKey;
    @SerializedName("Origin")
    private GeoCoordinate origin;

    public TileInfo() {
        RSSDistributions = new HashMap<String, RSSDistribution>();
        accessPoints = new HashMap<String, AccessPoint>();
    }

    @Override
    public IBoundary getBoundary() {
        return this.boundary;
    }

    @Override
    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public GeoCoordinate getOrigin() {
        return origin;
    }

    @Override
    public IZoneInfo getRoot() {
        return root;
    }

    @Override
    public Map<String, RSSDistribution> getRSSDistributions() {
        return this.RSSDistributions;
    }

    @Override
    public String getZoneId() {
        return this.getQuadKey() + " " + this.floor;
    }

    @Override
    public AccessPoint getAccessPoint(String bssid, boolean useRoot) {
        if (this.accessPoints.containsKey(bssid)) {
            return this.accessPoints.get(bssid);
        }
        if (useRoot && this.root != null) {
            return this.root.getAccessPoint(bssid);
        }
        return null;
    }

    @Override
    public AccessPoint getAccessPoint(String bssid) {
        return this.getAccessPoint(bssid, true);
    }

    @Override
    public boolean addAccessPoint(AccessPoint accessPoint) {
        if (accessPoint == null) {
            return false;
        }
        String bssid = accessPoint.getBSSID();
        if (this.accessPoints.containsKey(bssid)) {
            //Logger.w(TAG, "AccessPoint already exists: " + bssid);
            return false;
        }
        this.accessPoints.put(bssid, accessPoint);
        return true;
    }

    public String getQuadKey() {
        return quadKey;
    }

    public void setQuadKey(String quadKey) {
        this.quadKey = quadKey;
    }

    public void setRoot(IZoneInfo root) {
        this.root = root;
    }

    public void setOrigin(GeoCoordinate origin) {
        this.origin = origin;
    }

    public void setBoundary(Rectangle boundary) {
        this.boundary = boundary;
    }
}
