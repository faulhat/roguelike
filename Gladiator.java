/*
 * Gladiators were Rome's most popular sportsmen and her most ruthless warriors.
 * Boss of stage 2.
 */
public class Gladiator extends Enemy {
    public static class CastNet extends Spell {
        public CastNet(GameCharacter user, GameCharacter target)
        {
            super(false, user, target, 2);
        }

        @Override
        public String apply()
        {
            target.waitPeriod *= 2;
            return user.name + " casts his net over you! Your movement is restricted.";
        }

        @Override
        public String unapply()
        {
            target.waitPeriod /= 2;
            return "You broke free from " + user.name + "'s net.";
        }
    }

    private int counter;

    private Gladiator(String name)
    {
        super(name, 20, 6, 4, 30, 5000.0);

        approachMessage = "A man engineered in body and spirit by Mars himself stares you down.";
        attackMessage = name + " thrusts his trident at you!";
        timeLeft = 5000.0;
        counter = 0;
    }

    public Gladiator()
    {
        this("Gladiator");
    }

    public Gladiator(int n)
    {
        this("Gladiator " + n);
    }

    @Override
    public char getSymbol()
    {
        return 'G';
    }

    @Override
    public Spell getNextSpell(Game outerState)
    {
        counter = Math.max(counter - 1, 0);

        if (counter == 0 && outerState.rand.nextDouble() < 0.1) {
            counter = 2;
            return new CastNet(this, outerState.playerState);
        }

        return null;
    }

    @Override
    public String onDeath(Game outerState)
    {
        outerState.currentView = new LevelTransition.Transition2(outerState);
        return "";
    }

    public Gladiator getNew()
    {
        return new Gladiator(name);
    }
}
