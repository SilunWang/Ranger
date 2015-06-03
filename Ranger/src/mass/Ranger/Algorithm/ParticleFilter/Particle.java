package mass.Ranger.Algorithm.ParticleFilter;

import mass.Ranger.Algorithm.Localization.Point;

public class Particle {
    private Point  current = new Point(0, 0);
    private Point  last    = new Point(0, 0);
    private double weight  = 0;

    public Point getCurrent() {
        return current;
    }

    public void setCurrent(Point current) {
        this.last = this.current;
        this.current = current;
    }

    public Point getLast() {
        return last;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void copyFrom(Particle from) {
        current.copyFrom(from.current);
        last.copyFrom(from.last);
        weight = from.weight;
    }

    @Override
    public String toString() {
        return "Particle{" +
               "current=" + current +
               ", last=" + last +
               ", weight=" + weight +
               '}';
    }
}