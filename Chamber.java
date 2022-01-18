import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.naming.OperationNotSupportedException;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

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

    // Are events on?
    private boolean eventsOn;

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
        double farBottom = (double)HEIGHT - 1;
        double farRight = (double)WIDTH - 1;

        boolean goingUp = outerState.keyBox.getResetKey(KeyEvent.VK_UP),
                goingDown = outerState.keyBox.getResetKey(KeyEvent.VK_DOWN),
                goingLeft = outerState.keyBox.getResetKey(KeyEvent.VK_LEFT),
                goingRight = outerState.keyBox.getResetKey(KeyEvent.VK_RIGHT),
                terrainChangeVerticalWall = outerState.keyBox.getResetKey(KeyEvent.VK_V),
                terrainChangeHorizontalWall = outerState.keyBox.getResetKey(KeyEvent.VK_H),
                terrainChangeClear = outerState.keyBox.getResetKey(KeyEvent.VK_C),
                openInsertMenu = outerState.keyBox.getResetKey(KeyEvent.VK_I),
                closeInsertMenu = outerState.keyBox.getResetKey(KeyEvent.VK_E),
                Ctrl = outerState.keyBox.getResetKey(KeyEvent.VK_CONTROL),
                select = outerState.keyBox.getReleaseKey(KeyEvent.VK_S),
                delete = outerState.keyBox.getResetKey(KeyEvent.VK_BACK_SPACE),
                reset = outerState.keyBox.getResetKey(KeyEvent.VK_R),
                pause = outerState.keyBox.getResetKey(KeyEvent.VK_P),
                eventsSwitch = outerState.keyBox.getReleaseKey(KeyEvent.VK_W),
                interact = outerState.keyBox.getResetKey(KeyEvent.VK_Z);
        for (int i = (int) Math.max(playerPosition.y - 1, 0.0); i <= (int) Math.min(farBottom, playerPosition.y + 1.0); i++) {
            for (int j = (int) Math.max(playerPosition.x - 1.0, 0.0); j <= (int) Math.min(farRight, playerPosition.x + 1.0); j++) {
                Square s = squares[i][j];
                s.eventOn(new GameEvent.IntersectEvent(outerState.playerDirection));
            }
        }
      this.eventAtPos(playerPosition, new GameEvent.IntersectEvent(outerState.playerDirection));
      if (goingUp && !goingDown) { // Now that we've dealt with all possible diagonals, we can deal with the normal
        playerPosition.y = Math.max(0.0, playerPosition.y - delta);
        outerState.playerDirection = Direction.N;
      } else if (goingDown && !goingUp) {
        playerPosition.y = Math.min(farBottom, playerPosition.y + delta);
       outerState.playerDirection = Direction.S;
      } else if (goingLeft && !goingRight) {
       playerPosition.x = Math.max(0.0, playerPosition.x - delta);
       outerState.playerDirection = Direction.W;
      } else if (goingRight && !goingLeft) {
        playerPosition.x = Math.min(farRight, playerPosition.x + delta);
        outerState.playerDirection =  Direction.E;
      } else if (pause){
        outerState.currentView = new StartMenu(outerState);
      }
    }
    

    // Render to string
    @Override
    public String render() throws OperationNotSupportedException {
        String renderState = "";
        this.playerPosition = new Point2D.Double(0.0, 0.0);
        int trunc_x = (int)playerPosition.x, trunc_y = (int)playerPosition.y;
        for (int i = 0; i < HEIGHT; i++) {
            System.out.println(trunc_x + " " + trunc_y);
            for (int j = 0; j < WIDTH; j++) {
                char symbol = ' ';
                if (i == trunc_y && j == trunc_x){
                    symbol = '@';
                }
                if (squares[i][j].isWall) {
                    symbol = '*';
                } else if (squares[i][j].sprites.size() > 0) {
                    for (Sprite sprite : squares[i][j].sprites) {
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
    public void eventAtPos(Point2D.Double pointAt, GameEvent e){
        Square square = squares[(int)pointAt.y][(int)pointAt.x];
        square.eventOn(e);
    }
}