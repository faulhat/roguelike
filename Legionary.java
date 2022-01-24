/*
 * Legionaries were more experienced than velites.
 * Overworld enemy for stage 2.
 */
public class Legionary extends Enemy {
    private Legionary(String name)
    {
        super(name, 12, 3, 3, 15, 8000.0);

        approachMessage = "An experienced soldier orders you to stop.";
        attackMessage = name + " lunges!";
        timeLeft = 5000.0;
    }

    public Legionary()
    {
        this("Legionary");
    }

    public Legionary(int n)
    {
        this("Legionary " + n);
    }

    @Override
    public char getSymbol()
    {
        return 'L';
    }

    @Override
    public String onDeath(Game outerState)
    {
        String out = "";
        if (outerState.playerState.inventory.size() < PlayerState.MAX_ITEMS) {
            outerState.playerState.inventory.add(new Bread());
            out = name + " dropped a loaf of bread!\n";
        }
        
        return out + super.onDeath(outerState);
    }

    @Override
    public Legionary getNew()
    {
        return new Legionary(name);
    }
}
