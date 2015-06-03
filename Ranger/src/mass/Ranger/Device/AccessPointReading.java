package mass.Ranger.Device;

import com.google.gson.annotations.SerializedName;

public class AccessPointReading implements SensorReading {
    public String bssid;
    private String ssid;
    @SerializedName("rssi")
    public int RSSI;
    private int connected;
    private long timestamps;
    private int frequencyInMHz;
    private String capabilities;
    @SerializedName("security")
    private SecurityMode securityType;

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    protected void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int isConnected() {
        return connected;
    }

    protected void setConnected(int connected) {
        this.connected = connected;
    }

    public int getFrequencyInMHz() {
        return frequencyInMHz;
    }

    protected void setFrequencyInMHz(int frequencyInMHz) {
        this.frequencyInMHz = frequencyInMHz;
    }

    public String getCapabilities() {
        return capabilities;
    }

    protected void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
        if (capabilities.contains(SecurityMode.AD_HOC.toString())) {
            this.securityType = SecurityMode.AD_HOC;
        } else if (capabilities.contains(SecurityMode.EAP.toString())) {
            this.securityType = SecurityMode.EAP;
        } else if (capabilities.contains(SecurityMode.WEP.toString())) {
            this.securityType = SecurityMode.WEP;
        } else if (capabilities.contains(SecurityMode.WPA.toString())) {
            this.securityType = SecurityMode.WPA;
        } else if (capabilities.contains(SecurityMode.PSK.toString())) {
            this.securityType = SecurityMode.PSK;
        } else {
            this.securityType = SecurityMode.OPEN;
        }
    }

    public SecurityMode getSecurityType() {
        return securityType;
    }

    @Override
    public long getTimestamp() {
        return timestamps;
    }

    protected void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccessPointReading)) {
            return false;
        }

        AccessPointReading reading = (AccessPointReading) o;

        if (RSSI != reading.RSSI) {
            return false;
        }
        if (connected != reading.connected) {
            return false;
        }
        if (frequencyInMHz != reading.frequencyInMHz) {
            return false;
        }
        if (timestamps != reading.timestamps) {
            return false;
        }
        if (bssid != null ? !bssid.equals(reading.bssid) : reading.bssid != null) {
            return false;
        }
        if (capabilities != null ? !capabilities.equals(reading.capabilities) : reading.capabilities != null) {
            return false;
        }
        if (securityType != reading.securityType) {
            return false;
        }
        if (ssid != null ? !ssid.equals(reading.ssid) : reading.ssid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = bssid != null ? bssid.hashCode() : 0;
        result = 31 * result + (ssid != null ? ssid.hashCode() : 0);
        result = 31 * result + RSSI;
        result = 31 * result + connected;
        result = 31 * result + (int) (timestamps ^ (timestamps >>> 32));
        result = 31 * result + frequencyInMHz;
        result = 31 * result + (capabilities != null ? capabilities.hashCode() : 0);
        result = 31 * result + (securityType != null ? securityType.hashCode() : 0);
        return result;
    }
}
