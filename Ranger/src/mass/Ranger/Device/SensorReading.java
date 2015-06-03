package mass.Ranger.Device;

public interface SensorReading {
    /**
     * Returns time in nanosecond (1s = 1E7 ns) since January 1, 1970 00:00:00.0 UTC.
     */
    long getTimestamp();
}
