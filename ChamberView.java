import java.util.EnumMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.OperationNotSupportedException;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

/*
 * Thomas: This class is a wrapper for a Chamber that allows it to be used as a game view,
 * since that necessitates it keeping track of certain state
 */
public class ChamberView extends GameView {
    // Constants

    // Height of the dialogue box
    public static final int DIALOGUE_HEIGHT = 10;

    // time delta * this constant = true distance moved
    public static final double PLAYER_SPEED = 0.015;

    // The Chamber this instance wraps around
    public Chamber chamber;

    // The player's sub-grid position
    public Point2D.Double playerPosition;

    // Which direction is the player going?
    public DirectionEx playerDirection;

    // Queue of dialogue to be printed
    private ConcurrentLinkedQueue<String> dialogueQueue;

    // Is the game paused?
    private boolean paused;

    // Is the game waiting for the player to finish scrolling through dialogue?
    private boolean scrolling;

    public ChamberView(Game outerState, Chamber chamber) {
        super(outerState);

        this.chamber = chamber;

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
    public void update(double delta) throws Exception {
        // Calculate distance to move
        double currentSpeed = PLAYER_SPEED * delta;

        Point2D.Double newPosition = new Point2D.Double(playerPosition.x, playerPosition.y);

        // Get key input
        boolean goingUp = outerState.keyBox.getReleaseKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean goingRight = outerState.keyBox.getReleaseKeys(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
        boolean goingDown = outerState.keyBox.getReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean goingLeft = outerState.keyBox.getReleaseKeys(KeyEvent.VK_LEFT, KeyEvent.VK_A);

        DirectionEx newPlayerDirection = new DirectionEx();

        // Up and down 
        if (goingUp) {
            newPlayerDirection.add(Direction.N);
        }

        if (goingRight) {
            newPlayerDirection.add(Direction.E);
        }
        
        if (goingDown) {
            newPlayerDirection.add(Direction.S);
        }

        if (goingLeft) {
            newPlayerDirection.add(Direction.W);
        }

        if (!newPlayerDirection.isEmpty()) {
            playerDirection = newPlayerDirection;
            
            Point2D.Double offset = newPlayerDirection.getOffset();

            newPosition.x += offset.x * currentSpeed;
            newPosition.y += offset.y * currentSpeed;

            // Don't move into a wall.
            if (!chamber.squares[(int) newPosition.x][(int) newPosition.y].isWall) {
                // Move to an adjacent chamber if need be
                if (Math.floor(newPosition.y) < 0) {
                    chamber = chamber.adjacentChambers.get(Direction.N);
                    newPosition.x = (double) Chamber.HEIGHT - 1.0;
                }
                else if ((int) newPosition.x >= Chamber.WIDTH - 1) {
                    chamber = chamber.adjacentChambers.get(Direction.E);
                    newPosition.x = 0.0;
                }
                else if ((int) newPosition.y >= Chamber.HEIGHT - 1) {
                    chamber = chamber.adjacentChambers.get(Direction.S);
                    newPosition.y = 0.0;
                }
                else if (Math.floor(newPosition.x) < 0) {
                    chamber = chamber.adjacentChambers.get(Direction.W);
                    newPosition.x = (double) Chamber.WIDTH - 1.0;
                }
                
                playerPosition = newPosition;
            }
        }
    }

    // Render to string
    @Override
    public String render() throws OperationNotSupportedException {
        String renderState = "";
        this.playerPosition = new Point2D.Double(0.0, 0.0);
        int trunc_x = (int) playerPosition.x, trunc_y = (int) playerPosition.y;
        for (int i = 0; i < Chamber.HEIGHT; i++) {
            System.out.println(trunc_x + " " + trunc_y);
            for (int j = 0; j < Chamber.WIDTH; j++) {
                char symbol = ' ';
                if (i == trunc_y && j == trunc_x) {
                    symbol = '@';
                }
                if (chamber.squares[i][j].isWall) {
                    symbol = '*';
                } else if (chamber.squares[i][j].sprites.size() > 0) {
                    for (Sprite sprite : chamber.squares[i][j].sprites) {
                        if (sprite.isVisible()) {
                            symbol = sprite.symbol();
                        }
                    }
                }

                renderState += symbol;
            }
            renderState += '\n';
        }

        return renderState;
    }

    public void eventAtPos(Point2D.Double pointAt, GameEvent e) {
        Square square = chamber.squares[(int) pointAt.y][(int) pointAt.x];
        square.eventOn(e);
    }
}