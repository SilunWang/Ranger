package mass.Ranger.Algorithm.DTW;

import mass.Ranger.Algorithm.DTW.FastDtw.dtw.FastDTW;
import mass.Ranger.Algorithm.DTW.FastDtw.dtw.TimeWarpInfo;
import mass.Ranger.Algorithm.DTW.FastDtw.timeseries.TimeSeries;
import mass.Ranger.Algorithm.DTW.FastDtw.timeseries.TimeSeriesPoint;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunction;
import mass.Ranger.Algorithm.DTW.FastDtw.util.DistanceFunctionFactory;

import java.util.ArrayList;

public class DTW {

    private static int DTWCoefficientForWidth = 50;//DTW矩阵通道宽度所占比例

    private static int FastDtwRadius = 10;

    enum DTWSource {Insertion, Deletion, Match}

    //计算两个序列的DTW距离
    public static double getDTWDistance(ArrayList<Double> array1, ArrayList<Double> array2) {
        if (array1.size() == 0 || array2.size() == 0)
            return 0;

        double res[][] = new double[array1.size() + 1][array2.size() + 1];
        DTWSource path[][] = new DTWSource[array1.size() + 1][array2.size() + 1];
        //初始化，填充正无穷
        for (int i = 0; i <= array1.size(); i++)
            for (int j = 0; j <= array2.size(); j++)
                res[i][j] = 1000000;
        res[0][0] = 0;

        //计算“中心区”训练文件第i-1个矢量到测试文件第j-1个矢量的距离
        double slope = (double) array2.size() / (double) array1.size();
        for (int i = 1; i <= array1.size(); i++)
            for (int j = Math.max(1, (int) ((i - array1.size() * DTWCoefficientForWidth / 100) * slope));
                 j <= Math.min(array2.size(), (int) ((i + array1.size() * DTWCoefficientForWidth / 100) * slope));
                 j++)
                res[i][j] = Math.abs(array1.get(i - 1) - array2.get(j - 1));

        //计算总距离
        for (int i = 1; i <= array1.size(); i++) {
            for (int j = 1; j <= array2.size(); j++) {
                if (res[i - 1][j] <= res[i - 1][j - 1] && res[i - 1][j] <= res[i][j - 1]) {
                    res[i][j] += res[i - 1][j];//insertion
                    path[i][j] = DTWSource.Insertion;
                } else if (res[i][j - 1] <= res[i - 1][j - 1] && res[i][j - 1] <= res[i - 1][j]) {
                    res[i][j] += res[i][j - 1];//deletion
                    path[i][j] = DTWSource.Deletion;
                } else {
                    res[i][j] += res[i - 1][j - 1];//match
                    path[i][j] = DTWSource.Match;
                }
            }
        }

        //统计路径长度
        int rows = array1.size();
        int columns = array2.size();
        int numOfSteps = 0;
        while (rows > 0 && columns > 0) {
            if (path[rows][columns] == DTWSource.Insertion)
                rows--;
            else if (path[rows][columns] == DTWSource.Deletion)
                columns--;
            else {
                rows--;
                columns--;
            }
            numOfSteps++;
        }

        //返回计算结果
        return res[array1.size()][array2.size()] / numOfSteps;
    }

    public static double getFastDtwDistance(ArrayList<Double> array1, ArrayList<Double> array2) {
        TimeSeries ts1 = new TimeSeries(1);
        TimeSeries ts2 = new TimeSeries(1);
        for (int i = 0; i < array1.size(); i++) {
            ts1.addLast(i, new TimeSeriesPoint(new double[]{array1.get(i)}));
        }
        for (int i = 0; i < array2.size(); i++) {
            ts2.addLast(i, new TimeSeriesPoint(new double[]{array2.get(i)}));
        }
        final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        final TimeWarpInfo info = FastDTW.getWarpInfoBetween(ts1, ts2, FastDtwRadius, distFn);
        return info.getDistance();
    }

    //get DTW distance between 2 multi dimensional data array
    public static double getMultiDimDtwDistance(ArrayList<DTWDataPoint> array1, int array1dim, ArrayList<DTWDataPoint> array2, int array2dim) {
        TimeSeries ts1 = new TimeSeries(array1dim);
        TimeSeries ts2 = new TimeSeries(array2dim);
        for (int i = 0; i < array1.size(); i++) {
            ts1.addLast(array1.get(i).timeStamp, new TimeSeriesPoint(array1.get(i).data));
        }
        for (int i = 0; i < array2.size(); i++) {
            ts2.addLast(array2.get(i).timeStamp, new TimeSeriesPoint(array2.get(i).data));
        }
        final DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        final TimeWarpInfo info = FastDTW.getWarpInfoBetween(ts1, ts2, FastDtwRadius, distFn);
        return info.getDistance();
    }
}
