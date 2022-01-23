public class DefenseBuff extends Spell {
    public DefenseBuff(GameCharacter user)
    {
        super(true, user, user, 1);
    }

    @Override
    public String apply()
    {
        target.defensePoints += target.baseDefensePoints / 2;
        return "On guard. DEF: " + target.defensePoints;
    }

    @Override
    public String unapply()
    {
        target.defensePoints -= target.baseDefensePoints / 2;
        return "Battle stance. DEF: " + target.defensePoints;
    }
}
