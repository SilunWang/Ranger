package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.Location;
import mass.Ranger.Data.IO.GsonUtil;
import org.joda.time.DateTime;

import java.util.Date;

public class LocationStamp {
    @SerializedName("Location")
    private Location location;
    @SerializedName("Timestamp")
    private Date currentTime;
    @SerializedName("VenueID")
    private String venueId;

    public LocationStamp(Location location, Date currentTime, String venueId) {
        this.location = location;
        this.currentTime = currentTime;
        this.venueId = venueId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    @Override
    public String toString() {
        return "LocationStamp{" +
                "location=" + location +
                ", currentTime=" + GsonUtil.DATE_TIME_FORMATTER.get().print(new DateTime(currentTime)) +
                ", venueId='" + venueId + '\'' +
                '}';
    }
}
