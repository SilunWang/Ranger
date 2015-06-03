package mass.Ranger.Algorithm.StepDetection;

import mass.Ranger.Util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StepDetector {
    private final static int MaxStepCacheCount = 2;
    private final static float MaxStepDistance = 12f;
    private final static double MinimumTickElapseForFirstStep = 0.05 * Utils.TicksInOneSecond;
    private final static double MinimumTickElapseForNormalStep = 0.12 * Utils.TicksInOneSecond;
    private final static Double[] MeanFilterCoefficients = new Double[]{0.25, 0.25, 0.25, 0.25};
    private SlidingWindowFilter[] accMeanFilter;
    private int counterThreshold;
    private double AccelerationThreshold;
    private SlidingWindowFilterBasedOnSampleRate firFilter;
    /**
     * acceleration data since previousMinCounter (data for at most two step)
     */
    private List<Float> accData;
    /**
     * ticks ever since previousMinCounter
     */
    private List<Long> accTicks;
    private List<Double> headingData;
    private boolean isInAccelerationCurveValley = true;
    private float maxAccelerationSinceStatusChanged = -999999;
    /**
     * Corresponding sample counter when the acceleration
     * reach maximum ever since the end of the last peak status
     */
    private int maxCounterSinceStatusChanged = 0;
    private float minAccelerationSinceStatusChanged = 999999;
    /**
     * Corresponding sample counter when the acceleration
     * reach minimum ever since the end of the last valley status
     */
    private int minCounterSinceStatusChanged = 0;
    /**
     * Previous minCounterSinceLastValley
     */
    private int previousMinCounter = 0;
    private int sampleCounter = 0;
    private int stepNumber = 0;
    /**
     * How many step candidates ever since the last stop
     */
    private int stepsInSeries = 0;
    private List<StepCandidate> stepWaitingList;
    private List<StepEventListener> stepEventListeners;
    private float maxAccDataForThisStep;
    private float minAccDataForThisStep;

    public StepDetector(int sampleRateInHz) {
        this(sampleRateInHz, 0.05);
        //init();
    }

    public void init() {
        previousMinCounter = 0;
        sampleCounter = 0;
        stepNumber = 0;
        stepsInSeries = 0;
        minCounterSinceStatusChanged = 0;
        maxCounterSinceStatusChanged = 0;
        stepWaitingList = new ArrayList<StepCandidate>();
        stepEventListeners = new ArrayList<StepEventListener>();
        accData = new ArrayList<Float>();
        accTicks = new ArrayList<Long>();
        headingData = new ArrayList<Double>();
    }

    public StepDetector(int sampleRateInHz, double accelerationThreshold) {
        int sampleFactor = 250 / sampleRateInHz;
        this.counterThreshold = 30 / sampleFactor;
        this.AccelerationThreshold = accelerationThreshold;

        this.accMeanFilter = new SlidingWindowFilter[]
                {
                        new SlidingWindowFilter(MeanFilterCoefficients),
                        new SlidingWindowFilter(MeanFilterCoefficients),
                        new SlidingWindowFilter(MeanFilterCoefficients),
                };
        this.firFilter = new SlidingWindowFilterBasedOnSampleRate(sampleRateInHz);
        this.init();
        this.initialize();
    }

    /**
     * Calculate the distance between the two given float lists.
     * <para>Name this two list as a1, a2, ... an and b1, b2, ... bm,
     * this method return dist(an, bm) where</para>
     * <para>dist(ai, bj) = |ai - bj| + min(dist(a(i-1), bj), dist(ai, b(j-1)), dist(a(i-1), b(j-1)))
     */
    private static float dynamicWarping(List<Float> data, List<Float> reference) {
        int n = reference.size();
        int m = data.size();
        float infinite = 999999;

        float[][] costs = new float[n + 1][m + 1];
        for (int i = 1; i <= m; i++) {
            costs[0][i] = infinite;
        }

        for (int i = 1; i <= n; i++) {
            costs[i][0] = infinite;
        }

        costs[0][0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                float cost = Math.abs(data.get(j - 1) - reference.get(i - 1));
                costs[i][j] = cost + Math.min(Math.min(costs[i - 1][j], costs[i][j - 1]), costs[i - 1][j - 1]);
            }
        }

        return costs[n][m];
    }

    public void detectStep(long ticks, float x, float y, float z, double heading) {
        float currentAcc = this.calculateAcceleration(x, y, z);

        // if there have been enough time (greater than 1s) no step detected since last valley,
        // that means the user stops, and previous cached steps should be reported.
        if (this.longEnoughSinceTheBeginningOfThisStep(ticks)) {
            this.throwCachedStep();

            // if it's really long (say 20 seconds, we should clean all the data)
            if (this.longEnoughToCleanData(ticks)) {
                this.initialize();
            }

            this.stepsInSeries = 0;
        }

        this.updateMaxMinUsingNewestData(currentAcc);
        this.cacheAccelerationData(currentAcc, ticks, heading);

        if (this.isInAccelerationCurveValley) {
            if (this.outOfAccelerationCurveValley(currentAcc)) {
                this.minAccelerationSinceStatusChanged = currentAcc;
                this.isInAccelerationCurveValley = false;

                if (this.isEnoughTickElapseForAStep()) {
                    StepCandidate step = this.createCurrentStepCandidate();
                    this.tryConfirmStepCandidate(step);
                    this.cleanupAccDataForLastStep();
                    this.updateMaxMinForValley(currentAcc);
                }
            }
        } else {
            if (this.outOfAccelerationCurvePeak(currentAcc)) {
                this.maxAccelerationSinceStatusChanged = currentAcc;
                this.isInAccelerationCurveValley = true;
                this.updateMaxMinForPeak(currentAcc);
            }
        }
        this.sampleCounter++;
    }

    private boolean longEnoughToCleanData(long ticks) {
        return this.accTicks != null
                && this.accTicks.size() > 0
                && ticks - this.accTicks.get(0) > 20 * Utils.TicksInOneSecond;
    }

    public void addStepEventListener(StepEventListener listener) {
        synchronized (stepEventListeners.getClass()) {
            stepEventListeners.add(listener);
        }
    }

    private void initialize() {
        this.stepsInSeries = 0;
        this.accData.clear();
        this.accTicks.clear();
        this.headingData.clear();
        this.stepWaitingList.clear();
        this.maxAccelerationSinceStatusChanged = -999999;
        this.minAccelerationSinceStatusChanged = 999999;
        this.maxCounterSinceStatusChanged = 0;
        this.minCounterSinceStatusChanged = 0;
        this.previousMinCounter = 0;
        this.sampleCounter = 0;
        this.isInAccelerationCurveValley = true;
    }

    /// <summary>
    /// Longer than 2 seconds since last step
    /// </summary>
    /// <param name="ticks"></param>
    /// <returns></returns>
    private boolean longEnoughSinceTheBeginningOfThisStep(long ticks) {
        return this.accTicks != null
                && this.accTicks.size() > 0
                && ticks - this.accTicks.get(0) > Utils.TicksInOneSecond;
    }

    /// <summary>
    /// Report the first step in cache,
    /// while leaving the rest two steps (at most) to compare with on going steps
    /// </summary>
    private void throwCachedStep() {
        if (this.stepWaitingList.size() > 0) {
            StepCandidate step = this.stepWaitingList.remove(0);
            if (step.isValidStep()) {
                this.stepNumber++;
                for (StepEventListener listener : stepEventListeners) {
                    listener.onStepDetected(step);
                }
            }
        }
    }

    private void updateMaxMinForPeak(float currentAcc) {
        this.minAccelerationSinceStatusChanged = currentAcc;
        this.maxCounterSinceStatusChanged = this.sampleCounter;
    }

    private void updateMaxMinForValley(float currentAcc) {
        this.previousMinCounter = this.minCounterSinceStatusChanged;
        this.minCounterSinceStatusChanged = this.sampleCounter;
        this.maxAccelerationSinceStatusChanged = currentAcc;
    }

    private void cleanupAccDataForLastStep() {
        final int index = this.numberOfAccelerationDataForLastStep();
        final int end = this.sampleCounter - this.previousMinCounter;
        this.accData = new ArrayList<Float>(this.accData.subList(index, end));
        maxAccDataForThisStep = Collections.max(this.accData);
        minAccDataForThisStep = Collections.min(this.accData);
        this.accTicks = new ArrayList<Long>(this.accTicks.subList(index, end));
        this.headingData = new ArrayList<Double>(this.headingData.subList(index, end));
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private StepCandidate createCurrentStepCandidate() {
        final int totalCount = this.numberOfAccelerationDataForLastStep();
        List<Float> stepAccData = new ArrayList<Float>(totalCount);
        List<Double> headings = new ArrayList<Double>(totalCount);
        for (int i = 0; i < totalCount; i++) {
            stepAccData.add(this.accData.get(i));
            headings.add(this.headingData.get(i));
        }

        StepCandidate curStep = new StepCandidate(
                this.accTicks.get(0),
                this.accTicks.get(totalCount),
                Utils.averageDegree(headings),
                stepAccData);

        return curStep;
    }

    private boolean isEnoughTickElapseForAStep() {
        long tickElapse = this.accTicks.get(this.numberOfAccelerationDataForLastStep())
                - this.accTicks.get(0);

        // Normal step
        if (tickElapse > MinimumTickElapseForNormalStep) {
            return true;
        }

        // First step
        return this.stepNumber == 0 && tickElapse > MinimumTickElapseForFirstStep;
    }

    private int numberOfAccelerationDataForLastStep() {
        return this.minCounterSinceStatusChanged - this.previousMinCounter;
    }

    private boolean outOfAccelerationCurvePeak(float currentAcc) {
        return currentAcc < this.maxAccelerationSinceStatusChanged - AccelerationThreshold
                && this.sampleCounter > this.maxCounterSinceStatusChanged + this.counterThreshold;
    }

    private boolean outOfAccelerationCurveValley(float currentAcc) {
        return currentAcc > this.minAccelerationSinceStatusChanged + AccelerationThreshold
                && this.sampleCounter > this.minCounterSinceStatusChanged + this.counterThreshold
                && (maxAccDataForThisStep - minAccDataForThisStep) > this.AccelerationThreshold * 4
//                && (Collections.max(this.accData) - Collections.min(this.accData)) > this.AccelerationThreshold * 4
                ;
    }

    /**
     * Add the newest arrival data into cache
     *
     * @param currentAcc newest arrival acceleration
     * @param ticks      corresponding ticks
     * @param heading    compass reading
     */
    private void cacheAccelerationData(float currentAcc, long ticks, double heading) {
        this.accData.add(currentAcc);
        maxAccDataForThisStep = Math.max(maxAccDataForThisStep, currentAcc);
        minAccDataForThisStep = Math.min(minAccDataForThisStep, currentAcc);
        this.accTicks.add(ticks);
        this.headingData.add(heading);
    }

    private void updateMaxMinUsingNewestData(float currentAcc) {
        if (this.maxAccelerationSinceStatusChanged < currentAcc) {
            this.maxAccelerationSinceStatusChanged = currentAcc;
            this.maxCounterSinceStatusChanged = this.sampleCounter;
        }

        if (this.minAccelerationSinceStatusChanged > currentAcc) {
            this.minAccelerationSinceStatusChanged = currentAcc;
            this.minCounterSinceStatusChanged = this.sampleCounter;
        }
    }

    private float calculateAcceleration(float x, float y, float z) {
        // mean filter on all currentAcc samples
        double accX = this.accMeanFilter[0].filterNewData(x);
        double accY = this.accMeanFilter[1].filterNewData(y);
        double accZ = this.accMeanFilter[2].filterNewData(z);

        float rawAcc = (float) Math.sqrt((accX * accX) + (accY * accY) + (accZ * accZ));

        // FIR filtering
        return (float) this.firFilter.filterNewData(rawAcc);
    }

    private void tryConfirmStepCandidate(StepCandidate curStep) {
        this.stepWaitingList.add(curStep);
        this.stepsInSeries++;
        if (this.stepWaitingList.size() == MaxStepCacheCount) {
            StepCandidate refStep = this.stepWaitingList.get(0);

            // specific algorithm for the first step:
            // if there is consecutive 3 steps,
            // then the 1st one should be a real step
            if (this.stepsInSeries == MaxStepCacheCount) {
                refStep.confirmValidate();
            }

            float distance = this.calculateDistanceBetweenTwoStep(curStep, refStep);

            if (distance < MaxStepDistance) {
                curStep.confirmValidate();
                refStep.confirmValidate();
            }

            if (refStep.isValidStep()) {
                this.handleNewStep(refStep);

                // rule: if one peak exists between 2 steps,
                // then it is also a real step.
                if (curStep.isValidStep()) {
                    this.stepWaitingList.get(1).confirmValidate();
                }
            }

            this.stepWaitingList.remove(0);
        }
    }

    private void handleNewStep(StepCandidate refStep) {
        this.stepNumber++;
        refStep.setStepNumber(this.stepNumber);
        for (StepEventListener listener : stepEventListeners) {
            listener.onStepDetected(refStep);
        }
    }

    private float calculateDistanceBetweenTwoStep(StepCandidate curStep, StepCandidate refStep) {
        List<Float> refAccData = refStep.getAccData();
        float accMin = Collections.min(curStep.getAccData());
        float refAccMin = Collections.min(refAccData);
        float maxDiff = Collections.max(curStep.getAccData()) - accMin;
        float maxDiffPrev = Collections.max(refAccData) - refAccMin;

        // Normalization
        List<Float> normAccData = new ArrayList<Float>(curStep.getAccData().size());
        for (Float f : curStep.getAccData()) {
            normAccData.add((f - accMin) / maxDiff);
        }
        List<Float> normRefAccData = new ArrayList<Float>(refAccData.size());
        for (Float f : refAccData) {
            normRefAccData.add((f - refAccMin) / maxDiffPrev);
        }

        return dynamicWarping(normAccData, normRefAccData);
    }
}
