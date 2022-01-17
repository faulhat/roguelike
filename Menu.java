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
    
    public Menu(Game outerState, ArrayList<MenuItem> items, GameView returnView) {
        super(outerState);
        
        this.items = items;
        selected = 0;

        this.returnView = returnView;
    }

    public Menu(Game outerState, GameView returnView) {
        this(outerState, new ArrayList<>(), returnView);
    }

    public Menu(Game outerState, ArrayList<MenuItem> items) {
        this(outerState, items, null);
    }

    public Menu(Game outerState) {
        this(outerState, new ArrayList<>(), null);
    } 

    @Override
    public void update(double delta) {
        boolean upPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_UP);
        boolean downPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_DOWN);
        boolean selectPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_Z) || outerState.keyBox.getReleaseKey(KeyEvent.VK_ENTER);
        boolean cancelPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_X) || outerState.keyBox.getReleaseKey(KeyEvent.VK_ESCAPE);

        if (cancelPressed && returnView != null) {
            outerState.currentView = returnView;
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
    public String render() {
        String out = "";
        for (int i = 0; i < items.size(); i++) {
            if (i == selected) {
                out += "-+> ";
            }
            else {
                out += " -  ";
            }

            out += items.get(i).name + "\n";
        }

        return out;
    }
}
