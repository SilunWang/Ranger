package mass.Ranger.Device;

import mass.Ranger.Data.IO.RawName;
import mass.Ranger.Util.Quaternion;
import mass.Ranger.Util.Vector3;

public class ComboSensorReading implements SensorReading {
    @RawName(value = "Timestamp", priority = 1)
    private long timestamp;
    /**
     * Raw acceleration directly from Accelerometer, including the force of gravity
     */
    @RawName(value = "Acc", priority = 2, type = RawName.RawType.Combo)
    private Vector3 acceleration;
    /**
     * Orientation
     */
    @RawName(value = "Comp", priority = 3, type = RawName.RawType.Combo)
    private Vector3 compass;
    /**
     *
     */
    @RawName(value = "Comp.TrueHeading", priority = 4)
    private double trueHeading;
    /**
     *
     */
    @RawName(value = "Comp.MagneticHeading", priority = 5)
    private double magneticHeading;
    /**
     *
     */
    @RawName(value = "Comp.HeadingAccuracy", priority = 6)
    private int headingAccuracy;
    /**
     *
     */
    @RawName(value = "Gyro", priority = 7, type = RawName.RawType.Combo)
    private Vector3 rotationRate;
    /**
     *
     */
    @RawName(value = "Motion.Yaw", priority = 8)
    private float azimuth;
    /**
     *
     */
    @RawName(value = "Motion.Pitch", priority = 9)
    private float pitch;
    /**
     *
     */
    @RawName(value = "Motion.Roll", priority = 10)
    private float roll;
    /**
     *
     */
    @RawName(value = "Motion", priority = 11, type = RawName.RawType.Combo)
    private Quaternion quaternion;
    /**
     * calculated by {@code acceleration} using low pass filter, acceleration without gravity
     */
    @RawName(value = "DeviceAcc", type = RawName.RawType.Combo, priority = 12)
    private Vector3 linearAcceleration;
    @RawName(value = "DeviceRot", priority = 13, type = RawName.RawType.Combo)
    private Vector3 rotateVectorReading;
    /**
     * Gravity calculated by {@code acceleration} using low pass filter
     */
    @RawName(value = "Gravity", priority = 14, type = RawName.RawType.Combo)
    private Vector3 gravity;

    @RawName(value = "Pressure", priority = 15)
    private float pressure;

    private Vector3 deviceRotationRate;
    private WiFiStamps wifiStamps;

    public Vector3 getRotateVectorReading() {
        return rotateVectorReading;
    }

    public void setRotateVectorReading(Vector3 rotateVectorReading) {
        this.rotateVectorReading = rotateVectorReading;
    }

    public double getMagneticHeading() {
        return magneticHeading;
    }

    protected void setMagneticHeading(double magneticHeading) {
        this.magneticHeading = magneticHeading;
    }

    protected Vector3 getCompass() {
        return compass;
    }

    public void setCompass(Vector3 compass) {
        this.compass = compass;
    }

    public Vector3 getLinearAcceleration() {
        return linearAcceleration;
    }

    protected void setLinearAcceleration(Vector3 linearAcceleration) {
        this.linearAcceleration = linearAcceleration;
    }

    public int getHeadingAccuracy() {
        return headingAccuracy;
    }

    protected void setHeadingAccuracy(int headingAccuracy) {
        this.headingAccuracy = headingAccuracy;
    }

    public double getTrueHeading() {
        return trueHeading;
    }

    protected void setTrueHeading(double trueHeading) {
        this.trueHeading = trueHeading;
    }

    public Vector3 getRotationRate() {
        return rotationRate;
    }

    protected void setRotationRate(Vector3 rotationRate) {
        this.rotationRate = rotationRate;
    }

    public float getPitch() {
        return pitch;
    }

    protected void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Quaternion getQuaternion() {
        return quaternion;
    }

    protected void setQuaternion(Quaternion quaternion) {
        this.quaternion = quaternion;
    }

    public float getRoll() {
        return roll;
    }

    protected void setRoll(float roll) {
        this.roll = roll;
    }

    public float getAzimuth() {
        return azimuth;
    }

    protected void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public Vector3 getAcceleration() {
        return acceleration;
    }

    protected void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
    }

    public Vector3 getDeviceRotationRate() {
        return deviceRotationRate;
    }

    protected void setDeviceRotationRate(Vector3 deviceRotationRate) {
        this.deviceRotationRate = deviceRotationRate;
    }

    public Vector3 getGravity() {
        return gravity;
    }

    protected void setGravity(Vector3 gravity) {
        this.gravity = gravity;
    }

    public float getPressure() {
        return pressure;
    }

    protected void setPressure(float press) {
        this.pressure = press;
    }

    public WiFiStamps getWifiStamps() {
        return wifiStamps;
    }

    protected void setWifiStamps(WiFiStamps wifiStamps) {
        this.wifiStamps = wifiStamps;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ComboSensorReading{" +
                "timestamp=" + timestamp +
                ", acceleration=" + acceleration +
                ", compass=" + compass +
                ", trueHeading=" + trueHeading +
                ", magneticHeading=" + magneticHeading +
                ", headingAccuracy=" + headingAccuracy +
                ", rotationRate=" + rotationRate +
                ", azimuth=" + azimuth +
                ", pitch=" + pitch +
                ", roll=" + roll +
                ", quaternion=" + quaternion +
                ", airpressure=" + pressure +
                ", linearAcceleration=" + linearAcceleration +
                ", rotateVectorReading=" + rotateVectorReading +
                ", gravity=" + gravity +
                ", deviceRotationRate=" + deviceRotationRate +
                ", wifiStamps=" + wifiStamps +
                '}';
    }
}
