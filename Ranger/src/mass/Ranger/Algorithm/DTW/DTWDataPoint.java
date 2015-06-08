package mass.Ranger.Algorithm.DTW;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class DTWDataPoint implements Cloneable {
    public double[] data;
    public float timeStamp;

    public DTWDataPoint(ArrayList<Double> _data, float _timeStamp) {
        data = ArrayUtils.toPrimitive((Double[]) _data.toArray());
        timeStamp = _timeStamp;
    }

    public DTWDataPoint(double[] _data, float _timeStamp) {
        data = _data.clone();
        timeStamp = _timeStamp;
    }

    public DTWDataPoint(Double _data, float _timeStamp) {
        data = new double[]{_data.doubleValue()};
        timeStamp = _timeStamp;
    }
}
