package mass.Ranger.Device;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import mass.Ranger.Data.Stopwatch;
import mass.Ranger.Util.Quaternion;
import mass.Ranger.Util.Utils;
import mass.Ranger.Util.Vector3;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ComboSensor implements SensorEventListener, WiFiScanner.WiFiScannerEventHandler {
    private final static String TAG = ComboSensor.class.getName();
    private static ComboSensor instance;
    private final Object wiFiStampLock = new Object();
    int flagUnChangeCount = 0;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private Sensor rotateVector;
    private Sensor barometer;
    private SensorManager sensorManager;
    private static WiFiScanner wiFiScanner;

    private float[] acceleration = new float[3];
    private float[] accelerometerReading = new float[3];
    private float[] gyroscopeReading = new float[3];
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];
    private float airpressure = 0;

    private WiFiStamps wiFiStamps;
    /**
     * represent which sensor exists
     */
    private int sensorFlag;
    /**
     * represent which sensor has new data after last sync
     */
    private int syncFlag;
    private HashSet<ComboSensorEventListener> listeners = new HashSet<ComboSensorEventListener>();
    private int magneticFieldAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_LOW;
    private ReentrantLock lock = new ReentrantLock();
    private float[] rotateVectorReading;
    private Stopwatch stopwatch = new Stopwatch();
    private AtomicInteger accCounter = new AtomicInteger();
    private AtomicInteger magCounter = new AtomicInteger();
    private AtomicInteger gyrCounter = new AtomicInteger();
    private AtomicInteger rotCounter = new AtomicInteger();
    private AtomicInteger baroCounter = new AtomicInteger();

    public ComboSensor(Context context) {
        initSensor(context);
    }

    public static synchronized ComboSensor getInstance(Context context) {
        if (instance == null) {
            instance = new ComboSensor(context);
        }
        return instance;
    }

    /**
     * <b>Directly copy from SensorManager, Galaxy S4 throws a
     * "java.lang.IllegalArgumentException: rv array length must be 9 or 16"
     * exception when invoke SensorManager.getQuaternionFromVector</b>
     * <p/>
     * Helper function to convert a rotation vector to a normalized quaternion.
     * Given a rotation vector (presumably from a ROTATION_VECTOR sensor), returns a normalized
     * quaternion in the array Q.  The quaternion is stored as [w, particleX, particleY, z]
     *
     * @param rv the rotation vector to convert
     * @param Q  an array of floats in which to store the computed quaternion
     */
    public static void getQuaternionFromVector(float[] Q, float[] rv) {
        if (rv.length == 4) {
            Q[0] = rv[3];
        } else {
            Q[0] = 1 - rv[0] * rv[0] - rv[1] * rv[1] - rv[2] * rv[2];
            Q[0] = (Q[0] > 0) ? (float) Math.sqrt(Q[0]) : 0;
        }
        Q[1] = rv[0];
        Q[2] = rv[1];
        Q[3] = rv[2];
    }

    private void initSensor(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Logger.v(TAG, "Trying to get lock to initialize sensors");
                lock.lock();
                try {
                    //Logger.v(TAG, "Succeed in getting lock for initialize sensors");
                    sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    if (accelerometer == null) {
                        //Logger.w(TAG, "Unable to find accelerometer sensor");
                    } else {
                        sensorFlag |= getFlag(Sensor.TYPE_ACCELEROMETER);
                    }

                    gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                    if (gyroscope == null) {
                        //Logger.w(TAG, "Unable to find gyroscope sensor");
                    } else {
                        sensorFlag |= getFlag(Sensor.TYPE_GYROSCOPE);
                    }

                    magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                    if (magnetometer == null) {
                        //Logger.w(TAG, "Unable to find magnetometer sensor");
                    } else {
                        sensorFlag |= getFlag(Sensor.TYPE_MAGNETIC_FIELD);
                    }

                    rotateVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    if (rotateVector == null) {
                        //Logger.w(TAG, "Unable to find rotateVector sensor");
                    } else {
                        sensorFlag |= getFlag(Sensor.TYPE_ROTATION_VECTOR);
                    }

                    barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
                    if (barometer == null) {

                    } else {
                        sensorFlag |= getFlag(Sensor.TYPE_PRESSURE);
                    }

                    wiFiScanner = new WiFiScanner(context);
                    wiFiScanner.registerWiFiHandler(ComboSensor.this);
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    /**
     * an unsafe shift function
     */
    private int getFlag(int value) {
        return 1 << value;
    }

    public void start(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                try {
                    //Logger.v(TAG, "Trying to get lock to start sensors");
                    stopwatch.reset();
                    //Logger.v(TAG, "Succeed in getting lock to start sensors");
                    if (accelerometer != null) {
                        //Logger.v(TAG, MessageFormat.format("register {0} sensor", accelerometer.getName()));
                        sensorManager.registerListener(ComboSensor.this,
                                accelerometer,
                                SensorManager.SENSOR_DELAY_GAME);
                    }
                    if (magnetometer != null) {
                        //Logger.v(TAG, MessageFormat.format("register {0} sensor", magnetometer.getName()));
                        sensorManager.registerListener(ComboSensor.this,
                                magnetometer,
                                SensorManager.SENSOR_DELAY_FASTEST);
                    }
                    if (gyroscope != null) {
                        //Logger.v(TAG, MessageFormat.format("register {0} sensor", gyroscope.getName()));
                        sensorManager.registerListener(ComboSensor.this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
                    }
                    if (rotateVector != null) {
                        //Logger.v(TAG, MessageFormat.format("register {0} sensor", rotateVector.getName()));
                        sensorManager.registerListener(ComboSensor.this,
                                rotateVector,
                                SensorManager.SENSOR_DELAY_GAME);
                    }
                    if (barometer != null) {
                        sensorManager.registerListener(ComboSensor.this,
                                barometer,
                                SensorManager.SENSOR_DELAY_GAME);
                    }

                    wiFiScanner.start(context);
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    public void stop(final Context context) {
        //Logger.v(TAG, "Trying to get lock to stop sensors");
        lock.lock();
        try {
            //Logger.v(TAG, "Succeed in getting lock to stop sensors");
            sensorManager.unregisterListener(this);
            wiFiScanner.stop(context);


        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void restartWiFiScan() {
        // TODO cancel the current wifi scan and start a new one
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        int oldSyncFlag = syncFlag;
        int flag = getFlag(type);
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerReading = event.values.clone();
                lowPassFilterForGravity();
                accCounter.incrementAndGet();
                syncFlag |= flag;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values.clone();
                magCounter.incrementAndGet();
                syncFlag |= flag;
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeReading = event.values.clone();
                gyrCounter.incrementAndGet();
                syncFlag |= flag;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                rotateVectorReading = event.values.clone();
                rotCounter.incrementAndGet();
                syncFlag |= flag;
                break;
            case Sensor.TYPE_PRESSURE:
                airpressure = event.values[0];
                baroCounter.incrementAndGet();
                syncFlag |= flag;
                break;
            default:
                //Logger.w(TAG, MessageFormat.format("Unknown sensor type: {0}", event.sensor.getName()));
        }
        if (oldSyncFlag == syncFlag) {
            flagUnChangeCount++;
            if (flagUnChangeCount >= 1000) {
                //Logger.w(TAG, "Flag not change " + flagUnChangeCount + " times");
            }
        } else {
            flagUnChangeCount = 0;
        }
        // Logger.v(TAG, String.format("sensorFlag = 0X%16s, syncFlag = 0X%16s", Integer.toBinaryString(sensorFlag),
        // Integer.toBinaryString(syncFlag)));
        if (syncFlag == sensorFlag) {
            float[] rotationMatrix = new float[16];
            float[] inclinationMatrix = new float[16];
            if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravity, geomagnetic)) {
                float[] quaternion = new float[4];
                getQuaternionFromVector(quaternion, rotationMatrix);
                SensorManager.getOrientation(rotationMatrix, orientation);
                ComboSensorReading lastReading = new ComboSensorReading();

                lastReading.setAcceleration(new Vector3(accelerometerReading));
                lastReading.setLinearAcceleration(new Vector3(acceleration));
                lastReading.setGravity(new Vector3(gravity));

                lastReading.setRotateVectorReading(new Vector3(rotateVectorReading));

                lastReading.setHeadingAccuracy(magneticFieldAccuracy);
                lastReading.setAzimuth(orientation[0]);
                lastReading.setPitch(orientation[1]);
                lastReading.setRoll(orientation[2]);
                lastReading.setRotationRate(new Vector3(gyroscopeReading));

                lastReading.setMagneticHeading(Utils.normalizeDegree(Utils.radianToDegree(orientation[0])));
                // TODO set this as 0 until we have a mature algorithm/database to calculate
                lastReading.setTrueHeading(0);

                lastReading.setCompass(new Vector3(geomagnetic));
                lastReading.setQuaternion(new Quaternion(quaternion));
                lastReading.setTimestamp(Utils.currentTimeTicks());
                lastReading.setPressure(airpressure);

                synchronized (wiFiStampLock) {
                    if (wiFiStamps != null && !wiFiStamps.isEmpty()) {
                        lastReading.setWifiStamps(new WiFiStamps(wiFiStamps));
                        wiFiStamps.clear();
                    }
                }
                for (ComboSensorEventListener listener : listeners) {
                    listener.currentValueChanged(lastReading);
                }
            }
            // clear the flag
            syncFlag = 0;
        }
    }

    private void lowPassFilterForGravity() {
        final float alpha = 0.99f;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * accelerometerReading[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * accelerometerReading[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * accelerometerReading[2];

        acceleration[0] = accelerometerReading[0] - gravity[0];
        acceleration[1] = accelerometerReading[1] - gravity[1];
        acceleration[2] = accelerometerReading[2] - gravity[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldAccuracy = accuracy;
            for (ComboSensorEventListener listener : listeners) {
                listener.onMagneticFieldAccuracyChanged(accuracy);
            }
            //TODO: delete it in the future
            //Log current accuracy...
            Log.d(TAG, MessageFormat.format("Sensor {0}'s accuracy changed to ", sensor.getName()) + accuracy);
        }
    }

    public void registerComboSensorEventListener(ComboSensorEventListener listener) {
        this.listeners.add(listener);
    }

    public void dispose() {
        wiFiScanner.removeWiFiHandler(this);
    }

    @Override
    public void onWiFiScanFinished(WiFiStamps data) {
        synchronized (wiFiStampLock) {
            this.wiFiStamps = new WiFiStamps(data);
        }
    }

    public interface ComboSensorEventListener {
        void currentValueChanged(ComboSensorReading reading);

        void onMagneticFieldAccuracyChanged(int accuracy);
    }
}
