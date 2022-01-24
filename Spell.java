/*
 * This class specifies spells which provide buffs or debuffs to their targets.
 */
public abstract class Spell {
    // Is this a spell to harm or help?
    public boolean isGood;

    // Who cast this spell?
    public GameCharacter user;

    // What character is this spell being used on?
    public GameCharacter target;

    // How long does this spell last?
    public int turnsRemaining;

    // Constructor attaches this spell to its target.
    public Spell(boolean isGood, GameCharacter user, GameCharacter target, int turnsRemaining)
    {
        this.isGood = isGood;
        this.user = user;
        this.target = target;
        this.turnsRemaining = turnsRemaining;

        target.spellsAffecting.add(this);
    }

    // How to apply this spell
    // Returns a string to show as dialogue
    public abstract String apply();

    // What does it do each turn afterward?
    // Returns dialogue to be shown.
    // Returns null if there is no dialogue to show.
    public String eachTurn()
    {
        turnsRemaining--;
        if (turnsRemaining == 0) {
            return remove();
        }

        return null;
    }

    // How to remove this spell.
    public abstract String unapply();

    // Unapply and remove this spell from the target
    public String remove()
    {
        target.spellsAffecting.remove(this);

        return unapply();
    }
}
