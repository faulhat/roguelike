import java.util.function.Consumer;

public class Inventory extends Menu {
    public static final MAX_ITEMS = 8

                                    public Inventory(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        for (GameItem item : outerState.playerState.inventory) {
            Consumer<Game> itemAction = game -> {
                item.onUse(outerState);
                outerState.currentView = returnView;
            };

            MenuItem menuItem = new MenuItem(item.name, itemAction);
            items.add(menuItem);
        }
    }

    @Override
    public String render()
    {
        String menuStr = super.render();

        return menuStr + items.get(selected).getDescription();
    }
}