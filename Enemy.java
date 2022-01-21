/*
 * Thomas: Here's a class that represents an enemy that you can fight.
 */
public abstract class Enemy {
    public int hitPoints;

    public abstract String getName();

    public abstract char getSymbol();

    public abstract void nextAttack(Game outerState);
}