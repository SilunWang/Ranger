package mass.Ranger.Algorithm.StepDetection;

import java.util.HashMap;

public class SlidingWindowFilterBasedOnSampleRate extends SlidingWindowFilter {

    /// <summary>
    /// Filter coefficients based on sample rate, the coefficients are arrays
    /// follow Gaussian distributions
    /// </summary>
    private final static HashMap<FilterType, Double[]> FilterTypeToCoefficients = new HashMap<FilterType, Double[]>();

    static {
        FilterTypeToCoefficients.put(FilterType.FIR40, new Double[]{
                0.00101735661549253,
                0.00133548617440944,
                0.00189882669762977,
                0.00279677517520783,
                0.00410740118054645,
                0.00589120463600913,
                0.00818568737247777,
                0.0110011215194636,
                0.0143178302152479,
                0.0180852062191418,
                0.0222225873643774,
                0.0266219912484538,
                0.0311525927170294,
                0.0356667144076893,
                0.0400070004499014,
                0.0440143631373930,
                0.0475362375039717,
                0.0504346530999954,
                0.0525936378196458,
                0.0539255052404097,
                0.0543756424110140,
                0.0539255052404097,
                0.0525936378196458,
                0.0504346530999954,
                0.0475362375039717,
                0.0440143631373930,
                0.0400070004499014,
                0.0356667144076893,
                0.0311525927170294,
                0.0266219912484538,
                0.0222225873643774,
                0.0180852062191418,
                0.0143178302152479,
                0.0110011215194636,
                0.00818568737247777,
                0.00589120463600913,
                0.00410740118054645,
                0.00279677517520783,
                0.00189882669762977,
                0.00133548617440944,
                0.00101735661549253
        });
        FilterTypeToCoefficients.put(FilterType.FIR16, new Double[]{
                0.0025,
                0.0057,
                0.0147,
                0.0315,
                0.0555,
                0.0834,
                0.1099,
                0.1289,
                0.1358,
                0.1289,
                0.1099,
                0.0834,
                0.0555,
                0.0315,
                0.0147,
                0.0057,
                0.0025
        });
        FilterTypeToCoefficients.put(FilterType.FIR8, new Double[]{
                0.0051,
                0.0294,
                0.1107,
                0.2193,
                0.2710,
                0.2193,
                0.1107,
                0.0294,
                0.0051
        });
        FilterTypeToCoefficients.put(FilterType.FIR4, new Double[]{
                0.0101,
                0.2203,
                0.5391,
                0.2203,
                0.0101
        });
    }

    public SlidingWindowFilterBasedOnSampleRate(int sampleRateInHz) {
        super(GetCoefficientsBasedOnSampleRate(sampleRateInHz));
    }

    private static Double[] GetCoefficientsBasedOnSampleRate(int sampleRateInHz) {
        FilterType filterType;
        switch (sampleRateInHz) {
            case 250:
                filterType = FilterType.FIR40;
                break;

            case 100:
                filterType = FilterType.FIR16;
                break;

            case 50:
                filterType = FilterType.FIR8;
                break;

            case 25:
                filterType = FilterType.FIR4;
                break;

            default:
                filterType = FilterType.FIR8;
                break;
        }

        return FilterTypeToCoefficients.get(filterType);
    }

    private enum FilterType {
        FIR40,
        FIR16,
        FIR8,
        FIR4,
        WAVELET, // WARNING: WAVELET haven't been used
    }
}
