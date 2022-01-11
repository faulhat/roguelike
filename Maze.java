import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.awt.Point;

/*
 * Thomas: This is a class for generating mazes.
 * This will be used for the procedural level generation.
 */
public class Maze {
    public static enum Direction {
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

        // Guarantee that two WallRef objects are only equal if they are identical.
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
        
        // Initialize the matrix of cells such that all neighbors share walls
        cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = cells[i][j] = new Cell();

                if (i > 0) {
                    cell.walls.put(Direction.W, cells[i - 1][j].walls.get(Direction.E));
                }

                if (j > 0) {
                    cell.walls.put(Direction.N, cells[i][j - 1].walls.get(Direction.S));
                }

                if (i < width - 1) {
                    cell.walls.put(Direction.E, new WallRef());
                }

                if (j < height - 1) {
                    cell.walls.put(Direction.S, new WallRef());
                }
            }
        }
    }

    public Maze(int width, int height, Cell[][] cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    // Render the maze as a matrix of booleans where false means walkable and true means non-walkable
    public boolean[][] render() {
        boolean[][] grid = new boolean[width * 2 - 1][height * 2 - 1];
        for (boolean[] row : Arrays.asList(grid)) {
            Arrays.fill(row, false);
        }

        Set<WallRef> rendered = new HashSet<>(); // We only want to render each wall once.
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cellAt = cells[i][j];
                for (Direction direction : Direction.values()) {
                    WallRef wallRef = cellAt.walls.get(direction);
                    if (wallRef != null && !rendered.contains(wallRef) && wallRef.isWall) {
                        Point offset = direction.asOffset();
                        grid[i * 2 + offset.x][j * 2 + offset.y] = true;
                    }
                }
            }
        }

        // Wall corners and intersections should also be unwalkable.
        for (int i = 1; i < width * 2 - 2; i++) {
            for (int j = 1; j < height * 2 - 2; j++) {
                if (!grid[i][j]) {
                    if (grid[i - 1][j] || grid[i + 1][j]) {
                        if (grid[i][j - 1] || grid[i][j + 1]) {
                            grid[i][j] = true;
                        }
                    }
                }
            }
        }

        return grid;
    }

    @Override
    public String toString() {
        boolean[][] boolGrid = render();

        char[][] charGrid = new char[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (boolGrid[i][j]) {
                    charGrid[j][i] = '*';
                }
                else {
                    charGrid[j][i] = ' ';
                }
            }
        }

        String out = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out += charGrid[i][j];
            }

            out += '\n';
        }

        return out;
    }

    public void divide(Random rand) {
        if (width != 1 && height != 1) {
            // Make a vertical wall and a horizontal wall
            int slice_x = rand.nextInt(width - 1) + 1, // position of vertical wall
                slice_y = rand.nextInt(height - 1) + 1; // position of horizontal wall
            
            System.out.println("slice_x: " + slice_x + ", slice y: " + slice_y);

            // Create the horizontal wall
            for (int i = 0; i < width; i++) {
                cells[i][slice_y].walls.get(Direction.N).isWall = true;
            }

            // Create the vertical wall
            for (int i = 0; i < height; i++) {
                cells[slice_x][i].walls.get(Direction.W).isWall = true;
            }
            
            // Create openings in three of the arms of the cross we just made
            ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));
            directions.remove(rand.nextInt(directions.size()));

            System.out.println(toString());
            System.out.println();
            for (Direction direction : directions) {
                int breakAt;
                switch (direction) {
                case N:
                    breakAt = rand.nextInt(slice_x);
                    cells[breakAt][slice_y].walls.get(Direction.W).isWall = false;
                    break;
                case E:
                    breakAt = rand.nextInt(height - slice_y) + slice_y;
                    cells[slice_x][breakAt].walls.get(Direction.N).isWall = true;
                    break;
                case S:
                    breakAt = rand.nextInt(width - slice_x) + slice_x;
                    cells[breakAt][slice_y].walls.get(Direction.W).isWall = false;
                    break;
                case W:
                    breakAt = rand.nextInt(slice_y);
                    cells[slice_x][breakAt].walls.get(Direction.N).isWall = false;
                }
            }

            Cell[][] NEQuadrant = new Cell[width - slice_x][slice_y];
            Cell[][] SEQuadrant = new Cell[width - slice_x][height - slice_y];
            for (int i = slice_x; i < width; i++) {
                NEQuadrant[i] = Arrays.copyOfRange(cells[i], 0, slice_y);
                SEQuadrant[i] = Arrays.copyOfRange(cells[i], slice_y, height);
            }
            Maze NERegion = new Maze(width - slice_x, slice_y, NEQuadrant);
            Maze SERegion = new Maze(width - slice_x, height - slice_y, SEQuadrant);

            Cell[][] SWQuadrant = new Cell[slice_x][height - slice_y];
            Cell[][] NWQuadrant = new Cell[slice_x][slice_y];
            for (int i = 0; i < slice_x; i++) {
                SWQuadrant[i] = Arrays.copyOfRange(cells[i], height, slice_y);
                NWQuadrant[i] = Arrays.copyOfRange(cells[i], 0, slice_y);
            }
            Maze SWRegion = new Maze(slice_x, height - slice_y, SWQuadrant);
            Maze NWRegion = new Maze(slice_x, slice_y, NWQuadrant);

            NERegion.divide(rand);
            SERegion.divide(rand);
            SWRegion.divide(rand);
            NWRegion.divide(rand);
        }
    }

    public void genMaze() {
        Random rand = new Random(System.currentTimeMillis());

        divide(rand);
    }

    public static void main(String[] args) {
        Maze maze = new Maze(10, 6);
        System.out.println(maze.toString());
        System.out.println();
        maze.genMaze();

        System.out.println(maze.toString());
    }
}
