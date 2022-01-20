import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.EnumSet;

/*
 * Thomas: This is a class for generating mazes.
 * This will be used for the procedural level generation.
 */
public class Maze {
    // An object specifying whether or not a wall exists between two cells.
    // Two Cell objects should share each wall.
    public static class WallRef {
        private static AtomicInteger tracker = new AtomicInteger();

        public boolean isWall;

        public final int id;

        public WallRef(boolean isWall)
        {
            this.isWall = isWall;

            id = tracker.getAndIncrement();
        }

        public WallRef()
        {
            this(false);
        }

        // Guarantee that two WallRef objects are only equal if they are identical.
        @Override
        public boolean equals(Object other)
        {
            return other instanceof WallRef && id == ((WallRef) other).id;
        }

        @Override
        public int hashCode()
        {
            return id;
        }
    }

    // A cell in the maze. Can have a wall on each side.
    public static class Cell {
        public EnumMap<Direction, WallRef> walls;

        public Cell()
        {
            this.walls = new EnumMap<>(Direction.class);
        }

        public Cell(Map<Direction, WallRef> walls)
        {
            this.walls = new EnumMap<>(walls);
        }
    }

    public final int width, height;

    private Cell[][] cells;

    // Should debug info be printed to the command line?
    private final boolean debug;

