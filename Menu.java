import java.awt.event.KeyEvent;
import java.util.ArrayList;

/*
 * Thomas: A class for game menus.
 * Uses lambdas for actions performed by menu items.
 */
public class Menu extends GameView {
    // Menu items, and what each of them does
    protected ArrayList<MenuItem> items;

    // Which menu item is currently selected?
    protected int selected;

    // View to return to if the user presses the cancel key.
    // Cancel key does nothing if this is null.
    public GameView returnView;

    public Menu(Game outerState, ArrayList<MenuItem> items, GameView returnView)
    {
        super(outerState);

        this.items = items;
        selected = 0;

        this.returnView = returnView;
    }

    public Menu(Game outerState, GameView returnView)
    {
        this(outerState, new ArrayList<>(), returnView);
    }

    public Menu(Game outerState, ArrayList<MenuItem> items)
    {
        this(outerState, items, null);
    }

    public Menu(Game outerState)
    {
        this(outerState, new ArrayList<>(), null);
    }

    @Override
    public void update(double delta)
    {

        boolean upPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean downPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean selectPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_Z, KeyEvent.VK_ENTER);
        boolean cancelPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_X, KeyEvent.VK_ESCAPE);

        if (cancelPressed && returnView != null) {
            outerState.currentView = returnView;
            return;
        }

        if (items.size() == 0) {
            return;
        }

        if (upPressed) {
            selected = Math.max(selected - 1, 0);
        }

        if (downPressed) {
            selected = Math.min(selected + 1, items.size() - 1);
        }

        if (selectPressed) {
            items.get(selected).action.accept(outerState);
        }
    }

    @Override
    public String render()
    {
        String out = "";
        for (int i = 0; i < items.size(); i++) {
            if (i == selected) {
                out += " -> ";
            }
            else {
                out += "    ";
            }

            out += "- " + items.get(i).name + "\n";
        }

        return out;
    }
}
