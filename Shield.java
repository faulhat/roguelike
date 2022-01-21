public abstract class Shield extends GameItem {
    public abstract int getDefensePoints();

    public static class Default extends Shield {
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

        @Override
        public int getDefensePoints()
        {
            return 1;
        }
    }
}
