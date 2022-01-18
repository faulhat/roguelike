import javax.naming.OperationNotSupportedException;
import javax.swing.JTextArea;
import java.awt.Font;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/*
 * Thomas: the main class that will run the whole thing
 * Manages both the Swing-based display and the game state.
 */
public class Game {
    // An exception for when it is requested that a string be rendered which cannot be
    public static class RenderException extends Exception {
        public RenderException(String in) {
            super("Error! From class Game: this String could not be rendered to the display: \"" + in + "\"");
        }
    }

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
    public GameView currentView;

    public Game() {
        keyBox = new KeyBox();

        // Create the game display
        displayArea = new JTextArea(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        displayArea.setEditable(false);
        displayArea.setFocusable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));

        keyBox.frame.add(displayArea);
        keyBox.frame.pack();
        keyBox.frame.setVisible(true);

        currentView = new StartMenu(this);
    }

    public void setDisplayText(String toDisplay) throws RenderException {
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
    public void run() throws RenderException, OperationNotSupportedException {
        Instant then = Instant.now();
        while (true) {
            Instant now = Instant.now();
            currentView.update((double) Duration.between(then, now).toNanos() / 1e6);

            setDisplayText(currentView.render());
            then = now;
        }
    }

    public static void main(String args[]) {
        try {
            new Game().run();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
