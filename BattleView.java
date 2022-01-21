import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.event.*;

public class BattleView extends GameView {
    public static enum State { WAITING, MENU, ENEMY_SELECT, INVENTORY, BATTLE_COMPLETE }

    public State battleState;

    // How much time between the player's turns (in miliseconds)?
    public static final double waitPeriod = 2000.0;

    // How many options are there in the battle menu?
    public static final int N_OPTIONS = 3;

    // Maximum lines of dialogue in the dialogue box
    public static final int MAX_LINES = 6;

    // The options:
    public static final int ATTACK = 0, DEFEND = 1, INVENTORY = 2;

    public ArrayList<Enemy> enemies;

    // How much time is left until the player's next turn?
    public double timeLeft;

    // Which option is selected?
    public int selectedMenuOption;

    // Which enemy is selected?
    public int selectedEnemy;

    // Which inventory item is selected?
    public int selectedItem;

    // Is the player on guard?
    public boolean defending;

    // What does the dialogue box say?
    public String dialogueNow;

    // What should the dialogue box say next?
    public LinkedList<String> dialogueQueue;

    // How much gold will the player win?
    public int gold;

    // Where to return to after the battle ends
    public GameView returnView;

    // How long should the game wait before putting a new message to the dialogue box?
    public static double dialogueWaitPeriod = 100.0;

    // How long until we can put something to the dialogue box next?
    public double dialogueTimeLeft;

    // The player
    public PlayerState player;

    public BattleView(Game outerState, ArrayList<Enemy> enemies, GameView returnView)
    {
        super(outerState);

        battleState = State.MENU;
        this.enemies = enemies;
        timeLeft = waitPeriod;
        selectedMenuOption = 0;
        selectedEnemy = 0;
        selectedItem = 0;
        defending = false;
        gold = 0;

        if (enemies.size() == 1) {
            dialogueNow = enemies.get(0) + " attacks!";
        }
        else {
            dialogueNow = enemies.get(0) + " and its cohorts attack!";
        }

        dialogueQueue = new LinkedList<>();
        dialogueTimeLeft = dialogueWaitPeriod;

        this.returnView = returnView;
        player = outerState.playerState;
    }

    @Override
    public void update(double delta)
    {
        boolean selectPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ENTER, KeyEvent.VK_Z);
        if (battleState == State.BATTLE_COMPLETE) {
            if (selectPressed) {
                outerState.currentView = returnView;
                return;
            }
        }

        dialogueTimeLeft -= delta;
        if (dialogueTimeLeft <= 0 && !dialogueQueue.isEmpty()) {
            dialogueNow = dialogueQueue.getFirst();
            dialogueTimeLeft = dialogueWaitPeriod;
        }

