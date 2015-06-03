package mass.Ranger.Algorithm.Localization;

import java.text.MessageFormat;

public class ReceivedSignalStrengthMeasurement {
    /**
     * http://en.wikipedia.org/wiki/Rssi
     */
    protected int receivedSignalStrength;
    /**
     * http://en.wikipedia.org/wiki/BSSID#Basic_service_set_identification_.28BSSID.29
     */
    private String BSSID;
    /**
     * The name of the AP
     */
    private String SSID;

    public ReceivedSignalStrengthMeasurement(int receivedSignalStrength, String BSSID) {
        this.receivedSignalStrength = receivedSignalStrength;
        this.BSSID = BSSID;
    }

    public ReceivedSignalStrengthMeasurement() {
    }

    public int getReceivedSignalStrength() {
        return receivedSignalStrength;
    }

    public void setReceivedSignalStrength(int receivedSignalStrength) {
        this.receivedSignalStrength = receivedSignalStrength;
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
        return MessageFormat.format("{0}:{1}", BSSID, receivedSignalStrength);
    }
}
