public class FederalAgent extends Enemy {
    public int counter;

    public static class MKSpell extends Spell {
        public int turnsRemaining;

        public MKSpell(GameCharacter user, GameCharacter target)
        {
            super(false, user, target, 4);
        }

        @Override
        public String apply()
        {
            String message = user.name + " used secret MKUltra tech.";

            if (target.defensePoints == 0) {
                target.spellsAffecting.remove(this);
                return message + "\nIt had no effect.";
            }

            target.defensePoints -= 1;
            return message + "\n" + target.name + "'s DEF went down by 1.";
        }

        @Override
        public String unapply()
        {
            target.defensePoints++;

            return target.name + "has been freed from CIA mind control!\nDEF +1";
        }
    }

    private FederalAgent(String name)
    {
        super(name, 3, 2, 2, 10, 7000.0);

        approachMessage = "A glowing being swiftly approaches...";
        attackMessage = "Prepare for some freedom!";
        counter = 0;
        timeLeft = 9000.0;
    }

    public FederalAgent()
    {
        this("Fed");
    }

    public FederalAgent(int n)
    {
        this("Fed #" + n);
    }

    @Override
    public char getSymbol()
    {
        return 'F';
    }

    @Override
    public Spell getNextSpell(Game outerState)
    {
        try {
            if (counter % 3 == 0) {
                return new MKSpell(this, outerState.playerState);
            }

            return null;
        }
        finally {
            timeLeft = waitPeriod;
            counter++;
        }
    }
}
