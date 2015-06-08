/*
 * DtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package mass.Ranger.Algorithm.DTW.FastDtw;

import mass.Ranger.Algorithm.DTW.FastDtw.dtw.DTW;
import mass.Ranger.Algorithm.DTW.FastDtw.dtw.TimeWarpInfo;
import mass.Ranger.Algorithm.DTW.FastDtw.timeseries.TimeSeries;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunction;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunctionFactory;


public class DtwTest {

    // PUBLIC FUNCTIONS
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.out.println("USAGE:  java DtwTest timeSeries1 timeSeries2 [EuclideanDistance|ManhattanDistance|BinaryDistance]");
            System.exit(1);
        } else {
            final TimeSeries tsI = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ = new TimeSeries(args[1], false, false, ',');

            final DistanceFunction distFn;
            if (args.length < 3) {
                distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
            } else {
                distFn = DistanceFunctionFactory.getDistFnByName(args[2]);
            }   // end if

            final TimeWarpInfo info = DTW.getWarpInfoBetween(tsI, tsJ, distFn);

            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
        }  // end if

    }  // end main()

}  // end class DtwTest