        boolean upPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean downPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean cancelPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ESCAPE, KeyEvent.VK_X);

        for (Enemy enemy : enemies) {
            enemy.timeLeft -= delta;

            if (enemy.timeLeft <= 0) {
                int prevPoints = player.hitPoints;
                player.receiveAttack(enemy.attackPoints);
                enemy.timeLeft = enemy.waitPeriod;
                dialogueQueue.addLast(enemy.name + " attacks!\nYou took " + (player.hitPoints - prevPoints) + " points of damage.");
            }
        }

        switch (battleState) {
        case WAITING:
            timeLeft -= delta;

            if (timeLeft <= 0) {
                defending = false;
                battleState = State.MENU;
            }

            break;
        case MENU:
            if (upPressed) {
                selectedMenuOption = Math.max(selectedMenuOption - 1, 0);
            }

            if (downPressed) {
                selectedMenuOption = Math.min(selectedMenuOption + 1, N_OPTIONS - 1);
            }

            if (selectPressed) {
                if (selectedMenuOption == ATTACK) {
                    selectedMenuOption = 0;
                    battleState = State.ENEMY_SELECT;
                }
                else if (selectedMenuOption == DEFEND) {
                    defending = true;
                    player.defensePoints += player.defensePoints / 2;
                    selectedMenuOption = 0;
                    battleState = State.WAITING;
                    timeLeft = waitPeriod;
                }
                else if (selectedMenuOption == INVENTORY) {
                    selectedMenuOption = 0;
                    battleState = State.INVENTORY;
                }
            }

            break;
        case ENEMY_SELECT:
            if (cancelPressed) {
                selectedEnemy = 0;
                battleState = State.MENU;
            }

            if (upPressed) {
                selectedEnemy = Math.max(selectedEnemy - 1, 0);
            }

            if (downPressed) {
                selectedEnemy = Math.min(selectedEnemy + 1, enemies.size() - 1);
            }

            if (selectPressed) {
                Enemy enemySelected = enemies.get(selectedEnemy);
                String nextDialogue = "You attacked " + enemySelected.name;

                boolean enemyDied = enemySelected.receiveAttack(player.attackPoints);

                if (enemyDied) {
                    enemies.remove(enemySelected);
                    nextDialogue += "\n" + enemySelected.name + " has died!";
                    gold += enemySelected.gold;
                }

                if (enemies.size() == 0) {
                    nextDialogue += "\nYou won! You got $" + gold;
                    player.gold += gold;
                    battleState = State.BATTLE_COMPLETE;
                }

                dialogueQueue.addLast(nextDialogue);
                battleState = State.WAITING;
                timeLeft = waitPeriod;
            }

            break;
        case INVENTORY:
            if (player.inventory.size() == 0) {
                break;
            }

            if (upPressed) {
                selectedItem = Math.max(selectedItem - 1, 0);
            }

            if (downPressed) {
                selectedItem = Math.min(selectedItem + 1, player.inventory.size());
            }

            if (selectPressed) {
                player.inventory.get(selectedItem).onUse(outerState);
                selectedItem = 0;
                battleState = State.WAITING;
                timeLeft = waitPeriod;
            }

            break;
        default:
        }
    }

    @Override
    public String render()
    {
        String dialogueWrapped = Game.wrapString(dialogueNow);
        String out = "";

        for (int i = 0; i < MAX_LINES - dialogueWrapped.split("\n").length; i++) {
            out += "\n";
        }

        out = dialogueWrapped + out;

        for (int i = 0; i < Game.DISPLAY_WIDTH; i++) {
            out += "_";
        }

        out += "\n  ";

        for (Enemy enemy : enemies) {
            out += " " + enemy.getSymbol();
        }

        out += "\n";

        for (int i = 0; i < Game.DISPLAY_WIDTH - 3; i++) {
            out += " ";
        }

        out += "" + player.getSymbol() + "\n";

        switch (battleState) {
        case WAITING:
            out += "-------\n";

            int frac = (int) Math.ceil(5.0 * timeLeft / waitPeriod);
            for (int i = 0; i < 5; i++) {
                if (i <= frac) {
                    out += "   |||\n";
                }
                else {
                    out += "\n";
                }
            }

            out += "-------";
            break;
        case MENU:
            if (selectedMenuOption == ATTACK) {
                out += " -> ATTACK\n";
            }
            else {
                out += "    ATTACK\n";
            }

            if (selectedMenuOption == DEFEND) {
                out += " -> DEFEND\n";
            }
            else {
                out += "    DEFEND\n";
            }

            if (selectedMenuOption == INVENTORY) {
                out += " -> USE ITEM\n";
            }
            else {
                out += "    USE ITEM\n";
            }
            break;
        case INVENTORY:
            for (int i = 0; i < player.inventory.size(); i++) {
                if (i == selectedItem) {
                    out += " -> ";
                }
                else {
                    out += "    ";
                }

                out += player.inventory.get(i).getName();
            }
            break;
        case ENEMY_SELECT:
            break;
        default:
        }

        return out;
    }
}