package mass.Ranger.Device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import mass.Ranger.Activity.NavigationActivity;
import mass.Ranger.Activity.TrackingActivity;
import mass.Ranger.Algorithm.ParticleFilter.SimpleParticleFilter;
import mass.Ranger.Algorithm.StepDetection.StepCandidate;
import mass.Ranger.Algorithm.StepDetection.StepDetector;
import mass.Ranger.Algorithm.StepDetection.StepEventListener;
import mass.Ranger.Data.IO.DataFiles;
import mass.Ranger.Data.IO.DataSerializer;
import mass.Ranger.Util.Utils;
import mass.Ranger.View.PathView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class BackgroundPositioning implements ComboSensor.ComboSensorEventListener, StepEventListener {

    private Context applicationContext;
    private StepDetector stepDetector;
    private static ComboSensor comboSensor;
    // writer
    private static DataSerializer serializer;
    private LinkedList<LocationData> locationStamps = new LinkedList<LocationData>();
    // trace ID
    public static String patrollingID;
    private ArrayList<Double> magnetReading = new ArrayList<Double>();
    /**
     * For Navigation
     */
    private boolean navigationStarted = false;
    public static SimpleParticleFilter PF = null;
    public static ArrayList<List<AccessPointReading>> WiFis = new ArrayList<List<AccessPointReading>>();
    public static double realtimeX = 0;
    public static double realtimeY = 0;
    public static double initial_radian = 0;
    public static boolean radianInitialized = false;
    public static ArrayList<Float> dispXArray = new ArrayList<Float>();
    public static ArrayList<Float> dispYArray = new ArrayList<Float>();
    public static ArrayList<Integer> WiFiIndex = new ArrayList<Integer>();
    public static ArrayList<Float> traceRad = new ArrayList<Float>();
    public static ArrayList<Float> stepTrace = new ArrayList<Float>();
    public static ArrayList<ArrayList<Double>> magnetTrace = new ArrayList<ArrayList<Double>>();
    private static List<AccessPointReading> currentFingerprint;
    private ArrayList<ArrayList<Double>> currentMagnet = new ArrayList<ArrayList<Double>>();

    private NavigationActivity parentActivity;


    public BackgroundPositioning(Context context) {
        applicationContext = context;
        initSensors();
    }

    private List<AccessPointReading> getCurrFingerprint() {
        return currentFingerprint;
    }

    private ArrayList<ArrayList<Double>> getCurrentMagnet() {
        return currentMagnet;
    }

    private void initSensors() {
        // combosensor
        comboSensor = ComboSensor.getInstance(applicationContext);
        comboSensor.registerComboSensorEventListener(this);
        // stepdetector is not a part of combosensor
        stepDetector = new StepDetector(Utils.DefaultSampleRate);
        stepDetector.addStepEventListener(this);
    }

    /**
     * Start a background positioning task.
     */
    public synchronized void start() {
        // get trace ID
        patrollingID = UUID.randomUUID().toString();
        // init serializer with id
        serializer = new DataSerializer(applicationContext, patrollingID);
        // init step detector's vars
        //stepDetector.init();
        // start sensing
        comboSensor.start(applicationContext);
    }

    public synchronized void stop() {
        // stop sensing
        comboSensor.stop(applicationContext);
        serializer.close();
        radianInitialized = false;
        navigationStarted = false;
    }

    @Override
    public void currentValueChanged(ComboSensorReading reading) {
        try {
            stepDetector.detectStep(reading.getTimestamp(),
                    reading.getAcceleration().X,
                    reading.getAcceleration().Y,
                    reading.getAcceleration().Z,
                    Utils.normalizeDegree(Utils.radianToDegree(reading.getAzimuth())));
            double magnetMagnitude = reading.getCompass().magnitude();
            magnetReading.add(magnetMagnitude);
            // write to file
            if (TrackingActivity.serviceOn)
                serializer.serialize(reading);
            // valid wifi fingerprint
            if (reading.getWifiStamps() != null && reading.getWifiStamps().getWiFiPoints().size() > 0) {
                //update finger print
                currentFingerprint = reading.getWifiStamps().getWiFiPoints();
                //code unchanged
                LocationData data = LocationData.wrap(reading.getWifiStamps());
                locationStamps.addLast(data);
                if (locationStamps.size() > 10) {
                    locationStamps.removeFirst();
                }
                if (TrackingActivity.serviceOn)
                    serializer.serialize(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onMagneticFieldAccuracyChanged(int accuracy) {
        if (accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && navigationStarted == true) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(null);
            dlgAlert.setMessage("The accuracy of the magnet sensor is not satisfactory, please perform calibration gesture.")
                    .setMessage("Calibration required")
                    .setPositiveButton("OK", new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navigationStarted = true;
                        }
                    });
            dlgAlert.setCancelable(false);
            navigationStarted = false;
            dlgAlert.create().show();
        }
        return;
    }

    public void initPF() {
        PF = new SimpleParticleFilter(0, 0, WiFis, dispXArray, dispYArray, traceRad, magnetTrace);
    }

    @Override
    public void onStepDetected(StepCandidate step) {
        try {
            if (parentActivity != null) {
                parentActivity.setIndicator(true);
            }
            final double heading = step.getHeadingInDegree();
            double radian = Utils.degreeToRadian(heading);
            // magnet sliding window <= 5
            // total number of magnet data saved = 5*5
            if (currentMagnet.size() < 5)
                currentMagnet.add(magnetReading);
            else {
                currentMagnet.remove(0);
                currentMagnet.add(magnetReading);
            }

            //make sure the initial radian is the same
            if (!radianInitialized) {
                initial_radian = radian;
                radian = radian - initial_radian + Math.PI / 2;
                radianInitialized = true;
            } else {
                radian = radian - initial_radian + Math.PI / 2;
            }
            realtimeX += Math.cos(radian) * PathView.stepLength;
            realtimeY += Math.sin(radian) * PathView.stepLength;

            // Navigation Service
            if (NavigationActivity.serviceOn) {
                if (!navigationStarted) {
                    PF.findStartingPoint(getCurrFingerprint());
                    navigationStarted = true;
                } else {
                    // put coordinates, wifi-fingerprint and magnet readings into pf
                    PF.input(realtimeX, realtimeY, getCurrFingerprint(), getCurrentMagnet());
                    NavigationActivity.pathView.addOneStep(PF.currTraceIndex, (float) PF.currXonTrace, (float) PF.currYonTrace, PF.radius);
                }
            }
            // Tracking Service
            if (TrackingActivity.serviceOn) {
                TrackingActivity.pathView.addOneStep((float) radian);
                DataFiles.record(String.valueOf(radian), DataFiles.FileType.step);
                for (Double aMagnetReading : magnetReading)
                    DataFiles.record(String.valueOf(aMagnetReading), DataFiles.FileType.mag);
                DataFiles.record(currentFingerprint, DataFiles.FileType.wifiAP);
            }
            // init
            magnetReading = new ArrayList<Double>();
            if (parentActivity != null) {
                parentActivity.setIndicator(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setParentActivity(NavigationActivity activity) {
        parentActivity = activity;
    }
}
