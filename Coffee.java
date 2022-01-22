/*
 * Thomas: another game item. A cup of coffee.
 * Increases the player's speed in battle.
 */
public class Coffee extends GameItem {
    public static final double FACTOR = 0.8;

    public Coffee()
    {
        super("Coffee");
    }

    @Override
    public String description()
    {
        return "I'm a regular joe and I like my joe regular\nIncreases speed by 25% for 5 turns.";
    }

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
    public void onUse(Game outerState)
    {
        outerState.playerState.inventory.remove(this);

        new Caffeine(outerState.playerState);
    }
}
