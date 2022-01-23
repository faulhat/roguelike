/*
 * Thomas: A menu for loading save files
 * god i'm tired
 */
public class LoadSaveMenu extends Menu {
    public LoadSaveMenu(Game outerState, GameView returnView)
    {
        super(outerState, returnView);

        for (int i = 0; i < SaveList.N_SAVES; i++) {
            SaveState saveState = outerState.saveList.saves[i];
            if (saveState != null) {
                Runnable loadAction = () -> {
                    outerState.loadSave(saveState);
                };

                items.add(new MenuItem("File " + (i + 1), loadAction));
            }
            else {
                Runnable nullAction = () -> {};
                items.add(new MenuItem("File " + (i + 1) + " (empty)", nullAction));
            }
        }

        Runnable newGameAction = () -> {
            outerState.startNew();
        };

        Runnable returnAction = () -> {
            outerState.currentView = returnView;
        };

        items.add(new MenuItem("New game", newGameAction));
        items.add(new MenuItem("Return", returnAction));
    }
}
