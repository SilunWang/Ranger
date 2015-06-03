package mass.Ranger.Algorithm.StepDetection;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * <summary>
 * <para>This class is used as a slide window that calculate the
 * dot product between an predefined coefficients and the most recently
 * datas.</para>
 * <para>Author: chhuang@microsoft.com</para>
 * </summary>
 * <p/>
 * <example>
 * <para>If we have an <c>coefficients</c> array: [1, 0, 1]</para>
 * <para>and data stream are: 1, 2, 3, 0, -1, -2, -3
 * (each data is sent to this class by calling <c>filterNewData</c>)</para>
 * <p/>
 * <para></para>
 * <para>The result flow should be: 1, 1, 4, 2, 2, -2, -4</para>
 * </example>
 */
public class SlidingWindowFilter {
    private Double[] coefficients;
    private LinkedList<Double> slidingWindow;

    public SlidingWindowFilter(Double[] coefficients) {
        this.coefficients = Arrays.copyOf(coefficients, coefficients.length);

        this.slidingWindow = new LinkedList<Double>();
    }

    public double filterNewData(double newestData) {
        this.maintainSlideWindow(newestData);

        // Calculate dot product
        double dotProduct = 0;
        int index = 0;
        for (Double value : this.slidingWindow) {
            dotProduct += value * this.coefficients[index];
            index++;
        }

        return dotProduct;
    }

    private void maintainSlideWindow(double newestData) {
        if (this.slidingWindow.size() == this.coefficients.length) {
            this.slidingWindow.removeFirst();
        }

        this.slidingWindow.addLast(newestData);
    }
}
