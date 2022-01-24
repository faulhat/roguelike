/*
 * Another game item. A cup of coffee.
 * Increases the player's speed in battle.
 */
public class Coffee extends GameItem {
    public static final double FACTOR = 0.77;

    public Coffee()
    {
        super("Coffee");
    }

    @Override
    public Coffee clone()
    {
        return new Coffee();
    }

    @Override
    public String description()
    {
        return "I'm a regular joe and I like my joe regular\nIncreases speed by 30% for 4 turns.";
    }

    public static class Caffeine extends Spell {
        public Caffeine(GameCharacter user)
        {
            super(true, user, user, 4);
        }

        @Override
        public String apply()
        {
            target.waitPeriod *= FACTOR;

            return "You go faster now!";
        }

        @Override
        public String unapply()
        {
            target.waitPeriod /= FACTOR;

            return "Coffee wore off...";
        }
    }

    @Override
    public void onUse(Game outerState)
    {
        outerState.playerState.inventory.remove(this);

        new Caffeine(outerState.playerState).apply();
    }
}
