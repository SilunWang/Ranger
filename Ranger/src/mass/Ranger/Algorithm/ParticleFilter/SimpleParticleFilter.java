package mass.Ranger.Algorithm.ParticleFilter;

import android.util.Log;
import mass.Ranger.Algorithm.DTW.DTWDataPoint;
import mass.Ranger.Algorithm.DTW.FastDtw.dtw.FastDTW;
import mass.Ranger.Algorithm.DTW.FastDtw.dtw.TimeWarpInfo;
import mass.Ranger.Algorithm.DTW.FastDtw.timeseries.TimeSeries;
import mass.Ranger.Algorithm.DTW.FastDtw.timeseries.TimeSeriesPoint;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunction;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunctionFactory;
import mass.Ranger.Device.AccessPointReading;
import mass.Ranger.Logger.MyLog;
import mass.Ranger.View.PathView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class SimpleParticleFilter {

    public static int dtwWindowSize = 10;
    public static int dtwDensity = 5;
    public static int extraSampleWindow = 0;
    private static int bufferSize = 20;
    private double sdLen = 200;
    private static int particleNum = bufferSize;
    public float[] particleX = new float[bufferSize]; // displacement X
    public float[] particleY = new float[bufferSize]; // displacement Y
    public double[] rad = new double[bufferSize];
    public double[] len = new double[bufferSize];
    public double[] weight = new double[bufferSize];
    public float[] newParticleX = new float[bufferSize]; // displacement X
    public float[] newParticleY = new float[bufferSize]; // displacement Y
    public double[] radNew = new double[bufferSize];
    public double[] lenNew = new double[bufferSize];
    public double[] cumwgt = new double[bufferSize];

    private double lastXonTrace;
    private double lastYonTrace;
    public double currXonTrace;
    public double currYonTrace;
    double dispXLast = 0;
    double dispYLast = 0;
    public int currTraceIndex;
    public float currRadOnTrace;
    public int lastTraceIndex;

    public float radius;
    public int traceLen;
    ArrayList<ArrayList<Double>> magTrace;

    private static ArrayList<List<AccessPointReading>> WiFis = new ArrayList<List<AccessPointReading>>();
    private static ArrayList<Float> arrayX = new ArrayList<Float>();
    private static ArrayList<Float> arrayY = new ArrayList<Float>();
    private static ArrayList<Float> traceRad = new ArrayList<Float>();

    //TODO: what is TraceRad？
    public SimpleParticleFilter(float iniX, float iniY,
                                ArrayList<List<AccessPointReading>> WiFiSamples,
                                ArrayList<Float> traceX,
                                ArrayList<Float> traceY,
                                ArrayList<Float> trRad,
                                ArrayList<ArrayList<Double>> magTrace) {
        WiFis = WiFiSamples;
        arrayX = (ArrayList<Float>) traceX.clone();
        arrayY = (ArrayList<Float>) traceY.clone();
        this.magTrace = (ArrayList<ArrayList<Double>>) magTrace.clone();
        traceRad = trRad;
        if (traceRad.size() > 0)
            currRadOnTrace = traceRad.get(0);

        //New File Format might fix this mess
        traceLen = arrayX.size();
        if (traceLen > traceRad.size()) {
            traceLen = traceRad.size();
        }
        if (traceLen > magTrace.size()) {
            traceLen = magTrace.size();
        }
        if (traceLen > WiFiSamples.size()) {
            traceLen = WiFiSamples.size();
        }

        particleNum = bufferSize;
        double initWeight = 1.0 / particleNum;
        Random RN = new Random();

        for (int i = 0; i < particleNum; i++) {
            weight[i] = initWeight;
            double rnRad = RN.nextDouble() * Math.PI * 2;
            double rnLen = RN.nextDouble() * sdLen;
            particleX[i] = (float) (iniX + Math.cos(rnRad) * rnLen);
            particleY[i] = (float) (iniY + Math.sin(rnRad) * rnLen);
            rad[i] = rnRad;
            len[i] = rnLen;
        }
    }

    int startIndex;
    int endIndex;
    int particleWindowSize = 10;
    float minDis;

    public void findStartingPoint(List<AccessPointReading> currFP) {
        // if there is no wifi networks
        if (currFP == null || currFP.size() == 0) {
            currTraceIndex = 0;
            return;
        }
        float max_similarity = 0;
        int max_index = 0;
        //TODO: performance improvement
        for (int i = 0; i < traceLen && i < WiFis.size(); i++) {
            float similarity = getWiFiSimilarity(currFP, WiFis.get(i));
            if (max_similarity < similarity) {
                max_index = i;
                max_similarity = similarity;
            }
        }
        currTraceIndex = max_index;
    }

    private void spreadParticles(double dispx, double dispy) {
        Random RD = new Random();

        // set sliding window for particle filter
        startIndex = currTraceIndex - particleWindowSize + 1 >= 1 ? currTraceIndex - particleWindowSize + 1 : 1;
        endIndex = currTraceIndex + particleWindowSize + 1 <= traceLen ? currTraceIndex + particleWindowSize + 1 : traceLen;

        Log.i("start-end", String.valueOf(startIndex) + "-" + String.valueOf(endIndex));
        Log.i("currIndex", String.valueOf(currTraceIndex));
        particleX[0] = arrayX.get(startIndex);
        particleY[0] = arrayY.get(startIndex);

        //evenly distributed particle?
        double len_unit = (double) (endIndex - startIndex) * PathView.stepLength / particleNum;
        for (int i = 1; i < particleNum; i++) {
            int j = (int) ((double) i / particleNum * (endIndex - startIndex));
            double LenNoise = (RD.nextDouble() - 0.5) * 10 * len_unit;
            len[i] = LenNoise;
            rad[i] = traceRad.get(startIndex + j);
            particleX[i] = (float) (particleX[i - 1] + Math.cos(rad[i]) * (len_unit + len[i]));
            particleY[i] = (float) (particleY[i - 1] - Math.sin(rad[i]) * (len_unit + len[i]));
        }
        dispXLast = dispx;
        dispYLast = dispy;
    }


    private float getEucDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    int max_wifi_index;

    public static ArrayList<DTWDataPoint> resampleArray(ArrayList<DTWDataPoint> array, int numberOfSample) {
        int count = array.size();
        float interval = (float) count / (float) numberOfSample;
        ArrayList<DTWDataPoint> result = new ArrayList<DTWDataPoint>();
        for (int i = 0; i < numberOfSample; i += 1) {
            DTWDataPoint point = (((DTWDataPoint) array.get((int) Math.floor(i * interval))));
            result.add(new DTWDataPoint(point.data, point.timeStamp));
        }
        return result;
    }

    private void updateWeights(List<AccessPointReading> currFP, ArrayList<ArrayList<Double>> currMag) {

        float max_wifi_similarity = 0;
        max_wifi_index = 0;
        // wifi-fingerprint similarity
        LinkedList<Float> wifiSimList = new LinkedList<Float>();
        // magnetic DTW distance
        ArrayList<Double> magnetDTWDistance = new ArrayList<Double>();
        TimeSeries mag2 = new TimeSeries(1);
        for (int j = 0; j < currMag.size(); j++) {
            ArrayList<Double> aStepMagnetReading = magTrace.get(j);
            int count = aStepMagnetReading.size();
            float interval = (float) count / (float) dtwDensity;
            for (int k = 0; k < dtwDensity; k++) {
                mag2.addLast((float) (((float) j) + 1.0 / ((float) dtwDensity) * k), new TimeSeriesPoint(new double[]{aStepMagnetReading.get((int) Math.floor(k * interval))}));
            }
        }
        for (int i = 0; i < traceLen; i++) {
            // get wifi distances
            if (i >= WiFis.size() || WiFis.get(i).size() == 0) {
                wifiSimList.add(0F);
            } else {
                // valid wifi-fingerprint
                float similarity = getWiFiSimilarity(currFP, WiFis.get(i));
                if (max_wifi_similarity < similarity) {
                    max_wifi_similarity = similarity;
                    max_wifi_index = i;
                }
                wifiSimList.add(similarity);
            }

            // get magnetic DTW distances
            if (i >= magTrace.size()) {
                //TODO: is this good enough?
                magnetDTWDistance.add(10.0);
            } else {
                TimeSeries mag1 = new TimeSeries(1);
                for (int j = i - (dtwWindowSize - 1) >= 0 ? i - (dtwWindowSize - 1) : 0; j <= i; j++) {
                    ArrayList<Double> aStepMagnetReading = magTrace.get(j);
                    int count = aStepMagnetReading.size();
                    float interval = (float) count / (float) dtwDensity;
                    for (int k = 0; k < dtwDensity; k++) {
                        mag1.addLast((float) (((float) j) + 1.0 / ((float) dtwDensity) * k), new TimeSeriesPoint(new double[]{aStepMagnetReading.get((int) Math.floor(k * interval))}));
                    }
                }
                if (i >= startIndex - extraSampleWindow && i <= endIndex + extraSampleWindow) {
                    final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
                    final TimeWarpInfo info = FastDTW.getWarpInfoBetween(mag1, mag2, 10, distFn);
                    magnetDTWDistance.add(info.getDistance());
                } else {
                    magnetDTWDistance.add(0.0);
                }
            }
        }

        Log.i("max-wifi", String.valueOf(max_wifi_index));

        // update particle correlation & weight
        for (int i = 0; i < particleNum; i++) {
            double correlation = 0;
            double wiFiDistSum = 0;
            double wifiMin = 1;
            double wifiMax = 0;
            double magDistSum = 0;
            double magMin = 10;
            double magMax = 0;
            int step_count = 0;
            // normalize d1, d2, d3
            for (int j = 0; j < traceLen - 1; j++) {
                step_count++;
                double temp = wifiSimList.get(j);
                if (temp > wifiMax)
                    wifiMax = temp;
                if (temp < wifiMin)
                    wifiMin = temp;
                wiFiDistSum += wifiSimList.get(j);
                double temp2 = magnetDTWDistance.get(j);
                if (temp2 > magMax)
                    magMax = temp2;
                if (temp2 < magMin)
                    magMin = temp2;
                magDistSum += temp2;
            }
            wiFiDistSum /= (step_count > 0 ? step_count : 1);
            magDistSum /= (step_count > 0 ? step_count : 1);
            // calculate correlation
            for (int j = 0; j < traceLen - 1; j++) {
                double d_wifi = (wifiSimList.get(j) - wiFiDistSum) / (wifiMax - wifiMin + 0.01);
                double d_magnet;
                if (j >= startIndex - extraSampleWindow && i <= endIndex + extraSampleWindow) {
                    d_magnet = (magDistSum - magnetDTWDistance.get(j)) / (magMax - magMin + 0.01);
                } else {
                    d_magnet = 0;
                }
                double d_Euc = getEucDistance(particleX[i], particleY[i], arrayX.get(j), arrayY.get(j)) + 0.1;
                correlation += (d_magnet + d_wifi) / d_Euc;
            }
            double tunable_para = 10;
            // weight = e^(corr / k)
            weight[i] *= Math.exp(correlation / tunable_para);
        }

        // normalize
        normalizeWeight();
    }

    private void getNewTraceIndex() {
        float centerX = 0, centerY = 0;
        minDis = 1000;
        float currDis;
        int index = 0;

        // calculate particle center
        for (int i = 0; i < particleNum; i++) {
            centerX += particleX[i] * weight[i];
            centerY += particleY[i] * weight[i];
        }
        Log.i("particle center", "X: " + centerX + " Y: " + centerY);
        // closest point index on path
        for (int i = 0; i < traceLen; i++) {
            currDis = getEucDistance(centerX, centerY, arrayX.get(i), arrayY.get(i));
            if (minDis > currDis) {
                minDis = currDis;
                index = i;
            }
        }
        Log.i("trace index", String.valueOf(index));
        lastTraceIndex = currTraceIndex;
        // chasing wifi
        if (max_wifi_index > currTraceIndex) {
            if (index > currTraceIndex)
                currTraceIndex = index + 3 < max_wifi_index ? index + 3 : max_wifi_index;
            else
                currTraceIndex = currTraceIndex + 3 < max_wifi_index ? currTraceIndex + 3 : max_wifi_index;
        } else if (max_wifi_index < currTraceIndex) {
            if (index < currTraceIndex)
                currTraceIndex = index - 3 > max_wifi_index ? index - 3 : max_wifi_index;
            else
                currTraceIndex = currTraceIndex - 3 > max_wifi_index ? currTraceIndex - 3 : max_wifi_index;
        } else {
            currTraceIndex = index;
        }
        double low_pass_filter = 0.6;
        currTraceIndex = (int) ((1 - low_pass_filter) * lastTraceIndex + low_pass_filter * currTraceIndex);
    }

    private void normalizeWeight() {
        double sum = 0;
        for (int i = 0; i < particleNum; i++) {
            sum += weight[i];
        }
        for (int i = 0; i < particleNum; i++) {
            if (Math.abs(sum) < 0.000000001)
                weight[i] = 1.0 / particleNum;
            else
                weight[i] /= sum;
        }
    }

    // resample the particles according to weights
    private void Resample() {
        Random RN = new Random();
        cumwgt[0] = weight[0];
        for (int i = 1; i < particleNum; i++) {
            cumwgt[i] = cumwgt[i - 1] + weight[i];
        }

        for (int i = 1; i < particleNum; i++) {
            double tempRN = RN.nextDouble();
            int index = 0;
            while (cumwgt[index] < tempRN) {
                index++;
            }

            newParticleX[i] = particleX[index];
            newParticleY[i] = particleY[index];
            radNew[i] = rad[index];
            lenNew[i] = len[index];
        }

        for (int i = 1; i < particleNum; i++) {
            particleX[i] = newParticleX[i];
            particleY[i] = newParticleY[i];
            rad[i] = radNew[i];
            len[i] = lenNew[i];
        }
    }

    public void input(double inX, double inY, List<AccessPointReading> currFP, ArrayList<ArrayList<Double>> currMag) {

        spreadParticles(inX, inY);

        if (currFP != null && currFP.size() > 0) {
            updateWeights(currFP, currMag);
            getNewTraceIndex();
        } else {
            // forward one step
            lastTraceIndex = currTraceIndex;
            currTraceIndex = currTraceIndex + 1 < traceLen ? currTraceIndex + 1 : traceLen - 1;
            Log.i("wifi", "no wifi??");
        }

        radius = 1 / (minDis * 5000) < 25 ? 1 / (minDis * 5000) : 25;
        // update position
        lastXonTrace = currXonTrace;
        lastYonTrace = currYonTrace;
        MyLog.i("index", currTraceIndex);
        currXonTrace = arrayX.get(currTraceIndex);
        currYonTrace = arrayY.get(currTraceIndex);
        currRadOnTrace = traceRad.get(currTraceIndex);
    }

    private static float getWiFiSimilarity(List<AccessPointReading> L1, List<AccessPointReading> L2) {
        float sim = 0.0F;
        int A1 = L1.size();
        int A2 = L2.size();
        int AComm = 0;

        for (AccessPointReading aL1 : L1) {
            for (AccessPointReading aL2 : L2) {
                if (aL1.getBssid().equals(aL2.getBssid())) {
                    AComm++;
                    float a = Math.min(99 - aL1.getRSSI(), 99 - aL2.getRSSI());
                    float b = Math.max(99 - aL1.getRSSI(), 99 - aL2.getRSSI());
                    sim += a / b;
                }
            }
        }
        return sim / (A1 + A2 - AComm);
    }
}
