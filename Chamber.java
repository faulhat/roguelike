import java.util.EnumSet;
import java.util.Map;
import java.awt.Point;

/*
 * Thomas: a class representing rooms in the game.
 * These rooms are constant in height and width.
 */
public class Chamber implements DS.Storable {
    public static class ChamberLoadingException extends LoadingException {
        public ChamberLoadingException(String complaint)
        {
            super("Chamber", complaint);
        }
    }

    // Constants

    // These are the height and width of the chamber object, not the display.
    public static final int WIDTH = 25;
    public static final int HEIGHT = 20;

    // A map of this Chamber
    public Square[][] squares;

    // What is the encounter rate in this chamber?
    public double encounterRate;

    // Constructor for empty Chamber
    public Chamber()
    {
        squares = new Square[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                squares[i][j] = new Square(false);
            }
        }

        encounterRate = 0.02;
    }

    // Construct from a DS.Node
    public Chamber(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    // Copy constructor
    public Chamber(Chamber other)
    {
        squares = new Square[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                squares[i][j] = new Square(other.squares[i][j]);
            }
        }

        encounterRate = other.encounterRate;
    }

    // Method to fill in Chamber
    // For now it just puts walls on each side with two-block exits in them according to the exits set provided
    public void genChamber(EnumSet<Direction> exits)
    {
        for (int i = 0; i < WIDTH; i++) {
            // Make an exit in the middle of the North wall.
            if (!(exits.contains(Direction.N) && (i == WIDTH / 2 || i == WIDTH / 2 + 1))) {
                squares[i][0].isWall = true;
            }

            // Make an exit in the middle of the South wall.
            if (!(exits.contains(Direction.S) && (i == WIDTH / 2 || i == WIDTH / 2 + 1))) {
                squares[i][HEIGHT - 1].isWall = true;
            }
        }

        for (int i = 0; i < HEIGHT; i++) {
            // Make an exit in the middle of the West wall.
            if (!(exits.contains(Direction.W) && (i == HEIGHT / 2 || i == HEIGHT / 2 + 1))) {
                squares[0][i].isWall = true;
            }

            // Make an exit in the middle of the East wall.
            if (!(exits.contains(Direction.E) && (i == HEIGHT / 2 || i == HEIGHT / 2 + 1))) {
                squares[WIDTH - 1][i].isWall = true;
            }
        }
    }

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "Chamber");
    }

    public void putSprite(Point position, Sprite sprite)
    {
        squares[position.x][position.y].sprites.add(sprite);
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new ChamberLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.VectorNode matrixNode = (DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":squares");
        if (matrixNode.complexVal.size() != WIDTH) {
            throw new ChamberLoadingException("Square matrix has incorrect dimensions! (wrong width)");
        }

        squares = new Square[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            DS.Node colNode = matrixNode.complexVal.get(i);
            if (!(colNode instanceof DS.VectorNode)) {
                throw new ChamberLoadingException("Invalid column vector.");
            }

            DS.VectorNode colVectorNode = (DS.VectorNode) colNode;
            if (colVectorNode.complexVal.size() != HEIGHT) {
                throw new ChamberLoadingException("Square matrix has incorrect dimensions! (wrong height)");
            }

            for (int j = 0; j < HEIGHT; j++) {
                squares[i][j] = new Square(colVectorNode.complexVal.get(j));
            }
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("encounterRate");
        outNode.add(new DS.FloatNode(encounterRate));
        outNode.addKey("squares");

        DS.VectorNode matrixNode = new DS.VectorNode();
        for (int i = 0; i < WIDTH; i++) {
            DS.VectorNode colNode = new DS.VectorNode();
            for (int j = 0; j < HEIGHT; j++) {
                colNode.add(squares[i][j].dump());
            }

            matrixNode.add(colNode);
        }

        return outNode;
    }
}