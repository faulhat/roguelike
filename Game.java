/*
 * De Re Publica, a game about ancient Rome
 * created by Athenase, Lukas, Ricardo, Thomas, and Tobias.
 */

import javax.swing.JTextArea;
import java.awt.Font;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.awt.Point;

/*
 * The main class that will run the whole thing
 * Manages both the Swing-based display and the game state.
 */
public class Game {
    // An exception for when it is requested that a string be rendered which cannot be
    public static class RenderException extends Exception {
        public RenderException(String in)
        {
            super("Error! From class Game: this String could not be rendered to the display: \"" + in + "\"");
        }
    }

    // Constants regarding the game display
    // Must account for the whitespace added to the output
    public static final int DISPLAY_WIDTH = Chamber.WIDTH * 2 - 1;
    public static final int GAME_HEIGHT = Chamber.HEIGHT * 2 - 1;
    public static final int DISPLAY_HEIGHT = GAME_HEIGHT + ChamberView.DIALOGUE_HEIGHT;

    // Constants for size of game map (temporary) //
    public static final int MAP_WIDTH = 5, MAP_HEIGHT = 5;

    // How many levels in a game?
    public static final int N_LEVELS = 3;

    public KeyBox keyBox;

    private JTextArea displayArea;

    // The view being displayed
    public GameView currentView;

    // Which direciton is the player moving?
    public Direction playerDirection;

    // Levels in this game
    public ArrayList<ChamberMaze> levels;

    // Which level is the player on?
    public int currentLevel;

    // The current level
    public ChamberMaze levelMap;

    // The player's current state
    public PlayerState playerState;

    // The RNG for the game to use
    public Random rand;

    // The save files
    public SaveList saveList;

    private static boolean debug;

    // Generate new levels
    // Each level contains a teleporter to the next level and the previous level (if applicable).
    public static ArrayList<ChamberMaze> genLevels(int n_levels, int m_width, int m_height, Random rand)
    {
        assert(n_levels > 0);
        assert(m_width > 4);
        assert(m_height > 4);

        ArrayList<ChamberMaze> levels = new ArrayList<>();
        for (int i = 0; i < N_LEVELS; i++) {
            levels.add(new ChamberMaze(i, m_width + rand.nextInt(4) - 2, m_height + rand.nextInt(4) + 2, rand));
        }

        for (int i = 1; i < N_LEVELS; i++) {
            levels.get(i).chambers[0][0].putSprite(new Point(3, 3), new Teleporter(i - 1, levels.get(i - 1).bossLocation, new Point(5, 5)));
        }

        return levels;
    }

    public Game(long seed) throws Exception
    {
        keyBox = new KeyBox();

        // Create the game display
        displayArea = new JTextArea(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        displayArea.setEditable(false);
        displayArea.setFocusable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));

        keyBox.frame.add(displayArea);
        keyBox.frame.pack();
        keyBox.frame.setVisible(true);

        rand = new Random(seed);

        init();
    }

    public Game() throws Exception
    {
        this(System.currentTimeMillis());
    }

    public void init()
    {
        // Set current view to start menu
        currentView = new TitleScreen(this);
        saveList = SaveList.loadList();
    }

    // Method to start a new game, placing the player in the first chamber of a newly generated game world
    public void startNew()
    {
        playerState = new PlayerState();

        // Create the player's inventory and give him some supplies
        playerState = new PlayerState();
        for (int i = 0; i < 5; i++) {
            playerState.inventory.add(new Bread());
        }

        for (int i = 0; i < 4; i++) {
            playerState.inventory.add(new Coffee());
        }

        levels = genLevels(N_LEVELS, 5, 5, rand);
        if (debug) {
            for (ChamberMaze level : levels) {
                System.out.println(level.maze.toString() + "\n");
            }
        }

        currentView = new LevelTransition.StartTransition(this);
    }

    // Load a save state
    public void loadSave(SaveState state)
    {
        levels = new ArrayList<>();
        for (ChamberMaze level : state.levels) {
            levels.add(new ChamberMaze(level));
        }

        currentLevel = state.currentLevel;
        playerState = new PlayerState(state.player);

        currentView = new ChamberView(this, currentLevel, state.location, state.position);
    }

    public void setDisplayText(String toDisplay) throws RenderException
    {
        List<String> lines = Arrays.asList(toDisplay.split("\n"));

        if (lines.size() > DISPLAY_HEIGHT) {
            throw new RenderException(toDisplay);
        }

        for (String line : lines) {
            if (line.length() > DISPLAY_WIDTH) {
                throw new RenderException(line);
            }
        }

        displayArea.setText(toDisplay);
    }

    // Update and render repeatedly, passing the time delta since the last update to the current view's update method.
    public void run() throws Exception
    {
        Instant then = Instant.now();
        while (true) {
            Instant now = Instant.now();
            currentView.update((double) Duration.between(then, now).toNanos() / 1e6);

            setDisplayText(currentView.render());
            then = now;
        }
    }

    public static String wrapString(String in)
    {
        String out = "";
        int col = 0;
        for (int i = 0; i < in.length(); i++) {
            out += in.charAt(i);

            if (in.charAt(i) == '\n') {
                col = 0;
                continue;
            }

            if (col == DISPLAY_WIDTH - 1) {
                out += "\n";
                col = 0;
            }

            col++;
        }

        return out;
    }

    public static void main(String args[])
    {
        try {
            Game game = new Game();
            debug = args.length > 0 && args[0].equals("debug");

            game.run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
