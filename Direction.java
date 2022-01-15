import java.awt.Point;

public enum Direction {
    N, E, S, W;

    public Point asOffset() {
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
