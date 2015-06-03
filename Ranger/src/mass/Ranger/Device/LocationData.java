package mass.Ranger.Device;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class LocationData {
    private final static String TAG = LocationData.class.getName();
    @SerializedName("LocationStamps")
    private List<WiFiStamps> wiFiStampsList = new ArrayList<WiFiStamps>();
    @SerializedName("RequestHeader")
    private RequestHeader requestHeader = new RequestHeader();

    private LocationData() {
    }

    public static LocationData wrap(WiFiStamps wiFiStamps) {
        LocationData data = new LocationData();
        data.wiFiStampsList.add(wiFiStamps);
        return data;
    }

    public List<WiFiStamps> getWiFiStampsList() {
        return wiFiStampsList;
    }

    private static class RequestHeader {
        @SerializedName("DeviceProfile")
        private DeviceProfile deviceProfile = DeviceProfile.getInstance();
    }
}

