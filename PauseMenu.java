/*
 * Thomas: a menu for when you pause the game.
 */
public class PauseMenu extends Menu {
    public PauseMenu(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        Runnable goBackAction = () -> {
            outerState.currentView = returnView;
        };

        Runnable exitAction = () -> {
            outerState.currentView = new StartMenu(outerState);
        };

        MenuItem goBack = new MenuItem("Return", goBackAction);
        items.add(goBack);

        if (returnView instanceof ChamberView) {
            Runnable saveAction = () -> {
                outerState.currentView = new SaveMenu(outerState, this, (ChamberView) returnView);
            };

            items.add(new MenuItem("Save game", saveAction));
        }

        MenuItem exitGame = new MenuItem("Return to main menu", exitAction);
        items.add(exitGame);
    }
}
