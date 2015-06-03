package mass.Ranger.Algorithm.Localization;


import mass.Ranger.Data.Location.IZoneInfo;

import java.util.List;

public interface IZoneSelection {
    String getHelpString();

    String getID();

    <T extends IZoneInfo> List<T> select(
            List<T> zones,
            List<ReceivedSignalStrengthMeasurement> samples,
            String requestID,
            String... args);
}
