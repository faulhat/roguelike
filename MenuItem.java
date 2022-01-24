/*
 * This class represents an item in a menu.
 * It contains a string to be printed in the menu and a consumer specifying its action.
 */
public class MenuItem {
    public String name;

    public Runnable action;

    public MenuItem(String name, Runnable action)
    {
        this.name = name;
        this.action = action;
    }
}