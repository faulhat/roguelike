public class Spear extends Weapon {
    public Spear()
    {
        super("Spear");
    }

    @Override
    public int attackPoints()
    {
        return 7;
    }

    @Override
    public String description()
    {
        return "A spear with an iron head.\nATK 7";
    }

    @Override
    public Spear clone()
    {
        return new Spear();
    }
}
