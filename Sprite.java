import javax.naming.OperationNotSupportedException;

/*
 * Thomas: this interface doesn't have to just represent a drawn sprite.
 * It can represent any object that exists at a certain place in the game.
 */
public interface Sprite {
    // Is this sprite visible?
    public boolean isVisible();

    // If so, what symbol should be used to render it?
    // Throw OperationNotSupported if this sprite is not visible.
    public char symbol() throws OperationNotSupportedException;

    // Can it be walked on?
    public boolean isWalkable();

    // All-purpose event handling method.
    // Different event types should be dealt with separately.
    public void onEvent(Game outerState, GameEvent e);
}
