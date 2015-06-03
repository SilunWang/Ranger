package mass.Ranger.Algorithm.StepDetection;

import mass.Ranger.Util.Utils;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class StepCandidate {
    private final static int DefaultStepLengthInCM = 70;
    private static final float[] StepLengthParameters = {22.117268664605130f, 27.361173069881616f};
    /**
     * acceleration series for this step
     */
    private List<Float> accData;
    /**
     * End time for this step, in ticks
     */
    private long endTick;
    /**
     * Deviation from north, as with a compass reading.
     */
    private double headingInDegree;
    /**
     * Whether this candidate is a validate step or not
     */
    private boolean isValidStep = false;
    /**
     * Start time for this step, in ticks
     */
    private long startTick;
    /**
     * Frequence of this step
     */
    private float stepFrequency;
    private int stepNumber;

    protected StepCandidate(long startTick, long endTick, double direction, List<Float> accData) {
        this.startTick = startTick;
        this.endTick = endTick;
        this.accData = accData;
        this.headingInDegree = direction;
        this.stepFrequency = (float) Utils.TicksInOneSecond / (this.endTick - this.startTick);
    }

    public List<Float> getAccData() {
        return accData;
    }

    protected void setAccData(List<Float> accData) {
        this.accData = accData;
    }

    public long getEndTick() {
        return endTick;
    }

    protected void setEndTick(long endTick) {
        this.endTick = endTick;
    }

    public double getHeadingInDegree() {
        return headingInDegree;
    }

    protected void setHeadingInDegree(double headingInDegree) {
        this.headingInDegree = headingInDegree;
    }

    public boolean isValidStep() {
        return isValidStep;
    }

    public void confirmValidate() {
        this.isValidStep = true;
    }

    public long getStartTick() {
        return startTick;
    }

    protected void setStartTick(long startTick) {
        this.startTick = startTick;
    }

    public float getStepFrequency() {
        return stepFrequency;
    }

    protected void setStepFrequency(float stepFrequency) {
        this.stepFrequency = stepFrequency;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    protected void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    /// <summary>
    /// Return the length of the this step, in meter
    /// </summary>
    /// <returns></returns>
    public float getStepLengthInMeter() {
        float stepLength = (StepLengthParameters[0] * this.stepFrequency) + StepLengthParameters[1];

        if (this.stepNumber == 1) {
            stepLength = DefaultStepLengthInCM;
        }

        if (stepLength < 30 || stepLength > 110) {
            stepLength = DefaultStepLengthInCM;
        }

        return stepLength / 100.0f;
    }

    @Override
    public String toString() {
        return String.format(
                "%d: [ticks:%fs], [length:%fcm], ",
                this.stepNumber,
                (this.endTick - this.startTick) / Utils.TicksInOneSecond,
                this.getStepLengthInMeter());
    }
}
