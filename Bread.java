/*
 * Thomas: these are the main food item in the game
 */
public class Bread extends GameItem {
    public Bread()
    {
        super("Bread");
    }

    @Override
    public Bread clone()
    {
        return new Bread();
    }

    @Override
    public String description()
    {
        return "A loaf of hearty Roman bread.\nHeals 6 HP.";
    }

    @Override
    public void onUse(Game outerState)
    {
        outerState.playerState.inventory.remove(this);

        outerState.playerState.hitPoints += 6;
    }
}