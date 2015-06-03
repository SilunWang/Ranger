package mass.Ranger.Util;

public class Random {
    //
    // Private Constants
    //
    private final static int MBIG = Integer.MAX_VALUE;
    private final static int MSEED = 161803398;
    //
    // Member Variables
    //
    private int inext;
    private int inextp;
    private int[] SeedArray = new int[56];

    public Random(int Seed) {
        int ii;
        int mj, mk;

        //Initialize our Seed array.
        //This algorithm comes from Numerical Recipes in C (2nd Ed.)
        int subtraction = (Seed == Integer.MIN_VALUE) ? Integer.MAX_VALUE : Math.abs(Seed);
        mj = MSEED - subtraction;
        SeedArray[55] = mj;
        mk = 1;
        for (int i = 1; i < 55; i++) {  //Apparently the range [1..55] is special (Knuth) and so we're wasting the 0'th position.
            ii = (21 * i) % 55;
            SeedArray[ii] = mk;
            mk = mj - mk;
            if (mk < 0) mk += MBIG;
            mj = SeedArray[ii];
        }
        for (int k = 1; k < 5; k++) {
            for (int i = 1; i < 56; i++) {
                SeedArray[i] -= SeedArray[1 + (i + 30) % 55];
                if (SeedArray[i] < 0) SeedArray[i] += MBIG;
            }
        }
        inext = 0;
        inextp = 21;
        Seed = 1;
    }

    //
    // Package Private Methods
    //
    double Sample() {
        //Including this division at the end gives us significantly improved
        //random number distribution.
        return (InternalSample() * (1.0 / MBIG));
    }

    private int InternalSample() {
        int retVal;
        int locINext = inext;
        int locINextp = inextp;

        if (++locINext >= 56) locINext = 1;
        if (++locINextp >= 56) locINextp = 1;

        retVal = SeedArray[locINext] - SeedArray[locINextp];

        if (retVal == MBIG) retVal--;
        if (retVal < 0) retVal += MBIG;

        SeedArray[locINext] = retVal;

        inext = locINext;
        inextp = locINextp;

        return retVal;
    }

    /*=====================================Next=====================================
    **Returns: An int [0..maxValue)
    **Arguments: maxValue -- One more than the greatest legal return value.
    **Exceptions: None.
    ==============================================================================*/
    public int next(int maxValue) {
        if (maxValue < 0) {
            throw new IndexOutOfBoundsException(maxValue + " < 0");
        }
        return (int) (Sample() * maxValue);
    }

    /**
     * @return A double [0..1)
     */
    public double nextDouble() {
        return Sample();
    }

}
