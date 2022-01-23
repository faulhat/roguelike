import javax.naming.OperationNotSupportedException;

/*
 * Thomas: this class represents an item in the game
 * Items must be fungible and have names which are unique by subclass
 */
public abstract class GameItem implements Cloneable {
    public static class ItemLoadingException extends DS.Storable.LoadingException {
        public ItemLoadingException(String complaint)
        {
            super("GameItem", complaint);
        }
    }

    protected String name;

    public String getName()
    {
        return name;
    }

    public GameItem(String name)
    {
        this.name = name;
    }

    // clone method should always return an instance of the calling subclass created with its default constructor.
    @Override
    public abstract GameItem clone();

    // Get this item's attack points. (no such value by default)
    public int attackPoints() throws OperationNotSupportedException
    {
        throw new OperationNotSupportedException();
    }

    // Get this item's defense points. (no such value by default)
    public int defensePoints() throws OperationNotSupportedException
    {
        throw new OperationNotSupportedException();
    }

    // Get a description of this item
    public abstract String description();

    // Is this item a weapon? (false by default)
    public boolean isWeapon()
    {
        return false;
    }

    // Is this item a shield? (false by default)
    public boolean isShield()
    {
        return false;
    }

    // What does the item do when used?
    public abstract void onUse(Game outerState);

    // Format that description
    public String getDescWrap()
    {
        return Game.wrapString(description());
    }

    // Since all items of one subclass have the same name and all their properties are the same,
    // the name is all we need to serialize.
    public DS.Node dumpItem()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("name");
        outNode.add(new DS.StringNode(name));

        return outNode;
    }

    public static String getNameFromNode(DS.Node node) throws DS.Storable.LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new ItemLoadingException("Must be a map node.");
        }

        return ((DS.StringNode) DS.MapNode.getAndValidate(((DS.MapNode) node).getMap(), DS.StringNode.class, ":name", "GameItem")).value;
    }

    // Get a GameItem from a node.
    public static GameItem loadFromName(DS.Node node) throws DS.Storable.LoadingException, DS.NonDeserializableException
    {
        String className = getNameFromNode(node);

        if (className.equals("Coffee")) {
            return new Coffee();
        }

        if (className.equals("Cookie")) {
            return new Cookie();
        }

        if (className.equals("Fists")) {
            return new Weapon.Default();
        }

        if (className.equals("Cardboard")) {
            return new Shield.Default();
        }

        throw new ItemLoadingException("Invalid class name.");
    }
}