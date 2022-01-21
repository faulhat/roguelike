public abstract class Weapon extends GameItem {
    public int attackPoints;

    public static class Default extends Weapon {
        public Default()
        {
            attackPoints = 4;
        }

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
    }
}