public abstract class Weapon extends GameItem {
    public Weapon(String name)
    {
        super(name);
    }

    @Override
    public boolean isWeapon()
    {
        return true;
    }

    // Equip on use. Un-equip on use if already equipped
    @Override
    public void onUse(Game outerState)
    {
        if (outerState.playerState.equippedWeapon == this) {
            outerState.playerState.equipWeapon(new Default());
        }
        else {
            outerState.playerState.equipWeapon(this);
        }
    }

    public abstract int attackPoints();

    // The weapon the player starts with
    public static class Default extends Weapon {
        public Default()
        {
            super("Fists");
        }

        @Override
        public Default clone()
        {
            return new Default();
        }

        @Override
        public int attackPoints()
        {
            return 4;
        }

        @Override
        public String description()
        {
            return "Your own two hands.\n4 ATK.";
        }

        // Since this is the default, it can't be un-equipped
        @Override
        public void onUse(Game outerState)
        {
            outerState.playerState.equipWeapon(this);
        }
    }
}