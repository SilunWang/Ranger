package mass.Ranger.Data.Location;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;

import java.util.Date;

public class Trace implements Parcelable {
    /**
     * Trace that just be created
     */
    @Expose
    public static final int            STATE_CREATED    = 0;
    /**
     * Trace that has been compressed
     */
    @Expose
    public static final int            STATE_COMPRESSED = 1;
    /**
     * Trace that has been sent to server (i.e. this stepTrace can be remove from the service)
     */
    @Expose
    public static final int            STATE_SENT       = 2;
    /**
     * Trace for floorsetup
     */
    @Expose
    public static final int            TYPE_PLANE       = 0;
    /**
     * Trace for patrolling
     */
    @Expose
    public static final int            TYPE_SPACE       = 1;
    @Expose
    public static final Creator<Trace> CREATOR          = new Creator<Trace>() {
        @Override
        public Trace createFromParcel(Parcel source) {
            return new Trace(source.readInt(), source.readInt(), source.readString());
        }

        @Override
        public Trace[] newArray(int size) {
            return new Trace[size];
        }
    };
    private int    type;
    private int    state;
    private Date   date;
    private String uuid;

    public Trace(int type, String uuid) {
        this(type, STATE_CREATED, uuid);
    }

    public Trace(int type, int state, String uuid) {
        this.type = type;
        this.state = state;
        this.uuid = uuid;
        this.date = new Date();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getType());
        dest.writeInt(getState());
        dest.writeString(getUuid());
    }

    public String getUuid() {
        return uuid;
    }

    public int getState() {
        return state;
    }

    public Trace setState(int state) {
        this.state = state;
        return this;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return new Date(date.getTime());
    }

    @Override
    public String toString() {
        return "Trace{" +
               "type=" + type +
               ", state=" + state +
               ", date=" + date +
               ", uuid='" + uuid + '\'' +
               '}';
    }
}
