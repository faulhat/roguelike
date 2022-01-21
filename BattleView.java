public class BattleView extends GameView {
    public ArrayList<Enemy> enemies;

    public BattleView(Game outerState, ArrayList<Enemy> enemies)
    {
        super(outerState);

        this.enemies = enemies;
    }

    @Override
    public void update(double delta)
    {

    }

    @Override
    public String render()
    {
        String out = "\n";

        for (int i = 0; i < Game.DISPLAY_WIDTH; i++) {
            out += "_";
        }

        return out;
    }
}