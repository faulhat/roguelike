/*
 * Thomas: Praetorians were elite soldiers who served as bodyguards for consuls and praetors, and later emperors.
 * They were among the most feared men in Rome and could control Roman politics with a weak emperor on the throne.
 * Overworld enemy for stage 3.
 */
public class Praetorian extends Enemy {
    private Praetorian(String name)
    {
        super(name, 15, 5, 4, 20, 8000.0);

        approachMessage = "An older soldier approaches with fasces in one hand and a sword in the other.";
        attackMessage = name + " strikes decisively!";
        timeLeft = 4000.0;
    }

    public Praetorian()
    {
        this("Praetorian");
    }

    public Praetorian(int n)
    {
        this("Praetorian " + n);
    }

    @Override
    public char getSymbol()
    {
        return 'P';
    }

    @Override
    public Praetorian getNew()
    {
        return new Praetorian(name);
    }
}
