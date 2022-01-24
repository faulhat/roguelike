public class Fasces extends Weapon {
    public Fasces()
    {
        super("Fasces");
    }

    @Override
    public int attackPoints()
    {
        return 9;
    }

    @Override
    public String description()
    {
        return "An ax with reeds tied to it.\nThe weapon of a Praetorian\nATK 9";
    }

    @Override
    public Fasces clone()
    {
        return new Fasces();
    }
}
