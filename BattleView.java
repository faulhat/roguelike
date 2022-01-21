import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.event.*;
import java.util.Arrays;

public class BattleView extends GameView {
    public static enum State { WAITING, MENU, ENEMY_SELECT, INVENTORY, BATTLE_COMPLETE }

    public State battleState;

    // How many options are there in the battle menu?
    public static final int N_OPTIONS = 3;

    // Maximum lines of dialogue in the dialogue box
    public static final int MAX_LINES = 6;

    // The options:
    public static final int ATTACK = 0, DEFEND = 1, INVENTORY = 2;

    public ArrayList<Enemy> enemies;

    public int selectedMenuOption;

    public int selectedEnemy;

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
    public static double dialogueWaitPeriod = 2000.0;

    // How long until we can put something to the dialogue box next?
    public double dialogueTimeLeft;

    public BattleView(Game outerState, ArrayList<Enemy> enemies, GameView returnView)
    {
        super(outerState);

        battleState = State.MENU;
        this.enemies = enemies;
        selectedMenuOption = 0;
        selectedEnemy = 0;
        selectedItem = 0;
        defending = false;
        gold = 0;

        assert(enemies.size() > 0);
        Enemy firstEnemy = enemies.get(0);
        if (enemies.size() == 1) {
            dialogueNow = firstEnemy.approachMessage + "\n" + firstEnemy.name + " attacks!";
        }
        else {
            dialogueNow = firstEnemy.approachMessage + "\n" + firstEnemy.name + " and its cohorts attack!";
        }

        dialogueQueue = new LinkedList<>();
        dialogueTimeLeft = dialogueWaitPeriod;

        this.returnView = returnView;
    }

    public BattleView(Game outerState, Enemy enemy, GameView returnView)
    {
        this(outerState, new ArrayList<>(Arrays.asList(new Enemy[] { enemy })), returnView);
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
            dialogueNow = dialogueQueue.poll(); // Gets and removes head
            dialogueTimeLeft = dialogueWaitPeriod;
        }

