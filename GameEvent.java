public abstract class GameEvent {
    public static abstract class TouchEvent extends GameEvent {
        public final Direction fromDirection;

        public TouchEvent(Direction fromDirection) {
            this.fromDirection = fromDirection;
        }
    }

    // A class for when the player comes into contact with a non-walkable sprite
    public static class ContactEvent extends TouchEvent {
        public ContactEvent(Direction fromDirection) {
            super(fromDirection);
        }
    }

    // A class for when the player walks onto a walkable sprite
    public static class IntersectEvent extends TouchEvent {
        public IntersectEvent(Direction fromDirection) {
            super(fromDirection);
        }
    }

    // A class for when the player comes into contact with a non-walkable sprite and then presses the interact key
    public static class InteractEvent extends TouchEvent {
        public InteractEvent(Direction fromDirection) {
            super(fromDirection);
        }
    }

    // A class for when the player walks off of a square occupied by a walkable sprite
    public static class LeaveSquareEvent extends GameEvent {
        public final Direction toDirection;

        public LeaveSquareEvent(Direction toDirection) {
            this.toDirection = toDirection;
        }
    }
}
