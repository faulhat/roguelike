import java.awt.event.*;

public class TitleScreen extends GameView {
    public static enum State { TITLE, CONTROLS }

    public State state;

    public TitleScreen(Game outerState)
    {
        super(outerState);

        state = State.TITLE;
    }
    
    @Override
    public void update(double delta)
    {
        boolean selectPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ENTER, KeyEvent.VK_Z);

        if (selectPressed) {
            if (state == State.TITLE) {
                state = State.CONTROLS;
            }
            else {
                outerState.currentView = new StartMenu(outerState);
            }
        }
    }

    @Override
    public String render()
    {
        String out;
        if (state == State.TITLE) {
            String spaces = Repeat.repeat(" ", 6);
            out = "\n\n\n";
            out += spaces + "DE RE PUBLICA\n\n";
            // they're in alphabetical order before anyone gets upset.
            out += spaces + "A game by:\n\n" + spaces + "Athenase, Lukas, Ricardo,\n\n" + spaces + "Thomas, and Tobias.\n\n";
            out += spaces + "Z or ENTER to start...";
        }
        else {
            out = "CONTROLS\n\n";
            out += "Z/ENTER to interact, select, or continue.\n\n";
            out += "X/ESC to cancel.\n\n";
            out += "P to pause.\n\n";
            out += "I to view player inventory.";
        }

        return out;
    }
}
