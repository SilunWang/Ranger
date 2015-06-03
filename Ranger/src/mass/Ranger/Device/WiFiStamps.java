package mass.Ranger.Device;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WiFiStamps {
    private final static String TAG = WiFiStamps.class.getName();
    @SerializedName("WifiPoints")
    private ArrayList<AccessPointReading> wiFiPoints = new ArrayList<AccessPointReading>();
    @SerializedName("TSBegin")
    private Date startTime = new Date();
    @SerializedName("TSEnd")
    private Date endTime = new Date();
    @SerializedName("Heading")
    private String heading;
    @SerializedName("Level")
    private String level;
    @SerializedName("Loc")
    private String location;
    @SerializedName("Store")
    private String Store;
    @SerializedName("Venue")
    private String venue;
    @SerializedName("ts")
    private Date ts;

    public WiFiStamps() {
    }

    public WiFiStamps(WiFiStamps wiFiStamps) {
        this.wiFiPoints = new ArrayList<AccessPointReading>(wiFiStamps.wiFiPoints);
        this.startTime = wiFiStamps.startTime;
        this.endTime = wiFiStamps.endTime;
    }

    public List<AccessPointReading> getWiFiPoints() {
        return Collections.unmodifiableList(this.wiFiPoints);
    }

    public boolean add(AccessPointReading object) {
        return wiFiPoints.add(object);
    }

    public void clear() {
        wiFiPoints.clear();
    }

    public long getStartTimeMillis() {
        return startTime.getTime();
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTime.setTime(startTimeMillis);
        this.ts = this.startTime;
    }

    public long getEndTimeMillis() {
        return endTime.getTime();
    }

    public void setEndTimeTimeMillis(long endTimeMillis) {
        this.endTime.setTime(endTimeMillis);
    }

    public int size() {
        return wiFiPoints.size();
    }

    public boolean isEmpty() {
        return wiFiPoints.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WiFiStamps)) return false;

        WiFiStamps that = (WiFiStamps) o;

        if (Store != null ? !Store.equals(that.Store) : that.Store != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (heading != null ? !heading.equals(that.heading) : that.heading != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (ts != null ? !ts.equals(that.ts) : that.ts != null) return false;
        if (venue != null ? !venue.equals(that.venue) : that.venue != null) return false;
        if (wiFiPoints != null ? !wiFiPoints.equals(that.wiFiPoints) : that.wiFiPoints != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = wiFiPoints != null ? wiFiPoints.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (heading != null ? heading.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (Store != null ? Store.hashCode() : 0);
        result = 31 * result + (venue != null ? venue.hashCode() : 0);
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        return result;
    }
}
