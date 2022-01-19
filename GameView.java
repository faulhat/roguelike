import javax.naming.OperationNotSupportedException;

/*
 * Thomas: this class represents a view in the game.
 * This could be a menu, the inventory screen, the overworld...
 * Basically just any state that the game can be in.
 */
public abstract class GameView {
    // The Game instance that this object belongs to.
    protected final Game outerState;

    public GameView(Game outerState)
    {
        this.outerState = outerState;
    }

    public abstract void update(double delta) throws Exception;

    // Renders this state as a string
    public abstract String render() throws OperationNotSupportedException;
}
