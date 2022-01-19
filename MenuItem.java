import java.util.function.Consumer;

/*
 * Thomas: this class represents an item in a menu.
 * It contains a string to be printed in the menu and a consumer specifying its action.
 */
public class MenuItem {
    public final String name;

    public final Consumer<Game> action;

    public MenuItem(String name, Consumer<Game> action)
    {
        this.name = name;
        this.action = action;
    }
}