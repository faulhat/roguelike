import javax.swing.JTextArea;
import java.awt.Font;
import java.time.Duration;
import java.time.Instant;

/*
 * Thomas: the main class that will run the whole thing
 * Manages both the Swing-based display and the game state.
 */
public class Game {
    // An enumeration representing the current game view
    public static enum View {
        START_MENU,
        INVENTORY,
        OVERWORLD,
        BATTLE,
        PAUSE_MENU
    }

    // Constants regarding the game display
    // Must account for the whitespace added to the output
    public static final int DISPLAY_WIDTH = Chamber.WIDTH * 2 - 1;
    public static final int GAME_HEIGHT = Chamber.HEIGHT * 2 - 1;
    public static final int DISPLAY_HEIGHT = GAME_HEIGHT + Chamber.DIALOGUE_HEIGHT;
    
    public KeyBox keyBox;
    private JTextArea displayArea;

    // The view being displayed
    private GameView currentView;

    // Display state
    public Display display;

    public Game() {
        keyBox = new KeyBox();

        // Create the game display
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFocusable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        display = new Display(DISPLAY_HEIGHT, DISPLAY_WIDTH);

        displayArea.setText(display.toString());

        keyBox.frame.add(displayArea);
        keyBox.frame.pack();
        keyBox.frame.setVisible(true);

        currentView = new StartMenu(this);
    }

    // Update and render repeatedly, passing the time delta since the last update to the current view's update method.
    public void run() throws Display.RenderException {
        Instant then = Instant.now();
        while (true) {
            Instant now = Instant.now();
            currentView.update((double) Duration.between(then, now).toNanos() / 1e6);
            currentView.render();

            displayArea.setText(display.toString());
            then = now;
        }
    }

    public static void main(String args[]) {
        try {
            new Game().run();
        }
        catch (Display.RenderException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
