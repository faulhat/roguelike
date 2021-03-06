import java.util.ArrayList;
import java.util.Map;

/*
 * This class represents a square in a chamber.
 * It may be a wall, and it may have sprites on it.
 */
public class Square implements DS.Storable {
    public static class SquareLoadingException extends LoadingException {
        public SquareLoadingException(String complaint)
        {
            super("Square", complaint);
        }
    }

    public boolean isWall;

    public ArrayList<Sprite> sprites;

    public Square(boolean isWall)
    {
        this.isWall = isWall;
        sprites = new ArrayList<>();
    }

    public Square(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    // Copy constructor
    public Square(Square other)
    {
        isWall = other.isWall;

        sprites = new ArrayList<>();
        for (Sprite sprite : other.sprites) {
            sprites.add(sprite.clone());
        }
    }

    public void onEvent(Game outerState, GameEvent e)
    {
        ArrayList<Sprite> spritesCopy = new ArrayList<>();
        spritesCopy.addAll(sprites);
        for (Sprite sprite : spritesCopy) {
            sprite.onEvent(outerState, e);
        }
    }

    public static DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "Square");
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Square) {
            Square oSquare = (Square) other;
            if (isWall == oSquare.isWall) {
                if (sprites.size() == oSquare.sprites.size()) {
                    for (int i = 0; i < sprites.size(); i++) {
                        if (!sprites.get(i).equals(oSquare.sprites.get(i))) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new SquareLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.Node wallNode = getAndValidate(asMap, DS.IdNode.class, "wall");

        if (!((DS.IdNode) wallNode).isBool()) {
            throw new SquareLoadingException("isWall node is not a valid boolean.");
        }

        isWall = ((DS.IdNode) wallNode).isTrue();

        DS.VectorNode spritesNode = (DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, "sprites");

        sprites = new ArrayList<>();
        for (DS.Node spriteNode : spritesNode.complexVal) {
            if (!(spriteNode instanceof DS.MapNode)) {
                throw new Sprite.SpriteLoadingException("Must be a map node.");
            }

            Map<String, DS.Node> spriteAsMap = ((DS.MapNode) spriteNode).getMap();
            String spriteType = ((DS.StringNode) getAndValidate(spriteAsMap, DS.StringNode.class, "type")).value;
            if (spriteType.equals("Teleporter")) {
                sprites.add(new Teleporter(spriteNode));
            }
            else if (spriteType.equals("BossFight")) {
                sprites.add(new BossFight(spriteNode));
            }
            else if (spriteType.equals("Merchant")) {
                sprites.add(new Merchant(spriteNode));
            }
            else {
                throw new SquareLoadingException("Invalid sprite class name: " + spriteType);
            }
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("wall");
        outNode.add(new DS.BoolNode(isWall));
        outNode.addKey("sprites");

        DS.VectorNode spritesNode = new DS.VectorNode();
        for (Sprite sprite : sprites) {
            spritesNode.add(sprite.dump());
        }

        outNode.add(spritesNode);
        return outNode;
    }
}