package mass.Ranger.Algorithm.ParticleFilter;

import java.util.Date;

public class StepVector {
    private double x;
    private double y;
    private long timestamp;

    public StepVector(double x, double y, Date time) {
        this.x = x;
        this.y = y;
        timestamp = time.getTime();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
