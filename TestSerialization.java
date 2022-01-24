import java.awt.Point;

/*
 * Test to make sure all serialization works.
 */
public class TestSerialization {
    public static void main(String[] args)
    {
        try {
            DS.Node node;
            String nodeStr;

            Chamber chamber = new Chamber();
            node = chamber.dump();
            assert(chamber.equals(new Chamber(node)));

            ChamberMaze chamberMaze = new ChamberMaze(1);
            node = chamberMaze.dump();
            nodeStr = node.dumps();
            node = DS.loads(nodeStr);
            assert(chamberMaze.equals(new ChamberMaze(node)));

            DSPoint point = new DSPoint(1, -1);
            node = point.dump();
            nodeStr = node.dumps();
            System.out.println(nodeStr);
            node = DS.loads(nodeStr);
            node.print();
            System.out.println();
            assert(point.equals(new DSPoint(node)));

            PlayerState player = new PlayerState();
            node = player.dump();
            assert(player.equals(new PlayerState(node)));

            SaveList saveList = new SaveList();
            node = saveList.dump();
            assert(saveList.equals(new SaveList(node)));

            Sprite teleporter = new Teleporter(1, new Point(0, 0), new Point(0, 0));
            node = teleporter.dump();
            nodeStr = node.dumps();
            System.out.println(nodeStr);
            node = DS.loads(nodeStr);
            node.print();
            assert(teleporter.equals(new Teleporter(node)));

            Square square = new Square(true);
            square.sprites.add(teleporter);
            node = square.dump();
            assert(square.equals(new Square(node)));

            Coffee coffee = new Coffee();
            node = coffee.dumpItem();
            nodeStr = node.dumps();
            System.out.println(nodeStr);
            node = DS.loads(nodeStr);
            node.print();
            System.out.println();
            assert(coffee.equals(GameItem.loadFromName(node)));

            System.out.println("All ok.");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            System.out.println("Test failed.");
        }
    }
}