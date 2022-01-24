public class Gladius extends Weapon {
    public Gladius()
    {
        super("Gladius");
    }

    @Override
    public int attackPoints()
    {
        return 6;
    }

    @Override
    public String description()
    {
        return "The sword of a Roman soldier.\nATK 6";
    }

    @Override
    public Gladius clone()
    {
        return new Gladius();
    }
}