/*
 * Thomas: Legates (anglicized from Latin legati) were senior military officers and magistrates
 * who served under Rome's generals. They held imperium, the legal right to exercise their every whim,
 * and therefore only highly skilled military men attained this rank.
 * Boss for stage 1.
 */
public class Legate extends Enemy {
    public static class AmbushAttack extends Spell {
        public AmbushAttack(GameCharacter user, GameCharacter target)
        {
            super(false, user, target, 2);
        }

        @Override
        public String apply()
        {
            String out = user.name + "'s men decide to ambush you!\n";
            if (target.defensePoints <= 1) {
                target.spellsAffecting.remove(this);
                return out + "It had no effect.";
            }

            target.defensePoints--;
            return out + "Defense down! DEF: " + target.defensePoints;
        }

        @Override
        public String unapply()
        {
            target.defensePoints++;
            return user.name + "'s men back off. DEF: " + target.defensePoints;
        }
    }

    private int counter;

    private Legate(String name)
    {
        super(name, 13, 4, 4, 30, 5500.0);

        approachMessage = "An old commander orders his men to stand back as he challenges you.";
        attackMessage = name + " strikes!";
        timeLeft = 6000.0;
        counter = 5;
    }

    public Legate()
    {
        this("Legate");
    }

    public Legate(int n)
    {
        this("Legate " + n);
    }

    @Override
    public char getSymbol()
    {
        return 'C';
    }

    @Override
    public Spell getNextSpell(Game outerState)
    {
        counter = Math.max(counter - 1, 0);

        if (counter == 0) {
            counter = 6;
            return new AmbushAttack(this, outerState.playerState);
        }

        return null;
    }

    @Override
    public String onDeath(Game outerState)
    {
        outerState.currentView = new LevelTransition.Transition1(outerState);
        return "";
    }

    @Override
    public Legate getNew()
    {
        return new Legate(name);
    }
}
