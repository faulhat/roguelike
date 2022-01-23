import javax.naming.OperationNotSupportedException;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
    public static final double PLAYER_SPEED = 0.012;

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

    // Which direction was the player last going?
    public DirectionEx lastPlayerDirection;

    // Start player in top-leftmost Chamber
    public ChamberView(Game outerState, int level)
    {
        super(outerState);

        this.map = outerState.levels.get(level);

        playerDirection = new DirectionEx();
        lastPlayerDirection = new DirectionEx(Direction.N);
    }

    public ChamberView(Game outerState, int level, int map_x, int map_y, int chamber_x,int chamber_y)
    {
        this(outerState, level);

        enterAt(map_x, map_y, chamber_x, chamber_y);
    }

    public ChamberView(Game outerState, int level, Point location, Point position)
    {
        this(outerState, level, location.x, location.y, position.x, position.y);
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

        // Has the pause key been pressed?
        boolean toPause = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ESCAPE, KeyEvent.VK_P);
        if (toPause) {
            outerState.currentView = new PauseMenu(outerState, this);
            return;
        }

        // Has the inventory key been pressed?
        boolean goToInventory = outerState.keyBox.getReleaseKey(KeyEvent.VK_I);
        if (goToInventory) {
            outerState.currentView = new InventoryMenu(outerState, this);
            return;
        }

        // Get key input
        boolean goingUp = outerState.keyBox.getKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean goingRight = outerState.keyBox.getKeys(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
        boolean goingDown = outerState.keyBox.getKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean goingLeft = outerState.keyBox.getKeys(KeyEvent.VK_LEFT, KeyEvent.VK_A);

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

        // Re-align with grid if we are changing directions.
        // This prevents staggered movement when going diagonally.
        if (!newPlayerDirection.equals(playerDirection)) {
            newPosition.x = Math.floor(newPosition.x);
            newPosition.y = Math.floor(newPosition.y);
        }

        playerDirection = newPlayerDirection;

        // If the user is pressing a key to move...
        if (!playerDirection.isEmpty()) {
            Point2D.Double offset = playerDirection.getOffset();

            newPosition.x += offset.x * currentSpeed;
            newPosition.y += offset.y * currentSpeed;

            int prevGridPos_x = (int) position.x, prevGridPos_y = (int) position.y;
            int newGridPos_x = (int) newPosition.x, newGridPos_y = (int) newPosition.y;

            // If we'd end up outside of chamber
            if (newGridPos_x > Chamber.WIDTH - 1 || newGridPos_x < 0 || newGridPos_y > Chamber.HEIGHT - 1 || newGridPos_y < 0) {
                // System.out.println("New position x: " + newPosition.x + ", y: " + newPosition.y); //Fcrus

                // Move to an adjacent chamber if need be
                // We use Math.floor here because if y were -0.9 and we used newGridPos_y, it would be 0 and not -1.
                if (Math.floor(newPosition.y) < 0) {
                    // Don't move into the adjacent chamber if there is no adjacent chamber!
                    if (location.y - 1 < 0) {
                        newPosition.y = position.y;
                    }
                    // Otherwise, we should end up in the next chamber in the direction we're going.
                    else {
                        chamber = map.chambers[location.x][location.y - 1];
                        location.y--;
                        newPosition.y = (double) Chamber.HEIGHT - 1.0;
                    }
                }

                if (newGridPos_x > Chamber.WIDTH - 1) {
                    if (location.x + 1 >= map.width) {
                        newPosition.x = position.x;
                    }
                    else {
                        chamber = map.chambers[location.x + 1][location.y];
                        location.x++;
                        newPosition.x = 0.0;
                    }
                }

                if (newGridPos_y > Chamber.HEIGHT - 1) {
                    if (location.y + 1 >= map.height) {
                        newPosition.y = position.y;
                    }
                    else {
                        chamber = map.chambers[location.x][location.y + 1];
                        location.y++;
                        newPosition.y = 0.0;
                    }
                }

                if (Math.floor(newPosition.x) < 0) {
                    if (location.x - 1 < 0) {
                        newPosition.x = position.x;
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
            // Otherwise, move normally, but don't run into a wall.
            else if (!chamber.squares[newGridPos_x][newGridPos_y].isWall) {
                position = newPosition;
            }

            // Roll the dice
            if ((int) position.x != prevGridPos_x || (int) position.y != prevGridPos_y) {
                if (outerState.rand.nextDouble() < chamber.encounterRate) {
                    ArrayList<Enemy> enemies = new ArrayList<>();
                    enemies.add(new FederalAgent(1));
                    enemies.add(new FederalAgent(2));

                    outerState.currentView = new BattleView(outerState, enemies, this);
                }
            }

            // Update lastPlayerDirection. We do this here because we must only do it after the player has moved.
            lastPlayerDirection = playerDirection;
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
                        if (sprite.visible) {
                            assert(sprite.symbol != null);
                            symbol = sprite.symbol;
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

        renderState += "Level " + (outerState.currentLevel + 1) + "\n";

        for (int i = 0; i < Game.MAP_HEIGHT; i++) {
            for (int j = 0; j < Game.MAP_WIDTH; j++) {
                if (i == location.y && j == location.x) {
                    renderState += "* ";
                }
                else {
                    renderState += "- ";
                }
            }

            renderState += "\n";
        }

        return renderState;
    }

    public void eventAtPos(Point2D.Double pointAt, GameEvent e)
    {
        Square square = chamber.squares[(int) pointAt.y][(int) pointAt.x];
        square.onEvent(outerState, e);
    }
}