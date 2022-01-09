import java.util.Map;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Point;

public class Maze {
    public static enum Direction {
        N, E, S, W;

        public Point asOffset() {
            switch (this) {
            case N:
                return new Point(-1, 0);
            case E:
                return new Point(0, 1);
            case S:
                return new Point(1, 0);
            default:
                return new Point(0, -1);
            }
        }
    }

    // An object specifying whether or not a wall exists between two cells.
    // Two Cell objects should share each wall.
    public static class WallRef {
        private static AtomicInteger tracker = new AtomicInteger();

        public boolean isWall;

        public final int id;

        public WallRef(boolean isWall) {
            this.isWall = isWall;

            id = tracker.getAndIncrement();
        }

        public WallRef() {
            this(false);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof WallRef && id == ((WallRef) other).id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
    
    // A cell in the maze. Can have a wall on each side.
    public static class Cell {
        public EnumMap<Direction, WallRef> walls;

        public Cell() {
            this.walls = new EnumMap<>(Direction.class);
        
            for (Direction direction : Direction.values()) {
                this.walls.put(direction, new WallRef());
            }
        }

        public Cell(Map<Direction, WallRef> walls) {
            this.walls = new EnumMap<>(walls);
        }
    }

    public final int width, height;

    private Cell[][] cells;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
    }

    public boolean[][] render() {
        boolean[][] grid = new boolean[width * 2][height * 2];
        for (boolean[] row : Arrays.asList(grid)) {
            Arrays.fill(row, false);
        }

        Set<WallRef> rendered = new HashSet<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cellAt = cells[i][j];
                for (Direction direction : Direction.values()) {
                    WallRef wallRef = cellAt.walls.get(direction);
                    if (!rendered.contains(wallRef) && wallRef.isWall) {
                        Point offset = direction.asOffset();
                        grid[i * 2 + offset.x][j * 2 + offset.y] = true;
                    }
                }
            }
        }

        return grid;
    }

    public void genMaze() {
        
    }
}
