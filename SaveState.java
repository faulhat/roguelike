import java.util.ArrayList;
import java.util.Map;
import java.awt.Point;

/*
 * This class represents a game save state, which can be serialized.
 */
public class SaveState implements DS.Storable {
    public static class SaveStateLoadingException extends LoadingException {
        public SaveStateLoadingException(String complaint)
        {
            super("SaveState", complaint);
        }
    }

    public ArrayList<ChamberMaze> levels;

    public int currentLevel;

    public DSPoint location;

    public DSPoint position;

    public PlayerState player;

    // This constructor deep-copies game state info.
    // This way it won't be updated as the game progresses.
    public SaveState(ArrayList<ChamberMaze> levels, int currentLevel, Point location, Point position, PlayerState player)
    {
        this.levels = new ArrayList<>();
        for (ChamberMaze level : levels) {
            // Invoke copy constructor
            this.levels.add(new ChamberMaze(level));
        }

        this.currentLevel = currentLevel;
        this.location = new DSPoint(location);
        this.position = new DSPoint(position);
        this.player = new PlayerState(player);
    }

    public SaveState(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    public SaveState(Game state, Point location, Point position)
    {
        this(state.levels, state.currentLevel, location, position, state.playerState);
    }

    public SaveState(Game state, ChamberView view)
    {
        this(state, view.location, new Point((int) view.position.x, (int) view.position.y));
    }

    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "SaveState");
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new SaveStateLoadingException("Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.VectorNode levelsNode = (DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":levels");
        levels = new ArrayList<>();
        for (DS.Node levelNode : levelsNode.complexVal) {
            levels.add(new ChamberMaze(levelNode));
        }

        currentLevel = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":on-level")).value;
        location = new DSPoint((DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":location"));
        position = new DSPoint((DS.VectorNode) getAndValidate(asMap, DS.VectorNode.class, ":position"));
        player = new PlayerState((DS.MapNode) getAndValidate(asMap, DS.MapNode.class, ":player"));
    }

    @Override
    public DS.Node dump()
    {
        DS.MapNode outNode = new DS.MapNode();

        outNode.addKey("levels");
        DS.VectorNode levelsNode = new DS.VectorNode();
        for (ChamberMaze level : levels) {
            levelsNode.add(level.dump());
        }

        outNode.add(levelsNode);

        outNode.addKey("on-level");
        outNode.add(new DS.IntNode(currentLevel));
        outNode.addKey("location");
        outNode.add(location.dump());
        outNode.addKey("position");
        outNode.add(position.dump());
        outNode.addKey("player");
        outNode.add(player.dump());

        return outNode;
    }
}
