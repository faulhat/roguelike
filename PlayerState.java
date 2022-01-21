import java.util.ArrayList;

/*
 * Thomas: a class representing the game state regarding the player at a point in the game.
 * This should be serialized and saved to a save file when the user saves the game.
 */
public class PlayerState extends GameCharacter {
    // The player can have eight items at most.
    public static int MAX_ITEMS = 8;

    // What items does the player have in his inventory?
    public ArrayList<GameItem> inventory;

    // Where can the player teleport to?
    public ArrayList<Teleporter> canTeleportTo;

    public double trueHitPoints;

    public int hitPoints;

    public Weapon equippedWeapon;

    public Shield equippedShield;

    public int gold;

    public PlayerState()
    {
        super(5, 0, 0);

        equipWeapon(new Weapon.Default());
        equipShield(new Shield.Default());

        inventory = new ArrayList<>();
        canTeleportTo = new ArrayList<>();

        gold = 0;
    }

    @Override
    public char getSymbol()
    {
        return '@';
    }

    public void equipWeapon(Weapon weapon)
    {
        equippedWeapon = weapon;
        attackPoints = weapon.getAttackPoints();
    }

    public void equipShield(Shield shield)
    {
        equippedShield = shield;
        defensePoints = shield.getDefensePoints();
    }
}