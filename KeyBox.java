import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;

/*
 * Thomas: Here is a class that handles keyboard input.
 */
public class KeyBox {
    // These thread-safe hashmaps will store the state with respect to keyboard input.

    // Which keys have been pressed since the last update or have been held down through the last update?
    public ConcurrentHashMap<Integer, Boolean> wasPressed;

    // Which keys are being held down now?
    public ConcurrentHashMap<Integer, Boolean> isPressed;

    // A subclass of JFrame which implements KeyListener and updates its corresponding KeyBox on input
    public class MyFrame extends JFrame implements KeyListener {
        public MyFrame() {
            super();
            addKeyListener(this);
            setFocusable(true);
            setFocusTraversalKeysEnabled(false);
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            KeyBox.this.wasPressed.put(e.getKeyCode(), true);
            KeyBox.this.isPressed.put(e.getKeyCode(), true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            KeyBox.this.isPressed.put(e.getKeyCode(), false);
        }
    }

    public MyFrame frame;

    public KeyBox() {
        wasPressed = new ConcurrentHashMap<>();
        isPressed = new ConcurrentHashMap<>();

        frame = new MyFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
    }

    public boolean getResetKey(int keyCode) {
        if (wasPressed.contains(keyCode) && wasPressed.get(keyCode)) {
            if (!(isPressed.contains(keyCode) && isPressed.get(keyCode))) {
                // Only reset wasPressed[keyCode] if that key is no longer being held down.
                wasPressed.put(keyCode, false);
            }

            return true;
        }

        return false;
    }
}
