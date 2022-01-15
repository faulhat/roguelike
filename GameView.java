/*
 * Thomas: this class represents a view in the game.
 * This could be a menu, the inventory screen, the overworld...
 * Basically just any state that the game can be in.
 */
public abstract class GameView {
    // The Game instance that this object belongs to.
    protected final Game outerState;

    public GameView(Game outerState) {
        this.outerState = outerState;
    }

    public abstract void update(double delta);

    // Renders this state to outerState.display (see Display class).
    public abstract void render() throws Display.RenderException;
}
