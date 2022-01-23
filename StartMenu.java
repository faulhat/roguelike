/*
 * Thomas: menu for starting the game
 */
public class StartMenu extends Menu {
    public StartMenu(Game outerState)
    {
        super(outerState);

        Runnable startAction = () -> {
            outerState.currentView = new LoadSaveMenu(outerState, this);
        };

        Runnable exitAction = () -> {
            System.exit(0);
        };

        MenuItem startGame = new MenuItem("Start!", startAction);
        MenuItem exitGame = new MenuItem("Quit", exitAction);
        items.add(startGame);
        items.add(exitGame);
    }
}
