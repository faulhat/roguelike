public class FederalAgent extends Enemy {
    public int counter;

    public static class MKSpell extends Spell {
        public boolean effective;

        public int turnsRemaining;

        public MKSpell()
        {
            isGood = false;
            turnsRemaining = 4;
        }

        @Override
        public String apply(GameCharacter user, GameCharacter target)
        {
            String message = user.name + " used secret MKUltra tech.";

            if (target.defensePoints == 0) {
                effective = false;
                return message + "\nIt had no effect.";
            }

            target.defensePoints -= 1;
            effective = false;
            return message + "\n" + target.name + "'s def went down by 1.";
        }

        @Override
        public String eachTurn(GameCharacter target)
        {
            if (!effective) {
                return null;
            }

            turnsRemaining--;
            if (turnsRemaining == 0) {
                effective = false;
                return target.name + " has been freed from CIA mind control!\nDef +1";
            }

            return null;
        }
    }

    private FederalAgent(String name)
    {
        super(name, 12, 2, 2, 3, 7000.0);

        approachMessage = "You suddenly see a bright green glow in the darkness...";
        attackMessage = "Prepare for some freedom!";
        counter = 0;
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
                return new MKSpell();
            }

            return null;
        }
        finally {
            counter++;
        }
    }
}
