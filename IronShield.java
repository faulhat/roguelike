public class IronShield extends Shield {
    public IronShield()
    {
        super("IronShield");
    }

    @Override
    public int defensePoints()
    {
        return 6;
    }

    @Override
    public String description()
    {
        return "An iron shield.\nDEF 6";
    }

    @Override
    public IronShield clone()
    {
        return new IronShield();
    }
}
