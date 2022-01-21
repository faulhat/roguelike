/*
 * Thomas: this class represents an item in the game
 */
public abstract class GameItem {
    // What is this item called?
    public abstract String getName();

    // What does the item do when used?
    public abstract void onUse(Game outerState);

    // Give a description of this item
    public abstract String getDescription();

    // Format that description
    public String getDescWrap()
    {
        return Game.wrapString(getDescription());
    }
}