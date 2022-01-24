import java.awt.event.*;

public abstract class LevelTransition extends GameView {
    int level;

    String message;

    private LevelTransition(Game outerState, int level, String message)
    {
        super(outerState);
        this.level = level;
        this.message = message;
    }

    @Override
    public void update(double delta)
    {
        boolean selectPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ENTER, KeyEvent.VK_Z);

        if (selectPressed) {
            outerState.currentLevel = level;
            ChamberView nextView = new ChamberView(outerState, level);
            nextView.enterAt(0, 0, 5, 5);
            outerState.currentView = nextView;
        }
    }

    @Override
    public String render()
    {
        return Game.wrapString(message);
    }

    public static class StartTransition extends LevelTransition {
        public StartTransition(Game outerState)
        {
            super(outerState, 0, "The city of Rome has long been proud of its republican system of government.\nHowever, one of her greatest generals, SULLA, has marched his legions into the city.\nHe has become dictator over the republic and executed your father, a senator, and has jailed you.\nPress Z or ENTER to avenge your father.");
        }
    }

    public static class Transition1 extends LevelTransition {
        public Transition1(Game outerState)
        {
            super(outerState, 1, "You have escaped from jail, but now you will have to fight against Sulla's legionaries on the streets of Rome.\nPress Z or ENTER to continue.");
        }
    }

    public static class Transition2 extends LevelTransition {
        public Transition2(Game outerState)
        {
            super(outerState, 2, "You've reached Sulla's palace, but now you will have to contend with his armed bodyguards.\nPress Z or ENTER to continue.");
        }
    }

    public static class EndTransition extends LevelTransition {
        public EndTransition(Game outerState)
        {
            super(outerState, 0, "You've avenged your father and restored peace and dignity to the Roman Republic.\nFor this, you have not only been pardoned by the Senate,\nbut also named a hero of the Republic.\nYour run for the office of Quaestor next year will be smooth sailing!\nZ or ENTER to return to the main menu.");
        }

        @Override
        public void update(double delta)
        {
            boolean selectPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ENTER, KeyEvent.VK_Z);

            if (selectPressed) {
                outerState.currentView = new StartMenu(outerState);
            }
        }
    }
}
