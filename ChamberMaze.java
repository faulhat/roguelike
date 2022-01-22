import java.util.Map;
import java.util.Random;
/*
 * Thomas: this class is for a maze of chambers
 */

public class ChamberMaze implements DS.Storable {
    public static class CMLoadingException extends LoadingException {
        public CMLoadingException(String complaint)
        {
            super("ChamberMaze", complaint);
        }
    }

    public int width, height;

    public Maze maze;

    // A matrix of chambers representing this maze.
    public Chamber[][] chambers;

    public ChamberMaze()
    {
        init(5, 5, new Random(1));
    }

    public ChamberMaze(int width, int height, Random rand)
    {
        init(width, height, rand);
    }

    public ChamberMaze(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    public void init(int width, int height, Random rand)
    {
        this.width = width;
        this.height = height;

        maze = new Maze(width, height);
        maze.divRecursive(rand);

        // Generate the matrix of Chambers
        chambers = new Chamber[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                chambers[i][j] = new Chamber();
                chambers[i][j].genChamber(maze.getExits(i, j));
            }
        }

        chambers[0][0].encounterRate = 0.0;
        chambers[width - 1][height - 1].encounterRate = 0.0;
    }

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "ChamberMaze");
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new CMLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        width = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":width")).value;
        height = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":height")).value;

        chambers = new Chamber[width][height];

        DS.VectorNode matrixNode = (DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":matrix");
        if (matrixNode.complexVal.size() != width) {
            throw new CMLoadingException("Chamber matrix has incorrect dimensions! (Wrong width)");
        }

        for (int i = 0; i < width; i++) {
            DS.Node colNode = matrixNode.complexVal.get(i);

            if (!(colNode instanceof DS.VectorNode)) {
                throw new CMLoadingException("Invalid column vector.");
            }

            DS.VectorNode colVectorNode = (DS.VectorNode) colNode;
            if (colVectorNode.complexVal.size() != height) {
                throw new CMLoadingException("Chamber matrix has incorrect dimensions! (Wrong height)");
            }

            for (int j = 0; j < height; j++) {
                chambers[i][j] = new Chamber(colVectorNode.complexVal.get(j));
            }
        }
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.add(new DS.KeywordNode("width"));
        outNode.add(new DS.IntNode(width));
        outNode.add(new DS.KeywordNode("height"));
        outNode.add(new DS.IntNode(height));
        outNode.add(new DS.KeywordNode("chambers"));

        DS.VectorNode matrixNode = new DS.VectorNode();
        for (int i = 0; i < width; i++) {
            DS.VectorNode colNode = new DS.VectorNode();
            for (int j = 0; j < height; j++) {
                colNode.add(chambers[i][j].dump());
            }

            matrixNode.add(colNode);
        }

        outNode.add(matrixNode);
        return outNode;
    }
}