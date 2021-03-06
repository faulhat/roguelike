import javax.naming.OperationNotSupportedException;

/*
 * This class represents a view in the game.
 * This could be a menu, the inventory screen, the overworld...
 * Basically just any state that the game can be in.
 */
public abstract class GameView {
    // The Game instance that this object belongs to.
    public Game outerState;

    public PlayerState player;

    public GameView(Game outerState)
    {
        this.outerState = outerState;
        player = outerState.playerState;
    }

    public abstract void update(double delta) throws Exception;

    // Renders this state as a string
    public abstract String render() throws OperationNotSupportedException;
}
