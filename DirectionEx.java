import java.awt.geom.Point2D;
import java.awt.Point;
import java.util.EnumSet;

/*
 * Thomas: An extension of the direction enum that allows for diagonal directions.
 */
public class DirectionEx {
    // x and y of the point on a unit circle at pi/4 radians
    public static final double DIAG_INTERVAL = Math.sqrt(2.0) / 2.0;

    private EnumSet<Direction> directions;

    // Empty
    public DirectionEx()
    {
        directions = EnumSet.noneOf(Direction.class);
    }

    // Not diagonal
    public DirectionEx(Direction direction)
    {
        this();

        directions.add(direction);
    }

    // Should only be used for diagonals
    public DirectionEx(Direction direction_a, Direction direction_b)
    {
        this(direction_a);

        directions.add(direction_b);
    }

    public void add(Direction direction)
    {
        directions.add(direction);
    }

    public boolean isEmpty()
    {
        return directions.isEmpty();
    }

    // Get the offset for this compound direction
    // Analagous to Direction.asOffset()
    public Point2D.Double getOffset() throws Exception
    {

        // Only one direction
        if (directions.size() == 1) {
            for (Direction direction : directions) {
                Point offset = direction.asOffset();

                return new Point2D.Double((double) offset.x, (double) offset.y);
            }
        }

        // Two opposite directions
        if ((directions.contains(Direction.N) && directions.contains(Direction.S)) || (directions.contains(Direction.E) && directions.contains(Direction.W))) {
            return new Point2D.Double(0.0, 0.0);
        }

        // Diagonals. x and y offsets will both be cos(pi/4) (which is the value of the constant DIAG_INTERVAL) or -cos(pi/4).
        if (directions.contains(Direction.N)) {
            if (directions.contains(Direction.E)) {
                return new Point2D.Double(DIAG_INTERVAL, -DIAG_INTERVAL);
            }

            if (directions.contains(Direction.W)) {
                return new Point2D.Double(-DIAG_INTERVAL, -DIAG_INTERVAL);
            }
        }

        if (directions.contains(Direction.S)) {
            if (directions.contains(Direction.E)) {
                return new Point2D.Double(DIAG_INTERVAL, DIAG_INTERVAL);
            }

            if (directions.contains(Direction.W)) {
                return new Point2D.Double(DIAG_INTERVAL, -DIAG_INTERVAL);
            }
        }

        // Something has gone terribly wrong
        throw new Exception("WTF how did I get here?");
    }
}
