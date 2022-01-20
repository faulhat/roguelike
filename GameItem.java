/*
 * Thomas: this item represents an item in the game
 */
public interface GameItem {
    // What is this item called?
    public String getName();

    // What does the item do when used?
    public void onUse(Game outerState);

    // Give a description of this item
    public String getDescription();
}