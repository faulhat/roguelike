import java.util.ArrayList;

/*
 * Thomas: this class represents a square in a chamber.
 * It may be a wall, and it may have sprites on it.
 */
public class Square {
    public boolean isWall;

    public ArrayList<Sprite> sprites;

    public Square(boolean isWall)
    {
        this.isWall = isWall;
        sprites = new ArrayList<>();
    }

    public void onEvent(Game outerState, GameEvent e)
    {
        for (Sprite sprite : sprites) {
            sprite.onEvent(outerState, e);
        }
    }
}