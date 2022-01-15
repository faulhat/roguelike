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
    private JTextArea display;

    // The chamber the player is in
    private Chamber currentChamber;

    // The view being displayed
    private GameView currentView;

    public Game(Chamber startingChamber) {
        keyBox = new KeyBox();

        // Create the game display
        display = new JTextArea();
        display.setEditable(false);
        display.setFocusable(false);
        display.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        String placeholder = "";
        for (int i = 0; i < DISPLAY_HEIGHT; i++) {
            for (int j = 0; j < DISPLAY_WIDTH; j++) {
                placeholder += ' ';
            }

            placeholder += '\n';
        }

        display.setText(placeholder);

        keyBox.frame.add(display);
        keyBox.frame.pack();
        keyBox.frame.setVisible(true);

        currentChamber = startingChamber;
        startingChamber.outerState = this;
        currentView = currentChamber;
    }

    // Update and render repeatedly, passing the time delta since the last update to the current view's update method.
    public void run() {
        Instant then = Instant.now();
        while (true) {
            Instant now = Instant.now();
            currentView.update((double) Duration.between(then, now).toNanos() / 1e6);
        
            display.setText(currentView.render());
            then = now;
        }
    }

    public static void main(String args[]) {
        //
    }
}
