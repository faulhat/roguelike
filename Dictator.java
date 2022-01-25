/*
 * Dictator was a political title in the Roman Republic, granted to generals in times of emergency.
 * It gave the general unilateral command over Rome's legions and the legal authority to raise new legions at will.
 * Final boss.
 */
public class Dictator extends Enemy {
    private Dictator(String name)
    {
        super(name, 22, 7, 5, 0, 5000.0);

        approachMessage = "A man who brought nations to their knees stands relaxed before you, a dagger at his side.";
        attackMessage = name + " slices at you!";
        timeLeft = 6000.0;
    }

    public Dictator()
    {
        this("Sulla");
    }

    @Override
    public char getSymbol()
    {
        return 'S';
    }

    @Override
    public String onDeath(Game outerState)
    {
        outerState.currentView = new LevelTransition.EndTransition(outerState);
        return "";
    }

    @Override
    public Dictator getNew()
    {
        return new Dictator(name);
    }
}
