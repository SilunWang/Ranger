package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.AccessPoint;
import mass.Ranger.Algorithm.Localization.RSSDistribution;
import mass.Ranger.Util.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FloorInfo implements IZoneInfo {
    private final static String TAG = FloorInfo.class.getName();
    private HashMap<String, RSSDistribution> RSSDistributions;
    private HashMap<String, AccessPoint> accessPoints;
    @SerializedName("Boundary")
    private IBoundary boundary;
    @SerializedName("Floor")
    private String floor;
    @SerializedName("Root")
    private IZoneInfo root;
    @SerializedName("ZoneId")
    private String zoneId;
    @SerializedName("Origin")
    private GeoCoordinate origin;

    public FloorInfo(String floor, IBoundary boundary) {
        this.accessPoints = new HashMap<String, AccessPoint>();
        this.RSSDistributions = new HashMap<String, RSSDistribution>();
        this.boundary = boundary;
        this.floor = floor;
    }

    @Override
    public IBoundary getBoundary() {
        return this.boundary;
    }

    @Override
    public String getFloor() {
        return this.floor;
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
        return zoneId;
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
        bssid = Utils.convertToVirtualBSSID(bssid);
        if (this.accessPoints.containsKey(bssid)) {
            //Logger.w(TAG, "AccessPoint already exists: " + bssid);
            return false;
        }
        this.accessPoints.put(bssid, accessPoint);
        return true;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public void setRoot(IZoneInfo root) {
        this.root = root;
    }

    public void setOrigin(GeoCoordinate origin) {
        this.origin = origin;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Collection<AccessPoint> getAccessPoints() {
        return this.accessPoints.values();
    }
}