        boolean upPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_UP, KeyEvent.VK_W);
        boolean downPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_DOWN, KeyEvent.VK_S);
        boolean cancelPressed = outerState.keyBox.getReleaseKeys(KeyEvent.VK_ESCAPE, KeyEvent.VK_X);

        for (Enemy enemy : enemies) {
            enemy.timeLeft -= delta;

            for (Spell spell : enemy.spellsAffecting) {
                spell.eachTurn();
            }

            if (enemy.timeLeft <= 0) {
                Spell enemySpell = enemy.getNextSpell(outerState);
                if (enemySpell != null) {
                    dialogueQueue.addLast(enemySpell.apply());
                }
                else {
                    int prevPoints = player.hitPoints;
                    player.receiveAttack(enemy.attackPoints);
                    enemy.timeLeft = enemy.waitPeriod;
                    dialogueQueue.addLast(enemy.attackMessage + "\nYou took " + (prevPoints - player.hitPoints) + " points of damage.");
                }
            }
        }

        switch (battleState) {
        case WAITING:
            player.timeLeft -= delta;

            if (player.timeLeft <= 0) {
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
                    player.timeLeft = player.waitPeriod;
                }
                else if (selectedMenuOption == INVENTORY) {
                    if (player.inventory.size() > 0) {
                        selectedMenuOption = 0;
                        battleState = State.INVENTORY;
                    }
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
                int prevEnemyHP = enemySelected.hitPoints;

                boolean enemyDied = enemySelected.receiveAttack(player.attackPoints);

                if (enemyDied) {
                    enemies.remove(enemySelected);
                    nextDialogue += "\n" + enemySelected.name + " has died!";
                    gold += enemySelected.gold;
                }
                else {
                    nextDialogue += "\n" + (prevEnemyHP - enemySelected.hitPoints) + " damage! HP: " + enemySelected.hitPoints;
                }

                if (enemies.size() == 0) {
                    nextDialogue += "\nYou won! GOLD +" + gold;
                    nextDialogue += "\nPress enter to continue...";
                    player.gold += gold;

                    for (int i = 0; i < player.spellsAffecting.size(); i++) {
                        player.spellsAffecting.remove(i);
                    }

                    battleState = State.BATTLE_COMPLETE;
                }
                else {
                    battleState = State.WAITING;
                    player.timeLeft = player.waitPeriod;
                }

                dialogueQueue.addLast(nextDialogue);
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
                selectedItem = Math.min(selectedItem + 1, player.inventory.size() - 1);
            }

            if (selectPressed) {
                player.inventory.get(selectedItem).onUse(outerState);
                selectedItem = 0;
                battleState = State.WAITING;
                player.timeLeft = player.waitPeriod;
            }

            break;
        default:
        }
    }

    @Override
    public String render()
    {
        String yourHP = "Your HP: " + player.hitPoints;

        String dialogueWrapped = Game.wrapString(dialogueNow);
        String out = "\n\n";

        for (int i = 0; i < MAX_LINES - dialogueWrapped.split("\n").length; i++) {
            out += "\n";
        }

        out = yourHP + "\n\n" + dialogueWrapped + out;

        for (int i = 0; i < Game.DISPLAY_WIDTH; i++) {
            out += "_";
        }

        out += "\n\n  ";

        for (Enemy enemy : enemies) {
            out += " " + enemy.getSymbol();
        }

        out += "\n\n\n\n";

        for (int i = 0; i < Game.DISPLAY_WIDTH - 3; i++) {
            out += " ";
        }

        out += "" + player.getSymbol() + "\n\n";

        for (int i = 0; i < Game.DISPLAY_WIDTH; i++) {
            out += "_";
        }

        out += "\n\n";

        switch (battleState) {
        case WAITING:
            int frac = (int) Math.floor(8.0 * (1 - player.timeLeft / player.waitPeriod));
            String blank = "|        |\n";
            out += " --------\n" + blank;
            if (frac == 0) {
                out += "|   ||   |\n";
                out += "|   ||   |\n";
                out += blank;
                out += blank;
                out += blank;
            }
            else if (frac == 1) {
                out += "|     // |\n";
                out += "|    //  |\n";
                out += blank;
                out += blank;
                out += blank;
            }
            else if (frac == 2) {
                out += blank;
                out += blank;
                out += "|    === |\n";
                out += blank;
                out += blank;
            }
            else if (frac == 3) {
                out += blank;
                out += blank;
                out += blank;
                out += "|    \\\\  |\n";
                out += "|     \\\\ |\n";
            }
            else if (frac == 4) {
                out += blank;
                out += blank;
                out += blank;
                out += "|   ||   |\n";
                out += "|   ||   |\n";
            }
            else if (frac == 5) {
                out += blank;
                out += blank;
                out += blank;
                out += "|  //    |\n";
                out += "| //     |\n";
            }
            else if (frac == 6) {
                out += blank;
                out += blank;
                out += "| ===    |\n";
                out += blank;
                out += blank;
            }
            else if (frac == 7) {
                out += "| \\\\     |\n";
                out += "|  \\\\    |\n";
                out += blank;
                out += blank;
                out += blank;
            }

            out += blank;
            out += " --------\n";

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
                if (player.inventory.size() > 0) {
                    out += " -> USE ITEM\n";
                }
                else {
                    out += " -> (INVENTORY EMPTY)";
                }
            }
            else {
                if (player.inventory.size() > 0) {
                    out += "    USE ITEM\n";
                }
                else {
                    out += "    (INVENTORY EMPTY)";
                }
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

                out += player.inventory.get(i).getName() + "\n";
            }
            break;
        case ENEMY_SELECT:
            out += "To whom?\n";

            for (int i = 0; i < enemies.size(); i++) {
                if (i == selectedEnemy) {
                    out += " -> ";
                }
                else {
                    out += "    ";
                }

                out += enemies.get(i).name + "\n";
            }

            break;
        default:
        }

        return out;
    }
}