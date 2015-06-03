package mass.Ranger.Algorithm.ParticleWeighter;

import mass.Ranger.Algorithm.Localization.Point;

public abstract class IParticleWeighter {
    private long timestamp;

    public final long getTimestamp() {
        return timestamp;
    }

    public final void setTimestamp(long value) {
        timestamp = value;
    }

    public abstract double WeightParticle(Point last, Point current);
}