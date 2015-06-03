package mass.Ranger.Util;

import android.graphics.Point;

import java.util.List;

public class Utils {
    public static final  String TAG                   = Utils.class.getName();
    public final static  int    TicksInMs             = 10000;
    public final static  int    DefaultSampleRate     = 50;
    public final static  double EarthRadiusInMeter    = 6371000.0;
    public final static  double TicksInOneSecond      = 1E7;
    public final static  double defaultPrecision      = 0.000000001;
    public static final  int    MILLISECOND_IN_SECOND = 1000;
    private final static char[] HEX_ARRAY             = "0123456789ABCDEF".toCharArray();

    public static String convertMillisecondToTimeSpan(long millisecond) {
        long value = millisecond;
        long tick = (value % 1000) * 10000;
        value /= 1000;
        long second = value % 60;
        value /= 60;
        long minute = value % 60;
        value /= 60;
        long hour = value % 24;
        value /= 24;
        long day = value;
        if (day == 0) {
            return String.format("%02d:%02d:%02d.%d", hour, minute, second, tick);
        } else {
            return String.format("%d.%02d:%02d:%02d.%d", day, hour, minute, second, tick);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static long currentTimeTicks() {
        return timeMillisToTicks(System.currentTimeMillis());
    }

    public static long timeMillisToTicks(long timeMillis) {
        return timeMillis * TicksInMs + 621355968000000000L;
    }

    public static double normalizeDegree(double degree) {
        return (360 + degree % 360) % 360;
    }

    public static boolean equals(double a, double b, double precision) {
        return Math.abs(a - b) < precision;
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < defaultPrecision;
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /// <summary>
    /// given vector a->b, find a point c that (a->c):(a->b) == ratio
    /// </summary>
    /// <example>
    /// findPointInLine(a,b,0) == a
    /// findPointInLine(a,b,1) == b
    /// findPointInLine(a,b,0.5) == mid point between a & b
    /// </example>
    public static Point findPointInLine(Point a, Point b, double ratio) {
        return new Point((int) (a.x + (b.x - a.x) * ratio), (int) (a.y + (b.y - a.y) * ratio));
    }

    /**
     * Convert BSSID to virtual BSSID (set the last 3 bit to 0)
     *
     * @param BSSID
     * @return
     */
    public static String convertToVirtualBSSID(String BSSID) {
        int size = BSSID.length();
        char last = BSSID.charAt(size - 1);
        if (last >= '0' && last < '8') {
            return BSSID.substring(0, size - 1).toUpperCase() + '0';
        } else {
            return BSSID.substring(0, size - 1).toUpperCase() + '8';
        }
    }

    public static double averageDegree(List<Double> headingInDegrees) {
        double x = 0;
        double y = 0;
        for (double heading : headingInDegrees) {
            double radian = Utils.degreeToRadian(heading);
            x += Math.cos(radian);
            y += Math.sin(radian);
        }
        double averageRadian = Math.atan2(y, x);
        return Utils.radianToDegree(averageRadian);
    }

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }

    /// <summary>
    /// Convert a radian to degree
    /// </summary>
    /// <param name="radian"></param>
    /// <returns></returns>
    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }



    private double diffTwoAngle(double angle1, double angle2) {
        // left turn
        double diff1 = angle2 - angle1;
        if (diff1 < 0) {
            diff1 += 2 * Math.PI;
        }

        //right turn
        double diff2 = angle1 - angle2;
        if (diff2 < 0) {
            diff2 += 2 * Math.PI;
        }

        if (diff1 > diff2) {
            return -diff2;
        } else {
            return diff1;
        }
    }

//    public static <T> ArrayList<T> resampleArray(ArrayList<T> array, int numberOfSample){
//        int count = array.size();
//        float interval = (float)count/(float)numberOfSample;
//        ArrayList<T> result = new ArrayList<T>();
//        for (int i = 0; i < numberOfSample; i+=1) {
//            result.add((T)array.get((int)Math.floor(i*interval)));
//        }
//        return result;
//    }
}
