/*
 * Thomas: this class specifies spells which provide buffs or debuffs to their targets.
 */
public abstract class Spell {
    // Is this a spell to harm or help?
    public boolean isGood;

    // How to apply this spell
    // Returns a string to show as dialogue
    public abstract String apply(GameCharacter user, GameCharacter target);

    // What does it do each turn afterward?
    // Returns dialogue to be shown.
    // Returns null if there is no dialogue to show.
    public abstract String eachTurn(GameCharacter target);
}
