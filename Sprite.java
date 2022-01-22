import java.util.Map;

/*
 * Thomas: this interface doesn't have to just represent a drawn sprite.
 * It can represent any object that exists at a certain place in the game.
 */
public abstract class Sprite implements DS.Storable {
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
    public Sprite(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
    {
        load(node);
    }

    // All-purpose event handling method.
    // Different event types should be dealt with separately.
    public abstract void onEvent(Game outerState, GameEvent e);

    // Load subclass-specific data from a DS.Node
    public abstract void loadUnique(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException;

    // Dump subclass-specific data to a DS.Node
    public abstract DS.Node dumpUnique();

    @Override
    public void load(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new SpriteLoadingException("Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.Node typeNode = asMap.get(":type");
        if (typeNode == null) {
            throw new SpriteLoadingException("No myType node found! (No mapping)");
        }

        if (!(typeNode instanceof DS.StringNode)) {
            throw new SpriteLoadingException("No myType node found! (Wrong type)");
        }

        myType = ((DS.StringNode) typeNode).value;

        DS.Node visibleNode = asMap.get(":visible");
        if (visibleNode == null) {
            throw new SpriteLoadingException("No 'visible' node found! (No mapping)");
        }

        if (!(visibleNode instanceof DS.IdNode) || !((DS.IdNode) visibleNode).isBool()) {
            throw new SpriteLoadingException("No 'visible' node found! (Wrong type)");
        }

        visible = ((DS.IdNode) visibleNode).isTrue();

        DS.Node walkableNode = asMap.get(":walkable");
        if (walkableNode == null) {
            throw new SpriteLoadingException("No 'walkable' node found! (No mapping)");
        }

        if (!(walkableNode instanceof DS.IdNode) || !((DS.IdNode) walkableNode).isBool()) {
            throw new SpriteLoadingException("No 'walkable' node found! (Wrong type)");
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

        DS.Node uniqueNode = asMap.get(":unique");
        if (uniqueNode == null) {
            throw new SpriteLoadingException("No unique info map found.");
        }

        loadUnique(uniqueNode);
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.add(new DS.KeywordNode("type"));
        outNode.add(new DS.StringNode(myType));
        outNode.add(new DS.KeywordNode("visible"));
        outNode.add(new DS.BoolNode(visible));
        outNode.add(new DS.KeywordNode("walkable"));
        outNode.add(new DS.BoolNode(walkable));
        
        if (visible && symbol != null) {
            outNode.add(new DS.KeywordNode("symbol"));
            outNode.add(new DS.StringNode("" + symbol));
        }

        outNode.add(new DS.KeywordNode("unique"));
        outNode.add(dumpUnique());

        return outNode;
    }
}
