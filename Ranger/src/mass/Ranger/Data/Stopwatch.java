package mass.Ranger.Data;

public class Stopwatch {
    private long timeCost;

    public Stopwatch() {
        reset();
    }

    /**
     * start to calculate
     */
    public void reset() {
        timeCost = System.currentTimeMillis();
    }

    /**
     * get the time cost from last start, in milliseconds
     *
     * @return
     */
    public long getTimeElapsedMillis() {
        long tc = System.currentTimeMillis();
        return tc - timeCost;
    }
}
