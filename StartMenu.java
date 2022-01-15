import java.util.function.Consumer;

public class StartMenu extends Menu {
    public StartMenu(Game outerState) {
        super(outerState);

        Consumer<Game> startAction = game -> {
        };

        Consumer<Game> exitAction = game -> {
            System.exit(0);
        };

        MenuItem startGame = new MenuItem("Start!", startAction);
        MenuItem exitGame = new MenuItem("Quit", exitAction);
        items.add(startGame);
        items.add(exitGame);
    }
}
