import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.OperationNotSupportedException;

import java.awt.geom.Point2D;
import java.awt.Point;

/*
 * Thomas: This class represents a Chamber in the game,
 * this being a roguelike and all.
 * Chambers are of constant height and width.
 * Also, this class handles game dialogue as displayed while in this view.
 */
public class Chamber extends GameView {
    // Constants

    // These are the height and width of the chamber object, not the display.
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    // Height of the dialogue box
    public static final int DIALOGUE_HEIGHT = 10;

    // time delta * this constant = true distance moved
    public static final double PLAYER_SPEED = 0.015;

    // The external game state, which this class may need to refer to.
    public Game outerState;

    // A map of this chamber
    private Square[][] squares;

    // The player's sub-grid position
    private Point2D.Double playerPosition;

    // Queue of dialogue to be printed
    private ConcurrentLinkedQueue<String> dialogueQueue;

    // Is the game paused?
    private boolean paused;

    // Is the game waiting for the player to finish scrolling through dialogue?
    private boolean scrolling;

    public Chamber(Game outerState, Square[][] squares) {
        super(outerState);

        // This object assumes ownership of the square array.
        this.squares = squares;

        dialogueQueue = new ConcurrentLinkedQueue<>();
        paused = false;
        scrolling = false;
    }

    // Constructor for empty Chamber
    public Chamber(Game outerState) {
        this(outerState, new Square[WIDTH][HEIGHT]);

        for (int i = 0; i < WIDTH; i++) {
            Arrays.fill(squares[i], new Square(false));
        }
    }

    // Constructor for Chamber with exits
    public Chamber(Game outerState, EnumSet<Direction> exits) {
        this(outerState);

        genChamber(exits);
    }

    // Method to fill in Chamber
    public void genChamber(EnumSet<Direction> exits) {
        for (int i = 0; i < WIDTH; i++) {
            // Make an exit in the middle of the North wall.
            if (!(exits.contains(Direction.N) && i == WIDTH / 2 + 1)) {
                squares[i][0].isWall = true;
            }

            // Make an exit in the middle of the South wall.
            if (!(exits.contains(Direction.S) && i == WIDTH / 2 + 1)) {
                squares[i][HEIGHT - 1].isWall = true;
            }
        }

        for (int i = 0; i < HEIGHT; i++) {
            // Make an exit in the middle of the West wall.
            if (exits.contains(Direction.W) && !(i == HEIGHT / 2 + 1)) {
                squares[0][i].isWall = true;
            }

            // Make an exit in the middle of the East wall.
            if (exits.contains(Direction.E) && !(i == HEIGHT / 2 + 1)) {
                squares[WIDTH - 1][i].isWall = true;
            }
        }
    }

    // Put the player in the chamber at a given position
    public void enterAt(Point initialPosition) {
        playerPosition = new Point2D.Double((double) initialPosition.x, (double) initialPosition.y);
    }

    // How to update this view given a time delta
    @Override
    public void update(double delta) {
        // Do stuff here
    }

    // Render to string
    @Override
    public String render() throws OperationNotSupportedException {
        String renderState = "";
        for (Square[] line : squares) {
            for (Square square : line) {
                char symbol = ' ';

                if (square.isWall) {
                    symbol = '*';
                } else if (square.sprites.size() > 0) {
                    for (Sprite sprite : square.sprites) {
                        if (sprite.isVisible()) {
                            symbol = sprite.symbol();
                        }
                    }
                }

                renderState += symbol;
            }
        }

        return renderState;
    }
}