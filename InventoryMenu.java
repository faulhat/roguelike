import java.awt.event.*;

/*
 * This is a class for a menu showing the player's inventory
 * Navigating to an item will show a description of that item and selecting it will use it.
 */
public class InventoryMenu extends Menu {
    public static enum State { NAV, DROP_CONFIRM }

    private class GameItemMenuItem extends MenuItem {
        public GameItem item;

        public GameItemMenuItem(GameItem item)
        {
            super(item.getName(), null);

            this.item = item;
            this.action = () -> {
                item.onUse(outerState);

                assert(outerState.currentView instanceof InventoryMenu);

                if (!player.inventory.contains(item))
                {
                    InventoryMenu.this.items.remove(InventoryMenu.this.selected);

                    if (InventoryMenu.this.selected != 0) {
                        InventoryMenu.this.selected--;
                    }
                }
            };
        }
    }

    public class DropMenu extends Menu {
        private Runnable yesAction, noAction;

        public DropMenu(Game outerState)
        {
            super(outerState, (GameView) null);

            yesAction = () -> {
                InventoryMenu.this.items.remove(InventoryMenu.this.selected);
                outerState.playerState.inventory.remove(InventoryMenu.this.selected);

                if (InventoryMenu.this.selected != 0)
                {
                    InventoryMenu.this.selected--;
                }

                InventoryMenu.this.state = State.NAV;
            };

            noAction = () -> {
                InventoryMenu.this.state = State.NAV;
            };

            items.add(new MenuItem("yes", yesAction));
            items.add(new MenuItem("no", noAction));
        }

        @Override
        public void update(double delta)
        {
            boolean cancelPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ESCAPE, KeyEvent.VK_X);

            if (cancelPressed) {
                noAction.run();
                return;
            }

            super.update(delta);
        }

        @Override
        public String render()
        {
            return "Drop this item?\n" + super.render();
        }
    }

    public State state;

    private DropMenu dropMenu;

    public InventoryMenu(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        for (GameItem item : player.inventory) {
            MenuItem menuItem = new GameItemMenuItem(item);
            items.add(menuItem);
        }

        state = State.NAV;
        dropMenu = new DropMenu(outerState);
    }

    @Override
    public void update(double delta)
    {
        switch (state) {
        case NAV:
            boolean dropKeyPressed = outerState.keyBox.getReleaseKey(KeyEvent.VK_D);

            if (dropKeyPressed && player.inventory.size() != 0) {
                state = State.DROP_CONFIRM;
                return;
            }

            super.update(delta);
            break;
        case DROP_CONFIRM:
            dropMenu.update(delta);
        }
    }

    @Override
    public String render()
    {
        String menuStr = "X to return, D to drop, Z to use\n";

        // Show the player's hit points.
        menuStr += "\nHP: " + player.hitPoints + " ";
        menuStr += "ATK:" + player.attackPoints + " ";
        menuStr += "DEF:" + player.defensePoints + "\n";
        menuStr += "GOLD: " + player.gold;

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

            switch (state) {
            case NAV:
                // Show a description of the item at the bottom of the screen.
                menuStr += ((GameItemMenuItem) menuItem).item.getDescWrap();
                break;
            case DROP_CONFIRM:
                menuStr += dropMenu.render();
            }
        }

        return menuStr;
    }
}