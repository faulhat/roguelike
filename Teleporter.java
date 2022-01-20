import java.awt.Point;

/*
 * Thomas: A class for a Teleporter sprite
 * It takes you to a different map from the current one.
 */
public class Teleporter extends Sprite {
    public String name;

    public ChamberMaze toMaze;

    public Point toLocation;

    public Point toPosition;

    public Teleporter(String name, ChamberMaze toMaze, Point toLocation, Point toPosition)
    {
        this.name = name;
        this.toMaze = toMaze;
        this.toLocation = toLocation;
        this.toPosition = toPosition;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public char symbol()
    {
        return '=';
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    public void transport(Game outerState)
    {
        outerState.currentView = new ChamberView(outerState, toMaze, toLocation, toPosition);
        outerState.playerState.canTeleportTo.add(this);
    }

    @Override
    public onEvent(Game outerState, GameEvent e)
    {
        if (!(e instanceof InteractEvent)) {
            return;
        }

        transport(outerState);
    }
}