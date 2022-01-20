/*
 * Thomas: a class representing the game state regarding the player at a point in the game.
 * This should be serialized and saved to a save file when the user saves the game.
 */
public class PlayerState {
    // What items does the player have in his inventory?
    public ArrayList<GameItem> inventory;

    // Where can the player teleport to?
    public ArrayList<Teleporter> canTeleportTo;

    public PlayerState()
    {
        inventory = new ArrayList<>();
        canTeleportTo = new ArrayList<>();
    }
}