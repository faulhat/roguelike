/*
 * A class for shields.
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
            outerState.playerState.equipShield(new Default());
        }
        else {
            outerState.playerState.equipShield(this);
        }
    }

    public abstract int defensePoints();

    // The shield you start the game with.
    public static class Default extends Shield {
        public Default()
        {
            super("WoodenShield");
        }

        @Override
        public Default clone()
        {
            return new Default();
        }

        @Override
        public int defensePoints()
        {
            return 4;
        }

        @Override
        public String description()
        {
            return "A piece of wood you found on the ground.\n4 DEF";
        }

        // Since this is the default shield, it cannot be un-equipped
        @Override
        public void onUse(Game outerState)
        {
            outerState.playerState.equipShield(this);
        }
    }
}
