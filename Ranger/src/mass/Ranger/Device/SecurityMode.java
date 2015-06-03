package mass.Ranger.Device;

public enum SecurityMode {
    OPEN("Open"), WEP("WEP"), PSK("PSK"), EAP("EAP"), WPA("WPA"), AD_HOC("IBSS"),;
    private String value;

    private SecurityMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
