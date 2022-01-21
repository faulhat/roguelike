/*
 * Thomas: Here's a class that represents an enemy that you can fight.
 */
public abstract class Enemy extends GameCharacter {
    // How much gold is this enemy carrying?
    public int gold;

    // What to say when a battle with this enemy starts
    public String approachMessage;

    // What to say when this enemy attacks
    public String attackMessage;

    public Enemy(String name, int hitPoints, int attackPoints, int defensePoints, int gold, double waitPeriod)
    {
        super(name, hitPoints, attackPoints, defensePoints, waitPeriod, waitPeriod);

        this.gold = gold;

        this.waitPeriod = waitPeriod;
        timeLeft = waitPeriod;
    }

    // Get this enemy's next attack.
    // Returns null to attack the player normally.
    public abstract Spell getNextSpell(Game outerState);
}