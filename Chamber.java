import java.util.EnumSet;

/*
 * Thomas: a class representing rooms in the game.
 * These rooms are constant in height and width.
 */
public class Chamber {
    // Constants

    // These are the height and width of the chamber object, not the display.
    public static final int WIDTH = 25;
    public static final int HEIGHT = 20;

    // A map of this Chamber
    public Square[][] squares;

    // Constructor for creating a Chamber from a matrix of Squares (move semantics)
    public Chamber(Square[][] squares)
    {
        // This new Chamber assumes ownership of this matrix
        this.squares = squares;
    }

    // Constructor for empty Chamber
    public Chamber()
    {
        this(new Square[WIDTH][HEIGHT]);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                squares[i][j] = new Square(false);
            }
        }
    }

    // Method to fill in Chamber
    // For now it just puts walls on each side with two-block exits in them according to the exits set provided
    public void genChamber(EnumSet<Direction> exits)
    {
        for (int i = 0; i < WIDTH; i++) {
            // Make an exit in the middle of the North wall.
            if (!(exits.contains(Direction.N) && (i == WIDTH / 2 || i == WIDTH / 2 + 1))) {
                squares[i][0].isWall = true;
            }

            // Make an exit in the middle of the South wall.
            if (!(exits.contains(Direction.S) && (i == WIDTH / 2 || i == WIDTH / 2 + 1))) {
                squares[i][HEIGHT - 1].isWall = true;
            }
        }

        for (int i = 0; i < HEIGHT; i++) {
            // Make an exit in the middle of the West wall.
            if (!(exits.contains(Direction.W) && (i == HEIGHT / 2 || i == HEIGHT / 2 + 1))) {
                squares[0][i].isWall = true;
            }

            // Make an exit in the middle of the East wall.
            if (!(exits.contains(Direction.E) && (i == HEIGHT / 2 || i == HEIGHT / 2 + 1))) {
                squares[WIDTH - 1][i].isWall = true;
            }
        }
    }
}