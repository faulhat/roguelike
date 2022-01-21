/*
 * Thomas: this is a class for a menu showing the player's inventory
 * Navigating to an item will show a description of that item and selecting it will use it.
 */
public class InventoryMenu extends Menu {
    private class GameItemMenuItem extends MenuItem {
        public GameItem item;

        public GameItemMenuItem(GameItem item)
        {
            super(item.getName(), null);

            this.item = item;
            this.action = game -> {
                item.onUse(game);
                game.playerState.inventory.remove(item);

                assert(game.currentView instanceof InventoryMenu);
                InventoryMenu.this.items.remove(InventoryMenu.this.selected);

                if (!(InventoryMenu.this.selected == 0)) {
                    InventoryMenu.this.selected--;
                }
            };
        }
    }

    public InventoryMenu(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        for (GameItem item : outerState.playerState.inventory) {
            MenuItem menuItem = new GameItemMenuItem(item);
            items.add(menuItem);
        }
    }

    @Override
    public String render()
    {
        String menuStr = "Press X to return...\n";

        // Show the player's hit points.
        menuStr += "\nHP: " + outerState.playerState.hitPoints + "\n";

        if (items.size() == 0) {
            menuStr += "Your inventory is empty!";
        }
        else {
            menuStr += "Your inventory:\n" + super.render();

            for (int i = 0; i < PlayerState.MAX_ITEMS - (items.size() - 1); i++) {
                menuStr += "\n";
            }

            MenuItem menuItem = items.get(selected);
            assert(menuItem instanceof GameItemMenuItem);

            // Show a description of the item at the bottom of the screen.
            menuStr += ((GameItemMenuItem) menuItem).item.getDescWrap();
        }

        return menuStr;
    }
}