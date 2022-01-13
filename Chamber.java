import java.util.Arrays;
import java.awt.geom.Point2D;
import java.awt.Point;

/*
 * Thomas: This class represents a Chamber in the game,
 * this being a roguelike and all.
 */
public class Chamber implements GameView {
    // Constants
    public static final int WIDTH = 15;
    public static final int HEIGHT = 12;
    public static final double PLAYER_SPEED = 0.015;

    // The external game state, which this class may need to refer to.
    public Game outerState;

    // A map of which squares are walkable.
    private boolean[][] walkable;

    // The player's sub-grid position
    private Point2D.Double playerPosition;

    public Chamber(boolean[][] walkable) {
        this.walkable = new boolean[WIDTH][];

        assert(walkable.length == WIDTH);

        for (int i = 0; i < walkable.length; i++) {
            assert(walkable[i].length == HEIGHT);

            this.walkable[i] = Arrays.copyOf(walkable[i], HEIGHT);
        }
    }

    // Put the player in the chamber at a given position
    public void enterAt(Point initialPosition) {
        playerPosition = new Point2D.Double((double) initialPosition.x, (double) initialPosition.y);
    }

    // How to update this view given a time delta
    @Override
    public void update(double delta) {

    }

    // How to render this view as a string
    @Override
    public String render() {
        return ""; //
    }
}
