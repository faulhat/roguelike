/*
 * Thomas: another game item. A cup of coffee.
 * Increases the player's speed in battle.
 */
public class Coffee extends GameItem {
    public static final double FACTOR = 0.7;

    public static class Caffeine extends Spell {
        public int counter;

        public Caffeine(GameCharacter user)
        {
            super(true, user, user, 5);

            counter = 0;
        }

        @Override
        public String apply()
        {
            user.waitPeriod *= FACTOR;

            return "You go faster now!";
        }

        @Override
        public String unapply()
        {
            user.waitPeriod /= FACTOR;

            return "Coffee wore off...";
        }
    }

    @Override
    public String getName()
    {
        return "Coffee";
    }

    @Override
    public String getDescription()
    {
        return "A simple cup of coffee";
    }

    @Override
    public void onUse(Game outerState)
    {
        outerState.playerState.inventory.remove(this);

        new Caffeine(outerState.playerState);
    }
}
