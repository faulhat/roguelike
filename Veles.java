/*
 * Velites (sg. veles) were the lowest-ranking roman troops.
 * Overworld enemy for stage 1.
 */
public class Veles extends Enemy {
    private Veles(String name)
    {
        super(name, 6, 3, 2, 10, 9000.0);

        approachMessage = "A young soldier shivers as he draws his gladius...";
        attackMessage = name + " slashes at you weakly.";
        timeLeft = 4000.0;
    }

    public Veles()
    {
        this("Veles");
    }

    public Veles(int n)
    {
        this("Veles " + n);
    }

    @Override
    public char getSymbol()
    {
        return 'V';
    }

    @Override
    public Veles getNew()
    {
        return new Veles(name);
    }

    @Override
    public String onDeath(Game outerState)
    {
        String out = "";
        int nextInt = outerState.rand.nextInt(3);
        if (outerState.playerState.inventory.size() < PlayerState.MAX_ITEMS) {
            if (nextInt <= 1) {
                outerState.playerState.inventory.add(new Bread());
                out = "\n" + name + " dropped a loaf of bread!";
            }
            else {
                outerState.playerState.inventory.add(new Coffee());
                out = "\n" + name + " dropped a cup of coffee!\nIt landed upright.";
            }
        }

        return super.onDeath(outerState) + out;
    }
}
