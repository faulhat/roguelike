public abstract class Weapon extends GameItem {
    public abstract int getAttackPoints();

    public static class Default extends Weapon {
        @Override
        public String getName()
        {
            return "Fists";
        }

        @Override
        public String getDescription()
        {
            return "Your own two hands.\n2 attack points.";
        }

        @Override
        public void onUse(Game outerState)
        {
            outerState.playerState.equippedWeapon = this;
        }

        @Override
        public int getAttackPoints()
        {
            return 2;
        }
    }
}