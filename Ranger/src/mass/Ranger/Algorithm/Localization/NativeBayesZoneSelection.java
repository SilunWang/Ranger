package mass.Ranger.Algorithm.Localization;


import mass.Ranger.Data.Location.IZoneInfo;

import java.util.Arrays;
import java.util.List;

public class NativeBayesZoneSelection implements IZoneSelection {

    @Override
    public String getHelpString() {
        return "Argument: null";
    }

    @Override
    public String getID() {
        return "naiveBayes";
    }

    @Override
    public <T extends IZoneInfo> List<T> select(
            List<T> zones,
            List<ReceivedSignalStrengthMeasurement> samples,
            String requestID,
            String... args) {
        double maxProbability = -Double.MAX_VALUE;
        T bestZone = null;
        for (T zone : zones) {
            double probability = RSSDistribution.calculateProbability(samples, zone.getRSSDistributions());
            if (probability > maxProbability) {
                maxProbability = probability;
                bestZone = zone;
            }
        }
        return Arrays.asList(bestZone);
    }
}
