import java.awt.Point;

/*
 * A subclass of java.awt.Point which can be serialized.
 */
public class DSPoint extends Point implements DS.Storable {
    public static class PointLoadingException extends LoadingException {
        public PointLoadingException(String complaint)
        {
            super("DSPoint", complaint);
        }
    }

    public DSPoint(int x, int y)
    {
        super(x, y);
    }

    // Copy constructor
    public DSPoint(Point other)
    {
        this(other.x, other.y);
    }

    public DSPoint(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        load(node);
    }

    @Override
    public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.VectorNode)) {
            throw new PointLoadingException("Must be a vector (x, y).");
        }

        DS.VectorNode vectorNode = (DS.VectorNode) node;
        int vectorLen = vectorNode.complexVal.size();
        if (vectorLen != 2) {
            throw new PointLoadingException("Incorrect number of elements. Got " + vectorLen + "; wanted 2.");
        }

        DS.Node xNode = vectorNode.complexVal.get(0);
        if (!(xNode instanceof DS.IntNode)) {
            throw new PointLoadingException("x is not an int. Both x and y must be ints.");
        }

        x = ((DS.IntNode) xNode).value;

        DS.Node yNode = vectorNode.complexVal.get(1);
        if (!(yNode instanceof DS.IntNode)) {
            throw new PointLoadingException("y is not an int. Both y and y must be ints.");
        }

        y = ((DS.IntNode) yNode).value;
    }

    @Override
    public DS.Node dump()
    {
        DS.VectorNode outNode = new DS.VectorNode();
        outNode.add(new DS.IntNode(x));
        outNode.add(new DS.IntNode(y));

        return outNode;
    }
}
