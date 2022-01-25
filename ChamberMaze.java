import java.util.Map;
import java.util.Random;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * This class is for a maze of chambers
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

    public int level;

    public Encounters encounters;

    public DSPoint bossLocation;

    public DSPoint merchantLocation;

    public ChamberMaze(int level)
    {
        this(level, 5, 5, new Random(1));
    }

    public ChamberMaze(int level, int width, int height, Random rand)
    {
        init(level, width, height, rand);
    }

    public ChamberMaze(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    // Copy constructor
    public ChamberMaze(ChamberMaze other)
    {
        level = other.level;
        width = other.width;
        height = other.height;
        bossLocation = new DSPoint(other.bossLocation);
        merchantLocation = new DSPoint(other.merchantLocation);

        chambers = new Chamber[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                chambers[i][j] = new Chamber(other.chambers[i][j]);
            }
        }

        loadFromLevel();
    }

    public void init(int level, int width, int height, Random rand)
    {
        this.level = level;
        this.width = width;
        this.height = height;

        maze = new Maze(width, height);
        maze.divRecursive(rand);

        // Find leaf nodes in the maze (cells with only one exit)
        // We will put the boss of this maze in one of them
        ArrayList<Point> leaves = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (maze.getExits(i, j).size() == 1) {
                    leaves.add(new Point(i, j));
                }
            }
        }

        // Generate the matrix of Chambers
        chambers = new Chamber[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                chambers[i][j] = new Chamber();
                chambers[i][j].genChamber(maze.getExits(i, j));
            }
        }

        // Don't put boss fight at start
        if (leaves.contains(new Point(0, 0))) {
            leaves.remove(new Point(0, 0));
        }

        bossLocation = new DSPoint(leaves.get(rand.nextInt(leaves.size())));
        chambers[bossLocation.x][bossLocation.y].putSprite(new Point(Chamber.WIDTH / 2, Chamber.HEIGHT / 2), new BossFight(level, bossLocation));
        chambers[bossLocation.x][bossLocation.y].encounterRate = 0.0;

        leaves.remove(bossLocation);
        merchantLocation = new DSPoint(leaves.get(rand.nextInt(leaves.size())));
        chambers[merchantLocation.x][merchantLocation.y].putSprite(new Point(Chamber.WIDTH / 2, Chamber.HEIGHT / 2), new Merchant(level));
        chambers[merchantLocation.x][merchantLocation.y].encounterRate = 0.0;

        chambers[0][0].encounterRate = 0.0;
        loadFromLevel();
    }

    public void loadFromLevel()
    {
        ArrayList<Encounter> encounterList = new ArrayList<>();
        ArrayList<Enemy> twoVelites = new ArrayList<>(Arrays.asList(new Veles(1), new Veles(2)));
        ArrayList<Enemy> velesAndLegionary = new ArrayList<>(Arrays.asList(new Legionary(), new Veles()));
        double encounterRate = 0.007;
        switch (level) {
        case 0:
            ArrayList<Enemy> oneVeles = new ArrayList<>(Arrays.asList(new Veles()));
            encounterList.add(new Encounter(oneVeles));
            encounterList.add(new Encounter(twoVelites));
            break;
        case 1:
            encounterList.add(new Encounter(twoVelites)); // Don't worry about copying; only one of these conditions can occur
            ArrayList<Enemy> oneLegionary = new ArrayList<>(Arrays.asList(new Legionary()));
            encounterList.add(new Encounter(oneLegionary));
            encounterList.add(new Encounter(velesAndLegionary));
            break;
        case 2:
            encounterList.add(new Encounter(velesAndLegionary));
            ArrayList<Enemy> twoLegionaries = new ArrayList<>(Arrays.asList(new Legionary(1), new Legionary(2)));
            encounterList.add(new Encounter(twoLegionaries));
            ArrayList<Enemy> onePraetorian = new ArrayList<>(Arrays.asList(new Praetorian()));
            encounterList.add(new Encounter(onePraetorian));
            encounterRate = 0.008;
        }

        encounters = new Encounters(encounterList);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if ((i == 0 && j == 0) || (i == bossLocation.x && j == bossLocation.y)) {
                    chambers[i][j].encounterRate = 0.0;
                }
                else {
                    chambers[i][j].encounterRate = encounterRate;
                }
            }
        }
    }

    public void putSprite(Point location, Point position, Sprite sprite)
    {
        chambers[location.x][location.y].putSprite(position, sprite);
    }

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "ChamberMaze");
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof ChamberMaze) {
            ChamberMaze oChamberMaze = (ChamberMaze) other;

            if (level == oChamberMaze.level && width == oChamberMaze.width && height == oChamberMaze.height) {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (!chambers[i][j].equals(oChamberMaze.chambers[i][j])) {
                            return false;
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new CMLoadingException("Must be a map node!");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        level = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":levelno")).value;
        width = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":width")).value;
        height = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":height")).value;
        bossLocation = new DSPoint(getAndValidate(asMap, DS.VectorNode.class, ":boss-loc"));
        merchantLocation = new DSPoint(getAndValidate(asMap, DS.VectorNode.class, ":merchant-loc"));

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

        loadFromLevel();
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("levelno");
        outNode.add(new DS.IntNode(level));
        outNode.addKey("width");
        outNode.add(new DS.IntNode(width));
        outNode.addKey("height");
        outNode.add(new DS.IntNode(height));
        outNode.addKey("boss-loc");
        outNode.add(bossLocation.dump());
        outNode.addKey("merchant-loc");
        outNode.add(merchantLocation.dump());
        outNode.addKey("matrix");

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