/*
 * The menu to save the game.
 */
public class SaveMenu extends Menu {
    public class SaveAction implements Runnable {
        public final int target;

        public SaveAction(int target)
        {
            this.target = target;
        }

        @Override
        public void run()
        {
            SaveMenu.this.outerState.saveList.saves[target] = new SaveState(SaveMenu.this.outerState, SaveMenu.this.chamberView);
            SaveMenu.this.outerState.saveList.saveSelf();
            SaveMenu.this.items.get(target).name = "Saved to File " + (target + 1);
        }
    }

    // For details about the game state from the chamber view.
    public ChamberView chamberView;

    public SaveMenu(Game outerState, GameView returnView, ChamberView chamberView)
    {
        super(outerState, returnView);

        this.chamberView = chamberView;

        for (int i = 0; i < SaveList.N_SAVES; i++) {
            SaveAction saveAction = new SaveAction(i);
            if (outerState.saveList.saves[i] == null) {
                items.add(new MenuItem("File " + (i + 1) + " (empty)", saveAction));
            }
            else {
                items.add(new MenuItem("File " + (i + 1), saveAction));
            }
        }

        Runnable returnAction = () -> {
            outerState.currentView = returnView;
        };

        items.add(new MenuItem("Return", returnAction));
    }
}
