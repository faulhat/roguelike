import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Merchant extends Sprite {
    public static class Trade implements Cloneable, DS.Storable {
        public GameItem item;

        public int price;

        public Trade(GameItem item, int price)
        {
            this.item = item;
            this.price = price;
        }

        public Trade(DS.Node node) throws LoadingException, DS.NonDeserializableException
        {
            load(node);
        }

        @Override
        public Trade clone()
        {
            return new Trade(item.clone(), price);
        }

        public static DS.Node getAndValidate(Map<String, DS.Node> asMap, Class<? extends DS.Node> desired, String key) throws LoadingException
        {
            return DS.MapNode.getAndValidate(asMap, desired, key, "Trade");
        }

        @Override
        public void load(DS.Node node) throws LoadingException, DS.NonDeserializableException
        {
            if (!(node instanceof DS.MapNode)) {
                throw new LoadingException("Trade", "must be a map node.");
            }

            Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
            item = GameItem.loadFromName(getAndValidate(asMap, DS.MapNode.class, ":item"));
            price = ((DS.IntNode) getAndValidate(asMap, DS.IntNode.class, ":price")).value;
        }

        @Override
        public DS.Node dump()
        {
            DS.MapNode outNode = new DS.MapNode();
            outNode.addKey("item");
            outNode.add(item.dumpItem());
            outNode.addKey("price");
            outNode.add(new DS.IntNode(price));

            return outNode;
        }
    }

    public static class TradeView extends Menu {
        public ArrayList<Trade> trades;

        public String message;

        public TradeView(Game outerState, GameView returnView, ArrayList<Trade> trades)
        {
            super(outerState, returnView);

            this.trades = trades;

            for (Trade trade : trades) {
                Runnable tradeAction = () -> {
                    if (!(player.inventory.size() >= PlayerState.MAX_ITEMS))
                    {
                        if (player.gold >= trade.price) {
                            player.inventory.add(trade.item.clone());
                            player.gold -= trade.price;
                            message = "";
                        }
                        else {
                            message = "Not enough money!";
                        }
                    }
                    else
                    {
                        message = "Your inventory is full!";
                    }
                };

                items.add(new MenuItem(trade.item.name + " (" + trade.price + " GOLD)", tradeAction));
            }
        }

        @Override
        public String render()
        {
            String out = "Would you like to buy something?\nGOLD: " + player.gold + "\n";
            out += message + "\n" + super.render() + "\n";
            out += "Your inventory:\n";
            for (GameItem item : player.inventory) {
                out += "  " + item.name + "\n";
            }

            return out;
        }
    }

    public ArrayList<Trade> trades;

    private Merchant()
    {
        super("Merchant", false, 'M');
    }

    // Move semantics
    public Merchant(ArrayList<Trade> trades)
    {
        this();

        this.trades = trades;
    }

    public Merchant(int level)
    {
        this();

        Trade trade1 = new Trade(new Bread(), 10);
        Trade trade2 = new Trade(new Coffee(), 8);
        trades = new ArrayList<>(Arrays.asList(trade1, trade2));

        // The fallthrough is intentional.
        switch (level) {
        case 0:
            trades.add(new Trade(new Gladius(), 11));
        case 1:
            trades.add(new Trade(new Spear(), 13));
            trades.add(new Trade(new IronShield(), 12));
        case 2:
            trades.add(new Trade(new Fasces(), 16));
            break;
        default:
        }
    }

    public Merchant(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        this();

        load(node);
    }

    @Override
    public void onEvent(Game outerState, GameEvent e)
    {
        if (e instanceof GameEvent.InteractEvent) {
            outerState.currentView = new TradeView(outerState, outerState.currentView, trades);
        }
    }

    @Override
    public Merchant clone()
    {
        ArrayList<Trade> tradesCopy = new ArrayList<>();
        for (Trade trade : trades) {
            tradesCopy.add(trade.clone());
        }

        return new Merchant(tradesCopy);
    }

    @Override
    public void loadUnique(DS.Node node) throws LoadingException, DS.NonDeserializableException
    {
        if (!(node instanceof DS.MapNode)) {
            throw new LoadingException("Merchant", "Must be a map node.");
        }

        Map<String, DS.Node> asMap = ((DS.MapNode) node).getMap();
        DS.VectorNode tradesNode = (DS.VectorNode) DS.MapNode.getAndValidate(asMap, DS.VectorNode.class, ":trades", "Merchant");
        trades = new ArrayList<>();
        for (DS.Node tradeNode : tradesNode.complexVal) {
            trades.add(new Trade(tradeNode));
        }
    }

    @Override
    public DS.Node dumpUnique()
    {
        DS.MapNode outNode = new DS.MapNode();
        DS.VectorNode tradesNode = new DS.VectorNode();
        for (Trade trade : trades) {
            tradesNode.add(trade.dump());
        }

        outNode.addKey("trades");
        outNode.add(tradesNode);
        return outNode;
    }
}