package mass.Ranger.Util;

public class ThreadSafeRandom {
    private static       Random random = new Random((int) System.currentTimeMillis());

    public static Random getRandom() {
        return random;
    }

    public static boolean hit(double probability) {
        if (probability > 1 || probability < 0) {
            throw new IndexOutOfBoundsException("probability can only from 0 to 1.");
        }

        return nextDouble() < probability;
    }

    public synchronized static int next() {
        return random.next(Integer.MAX_VALUE);
    }

    public synchronized static int next(int max) {
        return random.next(max);
    }

    public synchronized static double nextDouble() {
        double rand = random.nextDouble();
        return rand;
    }

    public static double nextDouble(double maxValue) {
        double value = nextDouble() * maxValue;
        return value;
    }

    public static double nextDouble(double minValue, double maxValue) {
        double range = maxValue - minValue;
        double randomValue = nextDouble() * range + minValue;
        return randomValue;
    }

    public static double nextDoubleSidedExponential(double mean) {
        double r = nextExponential(mean);
        if (nextDouble() < 0.5) {
            r = -r;
        }

        return r;
    }

    public static double nextExponential(double mean) {
        double r = nextDouble();
        r = -1.0 * mean * Math.log(r);
        return r;
    }

    public static double nextGaussian(double mean, double std) {
        double sum = 0;
        for (int i = 0; i < 48; i++) {
            sum += ThreadSafeRandom.nextDouble() - 0.5;
        }

        sum = sum * std / 2;
        sum = sum + mean;
        return sum;
    }

    public static double nextSignedDouble(double maxValue) {
        double value = nextDouble() * maxValue * 2 - maxValue;
        return value;
    }

    /// <summary>
    /// this is mainly for debugging to get repeatability
    /// </summary>
    /// <param name="seed"></param>
    public static void resetSeed(int seed) {
        random = new Random(seed);
    }
}
