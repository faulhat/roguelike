public interface Sprite {
    public boolean isVisible();

    public char symbol();

    public boolean isWalkable();

    public void onEvent(GameEvent e);
}
