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
        directions = EnumSet.of(direction);
    }

    // Should only be used for diagonals
    public DirectionEx(Direction direction_a, Direction direction_b)
    {
        this(direction_a);

        add(direction_b);
    }

    public void add(Direction direction)
    {
        Direction opposite = direction.getOpposite();
        if (directions.contains(opposite)) {
            directions.remove(opposite);
        }
        else {
            directions.add(direction);
        }
    }

    public boolean isEmpty()
    {
        return directions.isEmpty();
    }

    public boolean contains(Direction direction)
    {
        return directions.contains(direction);
    }

    @Override
    public boolean equals(Object other)
    {
        // this.equals(other) if this and other contain the same directions

        if (!(other instanceof DirectionEx)) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            if (((DirectionEx) other).contains(direction) != contains(direction)) {
                return false;
            }
        }

        return true;
    }

    // Get the offset for this compound direction
    // Analagous to Direction.asOffset()
    // Note: it should be impossible for there to be two opposite directions in this.directions
    public Point2D.Double getOffset() throws Exception
    {
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
                return new Point2D.Double(-DIAG_INTERVAL, DIAG_INTERVAL);
            }
        }

        // If there is only one Direction in directions
        if (directions.size() == 1) {
            for (Direction direction : directions) {
                Point intOffset = direction.asOffset();
                return new Point2D.Double((double) intOffset.x, (double) intOffset.y);
            }
        }

        // directions must be empty if we've gotten here.
        return new Point2D.Double(0.0, 0.0);
    }
}
