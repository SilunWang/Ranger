package mass.Ranger.Util;

import com.google.gson.annotations.SerializedName;

import java.text.MessageFormat;

public class Range {
    private final static String TAG = Range.class.getName();
    @SerializedName("MaxValue")
    private double maxValue;
    @SerializedName("MinValue")
    private double minValue;

    public Range(double minValue, double maxValue) {
        if (minValue > maxValue) {
            throw new RuntimeException(MessageFormat.format("minValue greater than maxValue: {0} > {1}", minValue, maxValue));
        }

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public double midValue() {
        return (minValue + maxValue) / 2;
    }

    public double ensureInRange(double value) {
        if (value < minValue) {
            value = minValue;
        }

        if (value > maxValue) {
            value = maxValue;
        }

        return value;
    }

    public boolean inside(double value) {
        return value >= minValue && value <= maxValue;
    }

    public double randomValue() {
        return ThreadSafeRandom.nextDouble(this.minValue, this.maxValue);
    }
}
