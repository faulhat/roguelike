/*
 * Thomas: these are the main food item in the game
 * They recover 2 hit points each
 */
public class Cookie extends GameItem {
    public Cookie()
    {
        super("Cookie");
    }

    @Override
    public String description()
    {
        return "A pepperidge farm chessmen cookie\nHeals 2 HP.";
    }

    @Override
    public void onUse(Game outerState)
    {
        outerState.playerState.inventory.remove(this);

        outerState.playerState.hitPoints += 2;
    }
}