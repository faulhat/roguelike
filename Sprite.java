import java.util.Map;

/*
 * Thomas: this interface doesn't have to just represent a drawn sprite.
 * It can represent any object that exists at a certain place in the game.
 */
public abstract class Sprite implements DS.Storable, Cloneable {
    public static class SpriteLoadingException extends LoadingException {
        public SpriteLoadingException(String complaint)
        {
            super("Sprite", complaint);
        }
    }

    public String myType;

    // Is this sprite visible?
    public boolean visible;

    // If so, what symbol should be used to render it?
    // Should be null if visible is false
    public Character symbol;

    // Can it be walked on?
    public boolean walkable;

    // Constructor for a visible sprite
    // char is used instead of Character for symbol because it can't be null in this case
    public Sprite(String myType, boolean walkable, char symbol)
    {
        this.myType = myType;
        this.walkable = walkable;
        this.symbol = symbol;
        visible = true;
    }

    // Constructor for an invisible sprite
    public Sprite(String myType, boolean walkable)
    {
        this.myType = myType;
        this.walkable = walkable;
        symbol = null;
        visible = false;
    }

    // Construct from DS.Node
    public Sprite(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    // Create a copy of this sprite
    @Override
    public abstract Sprite clone();

    // All-purpose event handling method.
    // Different event types should be dealt with separately.
    public abstract void onEvent(Game outerState, GameEvent e);

    // Load subclass-specific data from a DS.Node
    public abstract void loadUnique(DS.Node node) throws LoadingException, DS.NonDeserializableException;

    // Dump subclass-specific data to a DS.Node
    public abstract DS.Node dumpUnique();

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "Sprite");
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new SpriteLoadingException("Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        myType = ((DS.StringNode) getAndValidate(asMap, DS.StringNode.class, ":type")).value;

        DS.IdNode visibleNode = (DS.IdNode) getAndValidate(asMap, DS.IdNode.class, ":visible");
        if (!visibleNode.isBool()) {
            throw new SpriteLoadingException("'visible' node is not a valid boolean.");
        }

        visible = ((DS.IdNode) visibleNode).isTrue();

        DS.IdNode walkableNode = (DS.IdNode) getAndValidate(asMap, DS.IdNode.class, ":walkable");
        if (!walkableNode.isBool()) {
            throw new SpriteLoadingException("'walkable' node is not a valid boolean.");
        }

        walkable = ((DS.IdNode) walkableNode).isTrue();

        DS.Node symbolNode = asMap.get(":symbol");
        if (symbolNode == null) {
            if (visible) {
                throw new SpriteLoadingException("Sprite is visible but has no symbol.");
            }

            symbol = null;
        }
        else {
            if (!(symbolNode instanceof DS.StringNode)) {
                throw new SpriteLoadingException("Invalid symbol node. (Wrong type)");
            }

            String symbolString = ((DS.StringNode) symbolNode).value;
            if (symbolString.length() != 1) {
                throw new SpriteLoadingException("Invalid symbol node. (Must be one char)");
            }

            symbol = symbolString.charAt(0);
        }

        DS.Node uniqueNode = getAndValidate(asMap, DS.MapNode.class, ":unique");
        loadUnique(uniqueNode);
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("type");
        outNode.add(new DS.StringNode(myType));
        outNode.addKey("visible");
        outNode.add(new DS.BoolNode(visible));
        outNode.addKey("walkable");
        outNode.add(new DS.BoolNode(walkable));

        if (visible && symbol != null) {
            outNode.addKey("symbol");
            outNode.add(new DS.StringNode("" + symbol));
        }

        outNode.addKey("unique");
        outNode.add(dumpUnique());

        return outNode;
    }
}
