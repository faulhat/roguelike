/*
 * Thomas: these are the main food item in the game
 * They recover 2 hit points each
 */
public class Cookie extends GameItem {
    @Override
    public String getName()
    {
        return "Cookie";
    }

    @Override
    public void onUse(Game outerState)
    {
        outerState.playerState.hitPoints += 2;
    }

    @Override
    public String getDescription()
    {
        return "One of those pepperidge farm chessmen cookies. Heals 2 HP.";
    }
}