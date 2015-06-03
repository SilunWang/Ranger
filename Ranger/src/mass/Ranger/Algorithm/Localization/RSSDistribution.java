package mass.Ranger.Algorithm.Localization;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RSSDistribution {
    private final static double MinimumProbability = 0.001;
    private final static int SlotCount = 12;
    private final static int SlotSize = 5;
    private double[] distributions;
    private String BSSID;

    public RSSDistribution(String BSSID, double[] distributions) {
        this.BSSID = BSSID;
        this.distributions = Arrays.copyOf(distributions, distributions.length);
    }

    public static double calculateProbability(
            List<ReceivedSignalStrengthMeasurement> sampleList,
            Map<String, RSSDistribution> distributions) {
        double probability = 0;
        int maxRSS = Integer.MIN_VALUE;
        for (ReceivedSignalStrengthMeasurement sample : sampleList) {
            String bssid = sample.getBSSID();
            int receivedSignalStrength = sample.getReceivedSignalStrength();
            if (distributions.containsKey(bssid)) {
                probability += Math.log10(distributions.get(bssid).getProbability(receivedSignalStrength));
            } else {
                probability += Math.log(RSSDistribution.MinimumProbability);
            }

            if (receivedSignalStrength > maxRSS) {
                maxRSS = receivedSignalStrength;
            }
        }

        return probability;
    }

    protected static int toSlotIndex(int RSS) {
        int index = (Math.abs(RSS) - 40) / SlotSize;

        if (index >= SlotCount) {
            index = SlotCount - 1;
        }

        if (index < 0) {
            index = 0;
        }

        return index;
    }

    @Override
    public String toString() {
        return "RSSDistribution{" +
                "distributions=" + Arrays.toString(distributions) +
                ", BSSID='" + BSSID + '\'' +
                '}';
    }

    public double getProbability(int RSS) {
        int idx = toSlotIndex(RSS);
        return Math.max(distributions[idx], MinimumProbability);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RSSDistribution)) {
            return false;
        }

        RSSDistribution that = (RSSDistribution) o;

        return !(BSSID != null ? !BSSID.equals(that.BSSID) : that.BSSID != null) && Arrays.equals(distributions, that.distributions);

    }

    @Override
    public int hashCode() {
        int result = distributions != null ? Arrays.hashCode(distributions) : 0;
        result = 31 * result + (BSSID != null ? BSSID.hashCode() : 0);
        return result;
    }
}
