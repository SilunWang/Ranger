package mass.Ranger.Data.Location;

import mass.Ranger.Algorithm.Localization.Point;

public interface IBoundary {
    Point ensureInside(Point point);

    boolean include(Point point);

    Point randomPosition();
}
