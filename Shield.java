/*
 * Thomas: a class for shields.
 */
public abstract class Shield extends GameItem {
    public Shield(String name)
    {
        super(name);
    }

    @Override
    public boolean isShield()
    {
        return true;
    }

    // Equips upon use, unless already equipped, in which case it un-equips
    @Override
    public void onUse(Game outerState)
    {
        if (outerState.playerState.equippedShield == this) {
            outerState.playerState.equippedShield = new Default();
        }
        else {
            outerState.playerState.equippedShield = this;
        }
    }

    @Override
    public int defensePoints()
    {
        return 3;
    }

    // The shield you start the game with.
    public static class Default extends Shield {
        public Default()
        {
            super("Cardboard");
        }

        @Override
        public Default clone()
        {
            return new Default();
        }

        @Override
        public String description()
        {
            return "A piece of cardboard you found on the ground.\n3 DEF";
        }

        // Since this is the default shield, it cannot be un-equipped
        @Override
        public void onUse(Game outerState)
        {
            outerState.playerState.equippedShield = this;
        }
    }
}
