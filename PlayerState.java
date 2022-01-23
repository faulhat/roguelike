import java.util.ArrayList;
import java.util.Map;

/*
 * Thomas: a class representing the game state regarding the player at a point in the game.
 * This should be serialized and saved to a save file when the user saves the game.
 */
public class PlayerState extends GameCharacter implements DS.Storable {
    public static class StateLoadingException extends LoadingException {
        public StateLoadingException(String complaint)
        {
            super("PlayerState", complaint);
        }
    }

    // The player can have eight items at most.
    public static int MAX_ITEMS = 10;

    // Where can the player teleport to?
    public ArrayList<Teleporter> teleporters;

    // What items does the player have in his inventory?
    public ArrayList<GameItem> inventory;

    // The player's default waitPeriod
    public static final double WAIT_PERIOD = 7000.0;

    public GameItem equippedWeapon;

    public GameItem equippedShield;

    public int gold;

    public PlayerState()
    {
        super("Player", 20, 0, 0, WAIT_PERIOD, 0.0);

        equipWeapon(new Weapon.Default());
        equipShield(new Shield.Default());

        inventory = new ArrayList<>();
        teleporters = new ArrayList<>();

        gold = 0;
    }

    public PlayerState(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        super();

        name = "Player";
        load(node);
    }

    // Copy constructor
    public PlayerState(PlayerState other)
    {
        super("Player", other.hitPoints, 0, 0, WAIT_PERIOD, 0);

        equipWeapon((Weapon) other.equippedWeapon.clone());
        equipShield((Shield) other.equippedShield.clone());

        inventory = new ArrayList<>();
        for (GameItem item : other.inventory) {
            if (item == other.equippedWeapon && !(other.equippedWeapon instanceof Weapon.Default)) {
                inventory.add(equippedWeapon);
            }
            else if (item == other.equippedShield && !(other.equippedShield instanceof Shield.Default)) {
                inventory.add(equippedShield);
            }
            else {
                inventory.add(item.clone());
            }
        }

        gold = other.gold;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof PlayerState) {
            PlayerState oPlayer = (PlayerState) other;
            if (equippedWeapon.equals(oPlayer.equippedWeapon) && equippedShield.equals(oPlayer.equippedShield)) {
                if (inventory.size() == oPlayer.inventory.size()) {
                    for (int i = 0; i < inventory.size(); i++) {
                        if (!inventory.get(i).equals(oPlayer.inventory.get(i))) {
                            return false;
                        }
                    }

                    if (gold == oPlayer.gold) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public char getSymbol()
    {
        return '@';
    }

    public void equipWeapon(Weapon weapon)
    {
        equippedWeapon = weapon;
        attackPoints = weapon.attackPoints();
    }

    public void equipShield(Shield shield)
    {
        equippedShield = shield;
        baseDefensePoints = shield.defensePoints();
        defensePoints = baseDefensePoints;
    }

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "PlayerState");
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new StateLoadingException("Must be a map node.");
        }

        inventory = new ArrayList<>();

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        trueHitPoints = ((DS.FloatNode) getAndValidate(asMap, DS.FloatNode.class, ":true-hp")).value;
        hitPoints = (int) Math.ceil(trueHitPoints);

        equipWeapon((Weapon) GameItem.loadFromName((DS.MapNode) getAndValidate(asMap, DS.MapNode.class, ":weapon")));
        if (!(equippedWeapon instanceof Weapon.Default)) {
            inventory.add(equippedWeapon);
        }

        equipShield((Shield) GameItem.loadFromName((DS.MapNode) getAndValidate(asMap, DS.MapNode.class, ":shield")));
        if (!(equippedShield instanceof Shield.Default)) {
            inventory.add(equippedShield);
        }

        gold = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":dolla-dolla-bills")).value;
        DS.VectorNode itemsNode = (DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":items");
        for (DS.Node itemNode : itemsNode.complexVal) {
            inventory.add(GameItem.loadFromName(itemNode));
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.add(new DS.KeywordNode("true-hp"));
        outNode.add(new DS.FloatNode(trueHitPoints));
        outNode.add(new DS.KeywordNode("weapon"));
        outNode.add(equippedWeapon.dumpItem());
        outNode.add(new DS.KeywordNode("shield"));
        outNode.add(equippedShield.dumpItem());
        outNode.add(new DS.KeywordNode("dolla-dolla-bills"));
        outNode.add(new DS.IntNode(gold));

        outNode.add(new DS.KeywordNode("items"));
        DS.VectorNode inventoryNode = new DS.VectorNode();
        for (GameItem item : inventory) {
            if (item != equippedWeapon && item != equippedShield) {
                inventoryNode.add(item.dumpItem());
            }
        }

        outNode.add(inventoryNode);
        return outNode;
    }
}
