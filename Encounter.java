import java.util.ArrayList;

/*
 * This class represents a possible random enemy encounter.
 */
public class Encounter {
    private ArrayList<Enemy> enemies;

    // Create a new potential encounter from a list of enemies involved.
    // Move semantics.
    public Encounter(ArrayList<Enemy> enemies)
    {
        this.enemies = enemies;
    }

    public ArrayList<Enemy> getEnemies()
    {
        ArrayList<Enemy> enemiesCopy = new ArrayList<>();

        for (Enemy enemy : enemies) {
            enemiesCopy.add(enemy.getNew());
        }

        return enemiesCopy;
    }
}