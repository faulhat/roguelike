import java.util.ArrayList;

/*
 * Thomas: a class representing the game state regarding the player at a point in the game.
 * This should be serialized and saved to a save file when the user saves the game.
 */
public class PlayerState {
    public static int MAX_ITEMS = 8;

    // What items does the player have in his inventory?
    public ArrayList<GameItem> inventory;

    // Where can the player teleport to?
    public ArrayList<Teleporter> canTeleportTo;

    public int hitPoints;

    public PlayerState()
    {
        inventory = new ArrayList<>();
        canTeleportTo = new ArrayList<>();
        hitPoints = 5;
    }
}