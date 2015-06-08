package mass.Ranger.Algorithm.ParticleFilter;

import mass.Ranger.Algorithm.ParticleWeighter.IParticleWeighter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class IParticleFilter {
    private static final String TAG = IParticleFilter.class.getName();
    //locks for mutual exclusion.
    private final static Object LOCK_PARTICLE_WEIGHTER = new Object();
    private final static Object LOCK_PARTICLE_DELTA = new Object();
    private final static Object LOCK_PARTICLES = new Object();
    private final static int WEIGHTER_BUFFER_SIZE = 1000;
    protected long latestStepTimestamp = 0;
    private Queue<IParticleWeighter> particleWeighters = new ConcurrentLinkedQueue<IParticleWeighter>();
    private Queue<StepVector> particleDelta = new ConcurrentLinkedQueue<StepVector>();
    private IParticleWeighter mapWeighter;

    public IParticleWeighter getMapWeighter() {
        return mapWeighter;
    }

    public void setMapWeighter(IParticleWeighter mapWeighter) {
        this.mapWeighter = mapWeighter;
    }

    public final void addRelativeMovement(StepVector delta) {
        synchronized (LOCK_PARTICLE_DELTA) {
            int removeCount = particleDelta.size() - WEIGHTER_BUFFER_SIZE + 1;
            if (removeCount > 0) {
                //Logger.w(TAG, "Queue is fulfilled, remove old deltas");
                for (int i = 0; i < removeCount; i++) {
                    particleDelta.remove();
                }
            }
            particleDelta.offer(delta);
        }

        if (particleDelta.size() > 0) {
            process();
        }
    }

    public final void addParticleWeighter(IParticleWeighter update) {
        synchronized (LOCK_PARTICLE_WEIGHTER) {
            int removeCount = particleWeighters.size() - WEIGHTER_BUFFER_SIZE + 1;
            if (removeCount > 0) {
                //Logger.w(TAG, "Queue is fulfilled, remove old weighters");
                for (int i = 0; i < removeCount; i++) {
                    particleWeighters.remove();
                }
            }
            particleWeighters.offer(update);
        }
        if (particleWeighters.size() > 0) {
            process();
        }
    }

    /**
     * The thread callback function for one iteration
     *
     * @return true  - successfully processed one iteration
     * false - nothing to process, a good indicator for sleeping to the caller
     */
    public final boolean process() {
        moveByDelta();
        return moveByWeighters();
    }

    private boolean moveByWeighters() {
        Deque<IParticleWeighter> weighters = new LinkedList<IParticleWeighter>();

        synchronized (LOCK_PARTICLE_WEIGHTER) {
            weighters.addAll(particleWeighters);
            particleWeighters.clear();
        }

        synchronized (LOCK_PARTICLES) {
            updateWeight(weighters);
        }

        return true;
    }

    public final boolean moveByDelta() {
        Deque<StepVector> delta = new LinkedList<StepVector>();
        synchronized (LOCK_PARTICLE_DELTA) {
            delta.addAll(particleDelta);
            particleDelta.clear();
        }

        if (delta.isEmpty()) {
            return false;
        }

        synchronized (LOCK_PARTICLES) {
            long timestamp = updatePosition(delta);
            if (timestamp > 0) {
                latestStepTimestamp = timestamp;
            }
        }

        return true;
    }

    protected abstract void updateWeight(Deque<IParticleWeighter> weighter);

    protected abstract long updatePosition(Deque<StepVector> deltaList);

    protected abstract void reSample();

}