public abstract class Shield extends GameItem {
    public int defensePoints;

    public static class Default extends Shield {
        public Default()
        {
            defensePoints = 3;
        }

        @Override
        public String getName()
        {
            return "Cardboard shield";
        }

        @Override
        public String getDescription()
        {
            return "A piece of cardboard you found on the ground.\n1 defense point.";
        }

        @Override
        public void onUse(Game outerState)
        {
            outerState.playerState.equippedShield = this;
        }
    }
}
