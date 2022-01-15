import java.awt.event.KeyEvent;
import java.util.ArrayList;

/*
 * Thomas: A class for game menus.
 * Uses lambdas for actions performed by menu items.
 */
public abstract class Menu extends GameView {
    // Menu items, and what each of them does
    protected ArrayList<MenuItem> items;

    // Which menu item is currently selected?
    protected int selected;
    
    public Menu(Game outerState, ArrayList<MenuItem> items) {
        super(outerState);
        
        this.items = items;
        selected = 0;
    }

    public Menu(Game outerState) {
        this(outerState, new ArrayList<>());
    }

    @Override
    public void update(double delta) {
        boolean upPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_UP);
        boolean downPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_DOWN);
        boolean selectPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_Z);
        
        if (upPressed) {
            selected = Math.max(selected - 1, 0);
        }

        if (downPressed) {
            selected = Math.min(selected + 1, items.size());
        }

        if (selectPressed) {
            items.get(selected).action.accept(outerState);
        }
    }
    
    @Override
    public void render() throws Display.RenderException {
        for (int i = 0; i < items.size(); i++) {
            if (i == selected) {
                outerState.display.print(" -> ");
            }
            else {
                outerState.display.print(" -  ");
            }

            outerState.display.println(items.get(i).name);
        }
    }
}
