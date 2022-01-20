import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.OperationNotSupportedException;

import java.awt.geom.Point2D;
import java.awt.event.KeyEvent;
import java.awt.Point;

/*
 * Thomas: This class is a wrapper for a ChamberMaze that allows it to be used as a game view,
 * since that necessitates it keeping track of certain state
 */
public class ChamberView extends GameView {
    // Constants

    // Height of the dialogue box
    public static final int DIALOGUE_HEIGHT = 10;

    // time delta * this constant = true distance moved
    public static final double PLAYER_SPEED = 0.9;

    // The ChamberMaze this instance wraps around
    public ChamberMaze map;

    // The Chamber the player is currently in.
    public Chamber chamber;

    // And where in the map is that Chamber?
    public Point location;

    // The player's sub-grid position
    public Point2D.Double position;

    // Which direction is the player going?
    public DirectionEx playerDirection;

    // Queue of dialogue to be printed
    private ConcurrentLinkedQueue<String> dialogueQueue;

    // Is the game waiting for the player to finish scrolling through dialogue?
    private boolean scrolling;

    // Start player in top-leftmost Chamber
    public ChamberView(Game outerState, ChamberMaze map)
    {
        super(outerState);

        this.map = map;

        dialogueQueue = new ConcurrentLinkedQueue<>();
        scrolling = false;

    }

    // Put the player in the chamber at a given position
    public void enterAt(int map_x, int map_y, int chamber_x, int chamber_y)
    {
        location = new Point(map_x, map_y);
        chamber = map.chambers[map_x][map_y];
        position = new Point2D.Double((double) chamber_x, (double) chamber_y);
    }

    // How to update this view given a time delta
    @Override
    public void update(double delta) throws Exception
    {
        // Calculate distance to move
        double currentSpeed = PLAYER_SPEED * delta;

        Point2D.Double newPosition = new Point2D.Double(position.x, position.y);

        // Get key input
        boolean goingUp = outerState.keyBox.getReleaseKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean goingRight = outerState.keyBox.getReleaseKeys(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
        boolean goingDown = outerState.keyBox.getReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean goingLeft = outerState.keyBox.getReleaseKeys(KeyEvent.VK_LEFT, KeyEvent.VK_A);
        boolean toPause = outerState.keyBox.getResetKeys(KeyEvent.VK_ESCAPE, KeyEvent.VK_P);

        if (toPause) {
            outerState.currentView = new PauseMenu(outerState, this);
            return;
        }

        DirectionEx newPlayerDirection = new DirectionEx();

        // Which direction?
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

            // Avoid lag when switching directions
            if ((int) newPosition.y == (int) position.y) {
                if (newPlayerDirection.contains(Direction.N) && playerDirection.contains(Direction.S)) {
                    newPosition.y = Math.floor(newPosition.y);
                }
                else if (newPlayerDirection.contains(Direction.S) && playerDirection.contains(Direction.N)) {
                    newPosition.y = Math.floor(newPosition.y) + 0.99;
                }
            }

            if ((int) newPosition.x == (int) position.x) {
                if (newPlayerDirection.contains(Direction.E) && playerDirection.contains(Direction.W)) {
                    newPosition.x = Math.floor(newPosition.x);
                }
                else if (newPlayerDirection.contains(Direction.W) && playerDirection.contains(Direction.E)) {
                    newPosition.x = Math.floor(newPosition.x) + 0.99;
                }
            }

            // Don't move into a wall.
            if (!chamber.squares[(int) newPosition.x][(int) newPosition.y].isWall) {
                // System.out.println("New position x: " + newPosition.x + ", y: " + newPosition.y);

                // Move to an adjacent chamber if need be
                if (Math.floor(newPosition.y) < 0) {
                    // Don't move into the adjacent chamber if there is no adjacent chamber!
                    if (location.y - 1 < 0) {
                        newPosition = position;
                    }
                    // Otherwise, we should end up in the next chamber in the direction we're going.
                    else {
                        chamber = map.chambers[location.x][location.y - 1];
                        location.y--;
                        newPosition.y = (double) Chamber.HEIGHT - 1.0;
                    }
                }
                else if ((int) newPosition.x >= Chamber.WIDTH - 1) {
                    if (location.x + 1 >= map.width) {
                        newPosition = position;
                    }
                    else {
                        chamber = map.chambers[location.x + 1][location.y];
                        location.x++;
                        newPosition.x = 0.0;
                    }
                }
                else if ((int) newPosition.y >= Chamber.HEIGHT - 1) {
                    if (location.y + 1 >= map.height) {
                        newPosition = position;
                    }
                    else {
                        chamber = map.chambers[location.x][location.y + 1];
                        location.y++;
                        newPosition.y = 0.0;
                    }
                }
                else if (Math.floor(newPosition.x) < 0) {
                    if (location.x - 1 < 0) {
                        newPosition = position;
                    }
                    else {
                        chamber = map.chambers[location.x - 1][location.y];
                        location.x--;
                        newPosition.x = (double) Chamber.WIDTH - 1.0;
                    }
                }

                // Update our position
                position = newPosition;
            }
        }
    }

    // Render to string
    @Override
    public String render() throws OperationNotSupportedException
    {
        String renderState = "";

        // chamber.squares must be transposed for rendering
        // otherwise the dimensions would be backwards.
        int trunc_x = (int) position.x, trunc_y = (int) position.y;
        for (int i = 0; i < Chamber.HEIGHT; i++) {
            for (int j = 0; j < Chamber.WIDTH; j++) {
                Square square = chamber.squares[j][i];

                char symbol = ' ';
                if (j == trunc_x && i == trunc_y) {
                    symbol = '@';
                }
                else if (square.isWall) {
                    symbol = '+';
                }
                else if (square.sprites.size() > 0) {
                    for (Sprite sprite : square.sprites) {
                        if (sprite.isVisible()) {
                            symbol = sprite.symbol();
                        }
                    }
                }

                if (j > 0) {
                    renderState += ' ';
                }

                renderState += symbol;
            }

            renderState += '\n';
        }

        renderState += "Location in chamber map: ( x = " + location.x + ", y = " + location.y + " )\n";

        String[] mazeStringLines = map.maze.toString().split("\n");
        for (int i = 0; i < mazeStringLines.length; i++) {
            if (i == 1 + location.y * 2) {
                for (int j = 0; j < mazeStringLines[i].length(); j++) {
                    if (j == 3+ location.x * 4) {
                        renderState += "*";
                    }
                    else {
                        renderState += mazeStringLines[i].charAt(j);
                    }
                }
            }
            else {
                renderState += mazeStringLines[i];
            }

            renderState += "\n";
        }

        return renderState;
    }

    public void eventAtPos(Point2D.Double pointAt, GameEvent e)
    {
        Square square = chamber.squares[(int) pointAt.y][(int) pointAt.x];
        square.eventOn(e);
    }
}