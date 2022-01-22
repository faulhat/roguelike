public abstract class Weapon extends GameItem {
    // Private, since only Default can have isDefault be true
    private Weapon(String name)
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
            outerState.playerState.equippedWeapon = new Default();
        }
        else {
            outerState.playerState.equippedWeapon = this;
        }
    }

    @Override
    public int attackPoints()
    {
        return 4;
    }

    // The weapon the player starts with
    public static class Default extends Weapon {
        public Default()
        {
            super("Fists");
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
            outerState.playerState.equippedWeapon = this;
        }
    }
}