    public Maze(boolean debug, int width, int height)
    {
        this.debug = debug;

        this.width = width;
        this.height = height;

        // Initialize the matrix of cells such that all neighbors share walls
        cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = cells[i][j] = new Cell();

                // Walls should not be generated on the edges of the maze,
                // since then they would not be shared by two cells.
                // It is implicit that you can't walk over the edges.
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

    public Maze(boolean debug, int width, int height, Cell[][] cells)
    {
        this.debug = debug;
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    // Assume that debug info should not be printed.
    public Maze(int width, int height)
    {
        this(false, width, height);
    }

    public Maze(int width, int height, Cell[][] cells)
    {
        this(false, width, height, cells);
    }

    // Get an EnumSet of Directions representing the exits from a cell
    public EnumSet<Direction> getExits(int x, int y)
    {
        Cell cell = cells[x][y];

        EnumSet<Direction> exits = EnumSet.allOf(Direction.class);
        for (Direction direction : Direction.values()) {
            WallRef wallRef = cell.walls.get(direction);

            if (wallRef == null || wallRef.isWall) {
                exits.remove(direction);
            }
        }

        return exits;
    }

    // Render the maze as a matrix of booleans where false means walkable and true means non-walkable
    public boolean[][] render()
    {
        boolean[][] grid = new boolean[width * 2 - 1][height * 2 - 1];
        for (boolean[] row : Arrays.asList(grid)) {
            Arrays.fill(row, false);
        }

        for (int i = 0; i < width ; i++) {
            for (int j = 0; j < height; j++) {
                Cell cellAt = cells[i][j];

                // For each cell, we only deal with its S and E wallRefs.
                // The N and W ones should have already been dealt with when we visited its neighbors.
                WallRef wallRefE = cellAt.walls.get(Direction.E);
                if (wallRefE != null && wallRefE.isWall) {
                    if (i < width - 1) {
                        if (j > 0) {
                            grid[i * 2 + 1][j * 2 - 1] = true;
                        }

                        grid[i * 2 + 1][j * 2] = true;

                        if (j < height - 1) {
                            grid[i * 2 + 1][j * 2 + 1] = true;
                        }
                    }
                }

                WallRef wallRefS = cellAt.walls.get(Direction.S);
                if (wallRefS != null && wallRefS.isWall) {
                    if (j < height - 1) {
                        if (i > 0) {
                            grid[i * 2 - 1][j * 2 + 1] = true;
                        }

                        grid[i * 2][j * 2 + 1] = true;

                        if (i < width - 1) {
                            grid[i * 2 + 1][j * 2 + 1] = true;
                        }
                    }
                }
            }
        }

        return grid;
    }

    @Override
    public String toString()
    {
        boolean[][] boolGrid = render();

        char[][] charGrid = new char[height * 2 - 1][width * 2 - 1];
        for (int i = 0; i < width * 2 - 1; i++) {
            for (int j = 0; j < height * 2 - 1; j++) {
                // charGrid is transposed from boolGrid, since otherwise the axes would be backwards.
                if (boolGrid[i][j]) {
                    charGrid[j][i] = '#';
                }
                else {
                    charGrid[j][i] = '-';
                }
            }
        }

        String out = "   ";
        for (int i = 0; i < width * 2 - 1; i++) {
            // Plus signs on axes mark true cells (not walls).
            // A wall should never be at the intersection of a row and a column both marked by plus signs.
            if (i % 2 == 0) {
                out += "+ ";
            }
            else {
                out += "  ";
            }
        }

        for (int i = 0; i < height * 2 - 1; i++) {
            out += '\n';

            if (i % 2 == 0) {
                out += "+ ";
            }
            else {
                out += "  ";
            }

            for (int j = 0; j < width * 2 - 1; j++) {
                out += " " + charGrid[i][j];
            }
        }

        return out;
    }

    // Generate maze according to recursive division algorithm.
    // See README for info on the algorithm.
    public void divRecursive(Random rand)
    {
        if (width != 1 && height != 1) {
            // Make a vertical wall and a horizontal wall
            int slice_x = rand.nextInt(width - 1) + 1, // position of vertical wall
                slice_y = rand.nextInt(height - 1) + 1; // position of horizontal wall

            if (debug) {
                System.out.println("width: " + width + ", height: " + height);
                System.out.println("slice_x: " + slice_x + ", slice y: " + slice_y);
            }

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

            for (Direction direction : directions) {
                int breakAt;
                switch (direction) {
                case N:
                    breakAt = rand.nextInt(slice_x);
                    cells[breakAt][slice_y].walls.get(Direction.N).isWall = false;
                    break;
                case E:
                    breakAt = rand.nextInt(height - slice_y) + slice_y;
                    cells[slice_x][breakAt].walls.get(Direction.W).isWall = false;
                    break;
                case S:
                    breakAt = rand.nextInt(width - slice_x) + slice_x;
                    cells[breakAt][slice_y].walls.get(Direction.N).isWall = false;
                    break;
                case W:
                    breakAt = rand.nextInt(slice_y);
                    cells[slice_x][breakAt].walls.get(Direction.W).isWall = false;
                }
            }

            if (debug) {
                System.out.println(toString());
                System.out.println();
            }

            // Divide and recurse
            Cell[][] NEQuadrant = new Cell[width - slice_x][slice_y];
            Cell[][] SEQuadrant = new Cell[width - slice_x][height - slice_y];
            for (int i = slice_x; i < width; i++) {
                NEQuadrant[i - slice_x] = Arrays.copyOfRange(cells[i], 0, slice_y);
                SEQuadrant[i - slice_x] = Arrays.copyOfRange(cells[i], slice_y, height);
            }
            Maze NERegion = new Maze(debug, width - slice_x, slice_y, NEQuadrant);
            Maze SERegion = new Maze(debug, width - slice_x, height - slice_y, SEQuadrant);

            Cell[][] SWQuadrant = new Cell[slice_x][height - slice_y];
            Cell[][] NWQuadrant = new Cell[slice_x][slice_y];
            for (int i = 0; i < slice_x; i++) {
                SWQuadrant[i] = Arrays.copyOfRange(cells[i], slice_y, height);
                NWQuadrant[i] = Arrays.copyOfRange(cells[i], 0, slice_y);
            }
            Maze SWRegion = new Maze(debug, slice_x, height - slice_y, SWQuadrant);
            Maze NWRegion = new Maze(debug, slice_x, slice_y, NWQuadrant);

            NERegion.divRecursive(rand);
            SERegion.divRecursive(rand);
            SWRegion.divRecursive(rand);
            NWRegion.divRecursive(rand);
        }
    }

    public void genMaze()
    {
        if (debug) {
            divRecursive(new Random(1));
        }
        else {
            divRecursive(new Random(System.currentTimeMillis()));
        }
    }

    public static void main(String[] args)
    {
        // See README for documentation of command-line options.

        int width, height;
        if (args.length >= 2) {
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
        }
        else {
            width = 10;
            height = 12;
        }

        boolean debug = args.length >= 3 && args[2].equals("debug");

        // Option to print debug info, but still use a random seed.
        boolean debug_rand = args.length >= 3 && args[2].equals("debug-rand");

        Maze maze = new Maze(debug || debug_rand, width, height);
        System.out.println(maze.toString());
        System.out.println();

        if (debug_rand) {
            maze.divRecursive(new Random(System.currentTimeMillis()));
        }
        else {
            maze.genMaze();
        }

        System.out.println("Final:\n" + maze.toString());
    }
}