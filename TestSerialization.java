import java.awt.Point;

/*
 * Thomas: test to make sure all serialization works.
 */
public class TestSerialization {
    public static void main(String[] args) throws Exception
    {
        DS.Node node;

        Chamber chamber = new Chamber();
        node = chamber.dump();
        assert(chamber.equals(new Chamber(node)));

        ChamberMaze chamberMaze = new ChamberMaze();
        node = chamberMaze.dump();
        assert(chamberMaze.equals(new ChamberMaze(node)));

        DSPoint point = new DSPoint(1, 1);
        node = point.dump();
        assert(point.equals(new DSPoint(node)));

        PlayerState player = new PlayerState();
        node = player.dump();
        assert(player.equals(new PlayerState(node)));

        SaveList saveList = new SaveList();
        node = saveList.dump();
        assert(saveList.equals(new SaveList(node)));

        Sprite teleporter = new Teleporter(1, new Point(0, 0), new Point(0, 0));
        node = teleporter.dump();
        assert(teleporter.equals(new Teleporter(node)));

        Square square = new Square(true);
        square.sprites.add(teleporter);
        node = square.dump();
        assert(square.equals(new Square(node)));

        Coffee coffee = new Coffee();
        node = coffee.dumpItem();
        String nodeStr = node.dumps();
        System.out.println(nodeStr);
        node = DS.loads(nodeStr);
        System.out.println(node.toString());
        assert(coffee.equals(GameItem.loadFromName(node)));

        System.out.println("All ok.");
    }
}