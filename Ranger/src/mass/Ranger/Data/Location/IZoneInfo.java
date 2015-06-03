package mass.Ranger.Data.Location;

import mass.Ranger.Algorithm.Localization.AccessPoint;
import mass.Ranger.Algorithm.Localization.RSSDistribution;

import java.util.Map;

public interface IZoneInfo {
    /**
     * Boundary of this zone, points are meter relative to the Origin
     */
    IBoundary getBoundary();

    /**
     * Get the floor of this zone
     */
    String getFloor();

    /**
     * Origin of the building (not this zone, but the whole building), all the other location are relative to this
     */
    GeoCoordinate getOrigin();

    /**
     * Top level zone
     */
    IZoneInfo getRoot();

    /**
     * Return the RSSDistributions for each Access Point
     */
    Map<String, RSSDistribution> getRSSDistributions();

    /**
     * Identify of this zone
     */
    String getZoneId();

    /**
     * <summary>
     * Return an access point model in this zone for the given bssid,
     * if not found and useRoot was true, try to search in the top level zone.
     * </summary>
     * <param name="bssid"></param>
     * <param name="useRoot"></param>
     * <returns></returns>
     */
    AccessPoint getAccessPoint(String bssid, boolean useRoot);

    AccessPoint getAccessPoint(String bssid);

    boolean addAccessPoint(AccessPoint accessPoint);

    void setFloor(String key);
}
