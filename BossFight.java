import java.awt.Point;
import java.util.Map;

/*
 * Thomas: this class represents a boss fight.
 * You have to interact with the sprite to start the battle.
 */
public class BossFight extends Sprite {
    public Enemy boss;

    int level;

    public Teleporter toNextLevel;

    public DSPoint location;

    public BossFight(int level, Point location) throws IllegalArgumentException
    {
        super("BossFight", false, 'B');

        this.level = level;
        loadFromLevel();

        toNextLevel = new Teleporter(level + 1, new Point(0, 0), new Point(5, 5));
        this.location = new DSPoint(location);
    }
    
    public BossFight(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        super(node);
    }

    public void loadFromLevel() throws IllegalArgumentException
    {
        switch (level) {
        case 0:
            boss = new Legate();
            break;
        case 1:
            boss = new Gladiator();
            break;
        case 2:
            boss = new Dictator();
            break;
        default:
            throw new IllegalArgumentException("There are only three levels in the game. Got: " + level);
        }

        toNextLevel = new Teleporter(level + 1, new Point(0, 0), new Point(5, 5));
    }

    @Override
    public BossFight clone() throws IllegalArgumentException
    {
        return new BossFight(level, new DSPoint(location));
    }

    @Override
    public void onEvent(Game outerState, GameEvent e)
    {
        if (e instanceof GameEvent.InteractEvent) {
            // I know this is awful but not only do I not care, I don't have time to care either.
            outerState.levels.get(level).chambers[location.x][location.y].putSprite(new Point(Chamber.WIDTH / 2, Chamber.HEIGHT / 2), toNextLevel);
            outerState.currentView = new BattleView(outerState, boss, outerState.currentView);
        }
    }

    @Override
    public DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
    {
        return DS.MapNode.getAndValidate(asMap, desired, key, "BossFight");
    }

    @Override
    public void loadUnique(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new LoadingException("BossFight", "Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        level = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":level")).value;
        loadFromLevel();

        location = new DSPoint(getAndValidate(asMap, DS.VectorNode.class, ":location"));
    }

    @Override
    public DS.Node dumpUnique()
    {
        DS.MapNode outNode = new DS.MapNode();
        outNode.addKey("level");
        outNode.add(new DS.IntNode(level));
        outNode.addKey("location");
        outNode.add(location.dump());

        return outNode;
    }
}
