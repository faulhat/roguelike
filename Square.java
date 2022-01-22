import java.util.ArrayList;
import java.util.Map;

/*
 * Thomas: this class represents a square in a chamber.
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

    public Square(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
    {
        load(node);
    }

    public void onEvent(Game outerState, GameEvent e)
    {
        for (Sprite sprite : sprites) {
            sprite.onEvent(outerState, e);
        }
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new SquareLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.Node wallNode = asMap.get(":wall");
        if (wallNode == null) {
            throw new SquareLoadingException("No isWall node found! (No mapping)");
        }

        if (!(wallNode instanceof DS.IdNode) || !((DS.IdNode) wallNode).isBool()) {
            throw new SquareLoadingException("No isWall node found! (Wrong type)");
        }

        isWall = ((DS.IdNode) wallNode).isTrue();

        DS.Node spritesNode = asMap.get(":sprites");
        if (spritesNode == null) {
            throw new SquareLoadingException("No sprites node found! (No mapping)");
        }

        if (!(spritesNode instanceof DS.VectorNode)) {
            throw new SquareLoadingException("No sprites node found! (Wrong type)");
        }

        sprites = new ArrayList<>();
        for (DS.Node spriteNode : ((DS.VectorNode) spritesNode).complexVal) {
            if (!(spriteNode instanceof DS.MapNode)) {
                throw new Sprite.SpriteLoadingException("Must be a map node.");
            }
    
            Map<String, DS.Node> spriteAsMap = ((DS.MapNode) spriteNode).getMap();
            DS.Node typeNode = spriteAsMap.get(":type");
            if (typeNode == null) {
                throw new Sprite.SpriteLoadingException("No myType node found! (No mapping)");
            }
    
            if (!(typeNode instanceof DS.StringNode)) {
                throw new Sprite.SpriteLoadingException("No myType node found! (Wrong type)");
            }

            String spriteType = ((DS.StringNode) typeNode).value;
            if (spriteType.equals("Teleporter")) {
                sprites.add(new Teleporter(spriteNode));
            }
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.add(new DS.KeywordNode("wall"));
        outNode.add(new DS.BoolNode(isWall));
        outNode.add(new DS.KeywordNode("sprites"));

        DS.VectorNode spritesNode = new DS.VectorNode();
        for (Sprite sprite : sprites)
        {
            spritesNode.add(sprite.dump());
        }

        outNode.add(spritesNode);
        return outNode;
    }
}