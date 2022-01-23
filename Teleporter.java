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

    public int toLevel;

    public Point toLocation;

    public Point toPosition;

    public Teleporter(int toLevel, Point toLocation, Point toPosition)
    {
        super("Teleporter", false, '#');

        this.toLevel = toLevel;
        this.toLocation = toLocation;
        this.toPosition = toPosition;
    }

    public Teleporter(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        super(node);
    }

    @Override
    public Teleporter clone()
    {
        return new Teleporter(toLevel, new Point(toLocation.x, toLocation.y), new Point(toPosition.x, toPosition.y));
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Teleporter) {
            Teleporter oTeleporter = (Teleporter) other;
            if (toLevel == oTeleporter.toLevel && toLocation.equals(oTeleporter.toLocation) && toPosition.equals(oTeleporter.toPosition)) {
                return true;
            }
        }

        return false;
    }

    public void transport(Game outerState)
    {
        outerState.currentView = new ChamberView(outerState, toLevel, toLocation, toPosition);
        outerState.playerState.teleporters.add(this);
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
        toLevel = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":to-level")).value;

        DS.Node toLocNode = DS.MapNode.getAndValidate(asMap, DS.VectorNode.class, ":to-location", "Teleporter");
        toLocation = new DSPoint(toLocNode);

        DS.Node toPosNode = DS.MapNode.getAndValidate(asMap, DS.VectorNode.class, "to-position", "Teleporter");
        toPosition = new DSPoint(toPosNode);
    }

    @Override
    public DS.Node dumpUnique()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("to-level");
        outNode.add(new DS.IntNode(toLevel));

        outNode.addKey("to-location");
        DS.VectorNode toLocNode = new DS.VectorNode();
        toLocNode.add(new DS.IntNode(toLocation.x));
        toLocNode.add(new DS.IntNode(toLocation.y));
        outNode.add(toLocNode);

        outNode.addKey("to-position");
        DS.VectorNode toPosNode = new DS.VectorNode();
        toPosNode.add(new DS.IntNode(toPosition.x));
        toPosNode.add(new DS.IntNode(toPosition.y));
        outNode.add(toPosNode);

        return outNode;
    }
}