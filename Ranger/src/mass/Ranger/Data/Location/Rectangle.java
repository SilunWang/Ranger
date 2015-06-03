package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;
import mass.Ranger.Algorithm.Localization.Point;
import mass.Ranger.Util.Range;

@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class Rectangle implements IBoundary {
    private final static String TAG = Rectangle.class.getName();
    @SerializedName("MinX")
    private final double minX;
    @SerializedName("MaxX")
    private final double maxX;
    @SerializedName("MinY")
    private final double minY;
    @SerializedName("MaxY")
    private final double maxY;
    @SerializedName("__type")
    private final String __type = "Rectangle:#iNav.Modeling.Common";
    private Range rangeX;
    private Range rangeY;

    public Rectangle(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.rangeX = new Range(minX, maxX);
        this.rangeY = new Range(minY, maxY);
    }

    @Override
    public Point ensureInside(Point point) {
        point.setX(rangeX.ensureInRange(point.getX()));
        point.setY(rangeX.ensureInRange(point.getY()));
        return point;
    }

    @Override
    public boolean include(Point point) {
        return rangeX.inside(point.getX()) && rangeY.inside(point.getY());
    }

    @Override
    public Point randomPosition() {
        return new Point(rangeX.randomValue(), rangeY.randomValue());
    }


}
