import java.util.concurrent.ConcurrentLinkedQueue;
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
    public static final int WIDTH = 15;
    public static final int HEIGHT = 12;

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

    // Put the player in the chamber at a given position
    public void enterAt(Point initialPosition) {
        playerPosition = new Point2D.Double((double) initialPosition.x, (double) initialPosition.y);
    }

    // How to update this view given a time delta
    @Override
    public void update(double delta) {
        // Do stuff here
    }

    // Render to display
    @Override
    public void render() {
        // Make this do stuff!
        outerState.display.clear();
    }
}
