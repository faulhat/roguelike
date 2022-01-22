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

    public ChamberMaze(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
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

    @Override
    public void load(DS.Node node) throws LoadingException, DS.Node.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new CMLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.Node widthNode = asMap.get(":width");
        if (widthNode == null) {
            throw new CMLoadingException("No width parameter found! (No mapping)");
        }

        if (!(widthNode instanceof DS.IntNode)) {
            throw new CMLoadingException("No width parameter found! (Wrong type)");
        }

        width = ((DS.IntNode) widthNode).value;

        DS.Node heightNode = asMap.get(":height");
        if (heightNode == null) {
            throw new CMLoadingException("No height parameter found! (No mapping)");
        }

        if (!(heightNode instanceof DS.IntNode)) {
            throw new CMLoadingException("No height parameter found! (Wrong type)");
        }

        height = ((DS.IntNode) heightNode).value;
        chambers = new Chamber[width][height];

        DS.Node matrixNode = asMap.get(":chambers");
        if (matrixNode == null) {
            throw new CMLoadingException("No chamber matrix found! (No mapping)");
        }

        if (!(matrixNode instanceof DS.VectorNode)) {
            throw new CMLoadingException("No chamber matrix found! (Wrong type)");
        }


        DS.VectorNode matrixVectorNode = (DS.VectorNode) matrixNode;
        if (matrixVectorNode.complexVal.size() != width) {
            throw new CMLoadingException("Chamber matrix has incorrect dimensions! (Wrong width)");
        }

        for (int i = 0; i < width; i++) {
            DS.Node colNode = matrixVectorNode.complexVal.get(i);

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