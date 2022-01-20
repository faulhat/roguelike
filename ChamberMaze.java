/*
 * Thomas: this class is for a maze of chambers
 */

public class ChamberMaze {
    public final int width, height;

    // A matrix of chambers representing this maze.
    public Chamber[][] chambers;

    public ChamberMaze(int width, int height)
    {
        this.width = width;
        this.height = height;

        Maze maze = new Maze(width, height);
        maze.genMaze();

        // Generate the matrix of Chambers
        chambers = new Chamber[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                chambers[i][j] = new Chamber();
                chambers[i][j].genChamber(maze.getExits(i, j));
            }
        }
    }
}