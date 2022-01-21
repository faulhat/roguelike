/*
 * Thomas: Here's a class that represents an enemy that you can fight.
 */
public abstract class Enemy extends GameCharacter {
    public String name;

    public int gold;

    public double waitPeriod;

    public double timeLeft;

    public Enemy(String name, int hitPoints, int attackPoints, int defensePoints, int gold, double waitPeriod)
    {
        super(hitPoints, attackPoints, defensePoints);

        this.name = name;
        this.gold = gold;

        this.waitPeriod = waitPeriod;
    }

    public abstract void nextAttack(Game outerState);
}