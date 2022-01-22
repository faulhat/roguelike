import java.awt.Point;
import java.util.Map;

/*
 * Thomas: A class for a Teleporter sprite
 * It takes you to a different map from the current one.
 */
public class Teleporter extends Sprite {
    public static class TeleporterLoadingException extends LoadingException {
        public TeleporterLoadingException(String complaint)
        {
            super("Teleporter", complaint);
        }
    }

    public ChamberMaze toMaze;

    public Point toLocation;

    public Point toPosition;

    public Teleporter(ChamberMaze toMaze, Point toLocation, Point toPosition)
    {
        super("Teleporter", false, '#');

        this.toMaze = toMaze;
        this.toLocation = toLocation;
        this.toPosition = toPosition;
    }

    public Teleporter(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        super(node);
    }

    public void transport(Game outerState)
    {
        outerState.currentView = new ChamberView(outerState, toMaze, toLocation, toPosition);
        outerState.playerState.canTeleportTo.add(this);
    }

    @Override
    public void onEvent(Game outerState, GameEvent e)
    {
        if (!(e instanceof GameEvent.InteractEvent)) {
            return;
        }

        transport(outerState);
    }

    @Override
    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "Teleporter");
    }

    @Override
    public void loadUnique(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new TeleporterLoadingException("Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.Node mazeNode = asMap.get(":to-maze");
        if (mazeNode == null) {
            throw new TeleporterLoadingException("No toMaze node found. (No mapping)");
        }

        toMaze = new ChamberMaze(mazeNode);

        DS.Node toLocNode = DS.MapNode.getAndValidate(asMap, DS.VectorNode.class, ":to-location", "Teleporter");
        toLocation = new DSPoint(toLocNode);

        DS.Node toPosNode = DS.MapNode.getAndValidate(asMap, DS.VectorNode.class, "to-position", "Teleporter");
        toPosition = new DSPoint(toPosNode);
    }

    @Override
    public DS.Node dumpUnique()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.add(new DS.KeywordNode("to-maze"));
        outNode.add(toMaze.dump());

        outNode.add(new DS.KeywordNode("to-location"));
        DS.VectorNode toLocNode = new DS.VectorNode();
        toLocNode.add(new DS.IntNode(toLocation.x));
        toLocNode.add(new DS.IntNode(toLocation.y));
        outNode.add(toLocNode);

        outNode.add(new DS.KeywordNode("to-position"));
        DS.VectorNode toPosNode = new DS.VectorNode();
        toPosNode.add(new DS.IntNode(toPosition.x));
        toPosNode.add(new DS.IntNode(toPosition.y));
        outNode.add(toPosNode);

        return outNode;
    }
}