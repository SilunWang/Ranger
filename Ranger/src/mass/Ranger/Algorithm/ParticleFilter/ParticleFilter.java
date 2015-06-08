package mass.Ranger.Algorithm.ParticleFilter;

import mass.Ranger.Algorithm.Localization.Point;
import mass.Ranger.Algorithm.ParticleWeighter.IParticleWeighter;
import mass.Ranger.Data.Location.IBoundary;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

/**
 * ParticleFilter class, contain all functions about filter process.
 */
public class ParticleFilter extends IParticleFilter {
    public static final double DISTANCE_THRESHOLD = 100.0;
    private final static Random random = new Random();
    private static final String TAG = ParticleFilter.class.getName();
    private double estimatedStepLength = -1;
    private int PARTICLE_NUMBER;
    private Point estimatedPosition = new Point(0, 0);
    private double stepCountForStepEstimation = 0;
    private double distanceForStepEstimation = 0;
    private Particle[] particles;
    private Particle[] newParticles;
    private Point[] initPoints;
    private int[] indexOfParticles;
    private boolean hasInitialized = false;
    private IBoundary boundary;

    public ParticleFilter(int particleNumber) {
        PARTICLE_NUMBER = particleNumber;
        initPoints = new Point[PARTICLE_NUMBER];
        particles = new Particle[PARTICLE_NUMBER];
        newParticles = new Particle[PARTICLE_NUMBER];

        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            particles[i] = new Particle();
            newParticles[i] = new Particle();
            initPoints[i] = new Point();
        }

