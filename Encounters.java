import java.util.ArrayList;

/*
 * Which encounters can occur in a level?
 */
public class Encounters {
    private ArrayList<Encounter> encounters;

    // Move semantics
    public Encounters(ArrayList<Encounter> encounters)
    {
        this.encounters = encounters;
    }

    public ArrayList<Enemy> getNextEncounter(Game outerState)
    {
        double prob = 1.0 / (double) encounters.size();
        double roll = outerState.rand.nextDouble();
        for (int i = 0; i < encounters.size() - 1; i++) {
            if (roll <= prob * (i + 1)) {
                return encounters.get(i).getEnemies();
            }
        }

        return encounters.get(encounters.size() - 1).getEnemies();
    }
}
