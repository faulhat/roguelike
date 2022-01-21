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
        String desc = getDescription();

        String out = "";
        int col = 0;
        for (int i = 0; i < desc.length(); i++) {
            out += desc.charAt(i);

            if (col == Game.DISPLAY_WIDTH - 1) {
                out += "\n";
                col = 0;
            }

            col++;
        }

        return out;
    }
}