package mass.Ranger.Algorithm.Localization;

import com.google.gson.annotations.SerializedName;
import mass.Ranger.Util.ThreadSafeRandom;

@SuppressWarnings("UnusedDeclaration")
public class AccessPoint {
    public static final double minPRange = -60;
    public static final double maxPRange = 20;
    public static final double minGammaRange = 2;
    public static final double maxGammaRange = 6;
    @SerializedName("PInDBM")
    private double pInDBM;
    @SerializedName("Gamma")
    private double gamma;
    @SerializedName("Location")
    private Location location;
    private String BSSID;
    private String SSID;

    public AccessPoint(String bssid, double pInDBM, double gamma, double x, double y, String floor) {
        this.pInDBM = pInDBM;
        this.gamma = gamma;
        this.location = new Location(x, y, floor);
        this.BSSID = bssid;
    }

    public AccessPoint(String BSSID, double pInDBM, double gamma, Location location) {
        this.pInDBM = pInDBM;
        this.gamma = gamma;
        this.location = location;
        this.BSSID = BSSID;
    }

    /**
     * Generate a random Access Point floor inside the building
     *
     * @param bssid Basic service set identification
     * @param floor floor id
     */
    public static AccessPoint generateRandomAccessPoint(
            String bssid,
            String floor) {
        Point point = new Point(0, 0);
        double P = ThreadSafeRandom.nextDouble(minPRange, maxPRange);
        double gamma = ThreadSafeRandom.nextDouble(minGammaRange, maxGammaRange);
        return new AccessPoint(bssid, P, gamma, new Location(point, floor));
    }

    /**
     * computes the RSS at a certain location using the model
     */
    public double getReceivedSignalStrengthAt(Location location) {
        double distance = (Location.minus(location, this.location)).getLength();
        return pInDBM - 10 * gamma * Math.log10(distance);
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof AccessPoint && this.BSSID.equals(((AccessPoint) o).getBSSID());
    }

    @Override
    public int hashCode() {
        return this.BSSID.hashCode();
    }

    public double getPInDBM() {
        return pInDBM;
    }

    public void setPInDBM(double pInDBM) {
        this.pInDBM = pInDBM;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    @Override
    public String toString() {
        return "AccessPoint{" +
                "pInDBM=" + pInDBM +
                ", gamma=" + gamma +
                ", location=" + location +
                ", BSSID='" + BSSID + '\'' +
                ", SSID='" + SSID + '\'' +
                '}';
    }
}
