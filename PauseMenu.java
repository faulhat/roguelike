import java.util.function.Consumer;

public class PauseMenu extends Menu {
    public PauseMenu(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        Consumer<Game> goBackAction = game -> {
            game.currentView = returnView;
        };

        Consumer<Game> exitAction = game -> {
            System.exit(0);
        };

        MenuItem goBack = new MenuItem("Return", goBackAction);
        MenuItem exitGame = new MenuItem("Quit", exitAction);
        items.add(goBack);
        items.add(exitGame);
    }
}