        indexOfParticles = new int[PARTICLE_NUMBER];
    }

    public boolean isHasInitialized() {
        return hasInitialized;
    }

    /**
     * assign a init initLocation to place the particle around
     */
    public final void initParticle(Point initLocation, double locationTrustRadius, IBoundary boundary) {
        this.boundary = boundary;
        this.estimatedPosition = new Point(initLocation.getX(), initLocation.getY());

        Point initPoint = new Point(initLocation.getX(), initLocation.getY());
        double initWeight = 1.0 / PARTICLE_NUMBER;

        initDrawSampleFromUniformDistribution(initPoint, locationTrustRadius);

        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            particles[i].setCurrent(initPoints[i]);
            particles[i].setWeight(initWeight);
        }

        hasInitialized = true;
    }

    public void initDrawSampleFromUniformDistribution(Point initLocation, final double radius) {
        initDrawSampleFromUniformDistribution(initLocation, radius, 0.1);
    }

    /**
     * discrete sample from uniform distribution
     * if the particle locates inside wall, it will be discarded by reSampling later
     */
    public void initDrawSampleFromUniformDistribution(Point initLocation, final double radius, final double step) {
        ArrayList<Point> candidates = new ArrayList<Point>();
        for (double x = initLocation.getX() - radius, maxX = initLocation.getX() + radius; x < maxX; x += step) {
            for (double y = initLocation.getY() - radius, maxY = initLocation.getY() + radius; y < maxY; y += step) {
                Point point = new Point(x, y);
                if (Point.minus(initLocation, point).getLength() < radius) {
                    candidates.add(point);
                }
            }
        }
        initDiscreteSample(PARTICLE_NUMBER, candidates.size());

        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            initPoints[i].copyFrom(candidates.get(indexOfParticles[i]));
        }
    }

    /**
     * get m samples from n, m samples can overlap
     *
     * @param m m is the number of sample
     * @param n n is the total number
     */
    private void initDiscreteSample(int m, int n) {
        for (int i = 0; i < m; i++) {
            indexOfParticles[i] = random.nextInt(n);
        }
    }

    /**
     * update particles' position by a single stepVector
     *
     * @param sv the step vector
     */
    private void updatePosition(StepVector sv) {
        final double XY_VAR = 0.2;
        Random rand = new Random(1);

        for (Particle particle : particles) {
            double deltaX = randDoubleOnNormalDistribution(sv.getX(), XY_VAR, rand);
            double deltaY = randDoubleOnNormalDistribution(sv.getY(), XY_VAR, rand);
            moveParticle(particle, deltaX, deltaY);
        }
        stepCountForStepEstimation += 1;
    }

    private void moveParticle(Particle particle, double deltaX, double deltaY) {
        Point moveToPoint = particle.getCurrent().deepClone().shift(deltaX, deltaY);
        boundary.ensureInside(moveToPoint);
        particle.setCurrent(moveToPoint);
    }

    /**
     * update particles' position by a stepVector list
     */
    @Override
    protected long updatePosition(Deque<StepVector> deltaList) {
        long stepEndTime = 0;
        for (StepVector s : deltaList) {
            updatePosition(s);
            stepEndTime = s.getTimestamp();
        }
        return stepEndTime;
    }

    /**
     * before this, some time-matching vectors have taken place.
     * update position when wifi scanned; to make up for the step vector's move.
     * span=-1, find no responding vector;
     * span=0, wifi scans in exactly the step timestamp;
     */
    private void moveByTimeDiff(long span) {
        if (span <= 0) {
            return;
        }

        double meanDist = span / 10000000.0 * 1;
        stepCountForStepEstimation += meanDist / 0.8;

        Random rand = new Random(1);

        for (Particle particle : particles) {
            // Estimate the range of possible positions
            double deltaX = randDoubleOnNormalDistribution(0, meanDist, rand);
            double deltaY = randDoubleOnNormalDistribution(0, meanDist, rand);
            moveParticle(particle, deltaX, deltaY);
        }
    }

    /**
     * http://stackoverflow.com/questions/218060/random-gaussian-variables
     * This is called Box-Muller transform
     */
    private double randDoubleOnNormalDistribution(double mean, double stddev, Random rand) {
        double u1 = rand.nextDouble(); //these are uniform(0,1) random doubles
        double u2 = rand.nextDouble();
        double randStdNormal = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2); //random normal(0,1)
        return mean + stddev * randStdNormal; //random normal(mean,stddev^2)
    }

    /**
     * for each weighters in the list:
     * find corresponding vectors,
     * update position,
     * and update weight.
     *
     * @param weighters the weighters list
     */
    @Override
    protected void updateWeight(Deque<IParticleWeighter> weighters) {
        updateParticle(weighters);
        updateParticleViaWeighter(getMapWeighter());
        estimateStepLength();
    }

    private void updateParticle(Deque<IParticleWeighter> weighters) {
        for (IParticleWeighter particleWeighter : weighters) {
            if (latestStepTimestamp != 0) {
                long span = particleWeighter.getTimestamp() - latestStepTimestamp;
                moveByTimeDiff(span);
            }
            latestStepTimestamp = particleWeighter.getTimestamp();
            //update the locating position

            //update weighters invoking WeightParticle
            updateParticleViaWeighter(particleWeighter);
        }
    }

    private void updateParticleViaWeighter(IParticleWeighter mapWeighter) {
        for (Particle particle : particles) {
            particle.setWeight(particle.getWeight() * mapWeighter.WeightParticle(particle.getLast(), particle.getCurrent()));
        }
        reSample();
    }

    public Point getEstimatedPosition() {
        return estimatedPosition;
    }

    private void estimateStepLength() {
        Point lastPosition = this.estimatedPosition;
        this.estimatedPosition = calcMostPossiblePosition();
        distanceForStepEstimation += Point.minus(lastPosition, estimatedPosition).getLength();

        if (distanceForStepEstimation > DISTANCE_THRESHOLD && stepCountForStepEstimation > 0) {
            this.estimatedStepLength = (this.estimatedStepLength + distanceForStepEstimation / stepCountForStepEstimation) * 0.5 / 1.1;
            this.estimatedStepLength = Math.min(0.9, this.estimatedStepLength);
            //Logger.v(TAG, "Estimated step length: " + this.estimatedStepLength);
            distanceForStepEstimation = 0;
            stepCountForStepEstimation = 0;
        }
    }

    /**
     * reSample the particles
     */
    @Override
    protected void reSample() {
        double[] weights = new double[PARTICLE_NUMBER];
        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            weights[i] = particles[i].getWeight();
        }

        discreteSample(weights, PARTICLE_NUMBER);

        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            int index = indexOfParticles[i];
            newParticles[i].copyFrom(particles[index]);
        }

        double totalWeight = 0;
        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            totalWeight += newParticles[i].getWeight();
        }

        for (int i = 0; i < PARTICLE_NUMBER; i++) {
            particles[i].copyFrom(newParticles[i]);
            particles[i].setWeight(particles[i].getWeight() / totalWeight);
        }
    }

    private void discreteSample(double[] weights, int particleNum) {
        double weightSum = 0.0;
        for (double weight : weights) {
            weightSum += weight;
        }

        //normalization and get new weight
        for (int i = 0; i < particleNum; i++) {
            weights[i] /= weightSum;
        }

        // accumulate 0 --> i-1 weights in weightAccumulator[i]
        double[] weightAccumulator = new double[particleNum];
        for (int i = 1; i < particleNum; i++) {
            weightAccumulator[i] = weightAccumulator[i - 1] + weights[i - 1];
        }

        for (int i = 0; i < particleNum; i++) {
            double largerThan = random.nextDouble();
            indexOfParticles[i] = binarySearch(weightAccumulator, 0, particleNum - 1, largerThan);
        }
    }

    private int binarySearch(double[] arr, int low, int high, double searchKey) {
        int mid;
        while (low < high) {
            mid = (low + high) / 2;
            if (arr[mid] == searchKey) {
                high = mid;
            } else {
                if (arr[mid] < searchKey) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return low;
    }

    /**
     * calculate the mean position of all the particles
     * get the weighted mean value of all the particles, the result serves as the estimated position.
     *
     * @return estimated position
     */
    public final Point calcMostPossiblePosition() {
        Point possiblePosition = new Point(0, 0);
        for (Particle particle : particles) {
            possiblePosition.shift(particle.getCurrent().scale(particle.getWeight()));
        }

        return possiblePosition;
    }

    /**
     * get all the particles of the filter.
     *
     * @return all the particles
     */
    public final Particle[] getParticles() {
        return particles;
    }

    public double getEstimatedStepLength() {
        return estimatedStepLength;
    }
}