package mass.Ranger.Algorithm.Localization;

public class AccessPointAnnotatedRSSMeasurement extends ReceivedSignalStrengthMeasurement {
    /**
     * A GUID request ID that can be used to compute errors later
     */
    private String requestID;
    private AccessPoint AccessPoint;

    public AccessPointAnnotatedRSSMeasurement(String requestId, AccessPoint accessPoint, int signalStrength) {
        this.AccessPoint = accessPoint;
        super.receivedSignalStrength = signalStrength;
        this.requestID = requestId;
        this.setBSSID(this.AccessPoint.getBSSID());
    }

    public String getRequestID() {
        return requestID;
    }

    public AccessPoint getAccessPoint() {
        return AccessPoint;
    }
}
