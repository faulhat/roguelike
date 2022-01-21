/*
 * Thomas: this class represents a character who can engage in battle.
 */
public abstract class GameCharacter {
    public String name;

    public double trueHitPoints;

    public int hitPoints;

    public int attackPoints;

    public int defensePoints;

    public GameCharacter(String name, int hitPoints, int attackPoints, int defensePoints)
    {
        this.name = name;
        this.hitPoints = hitPoints;
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;

        trueHitPoints = (double) hitPoints;
    }

    public void deductHP(double hpDeducted)
    {
        trueHitPoints -= hpDeducted;
        hitPoints = (int) Math.ceil(trueHitPoints);
    }

    // Receive an attack. attackMagnitude should be the attack points of the attacker.
    // Returns a boolean saying whether or not this character is dead.
    public boolean receiveAttack(int attackMagnitude)
    {
        trueHitPoints -= 1.0 / (1.0 + (double) defensePoints / 2.0) * attackMagnitude;
        hitPoints = (int) Math.ceil(trueHitPoints);

        return hitPoints <= 0;
    }

    public void recoverHP(double hpRecovered)
    {
        trueHitPoints += hpRecovered;
        hitPoints = (int) Math.ceil(trueHitPoints);
    }

    public abstract char getSymbol();
}
