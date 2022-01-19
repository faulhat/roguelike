import java.awt.Point;

/*
 * Thomas: an enumeration representing directions on a grid
 * N = up, E = right, S = down, W = left.
 */
public enum Direction {
    N, E, S, W;

    // Get this direction as an offset.
    // position + offset should be the point you'd get to moving 1 unit in this direction away from position.
    public Point asOffset()
    {
        switch (this) {
        case N:
            return new Point(0, -1);
        case E:
            return new Point(1, 0);
        case S:
            return new Point(0, 1);
        default:
            return new Point(-1, 0);
        }
    }
}
