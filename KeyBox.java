import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;

/*
 * Thomas: Here is a class that handles keyboard input.
 */
public class KeyBox {
    // A lock to control internal state access
    private Lock stateLock;

    // Which keys have been pressed since the last update or have been held down through the last update?
    private HashSet<Integer> wasPressed;

    // Which keys are being held down now?
    private HashSet<Integer> isPressed;

    // Is a key being held down, but has already been processed?
    private HashSet<Integer> beenProcessed;

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
            KeyBox.this.stateLock.lock();
            try {
                KeyBox.this.wasPressed.add(e.getKeyCode());
                KeyBox.this.isPressed.add(e.getKeyCode());

                // If a key is still marked as processed from a previous update cycle, unmark it.
                KeyBox.this.beenProcessed.remove(e.getKeyCode());
            }
            finally {
                KeyBox.this.stateLock.unlock();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            KeyBox.this.stateLock.lock();
            try {
                KeyBox.this.isPressed.remove(e.getKeyCode());
            }
            finally {
                KeyBox.this.stateLock.unlock();
            }
        }
    }

    public MyFrame frame;

    public KeyBox() {
        stateLock = new ReentrantLock();

        wasPressed = new HashSet<>();
        isPressed = new HashSet<>();
        beenProcessed = new HashSet<>();

        frame = new MyFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
    }

    // See if a key has been pressed. Return true even if the signal has already been processed by a client
    public boolean getResetKey(int keyCode) {
        stateLock.lock();
        try {
            if (wasPressed.contains(keyCode)) {
                if (!isPressed.contains(keyCode)) {
                    // Only reset wasPressed[keyCode] if that key is no longer being held down.
                    wasPressed.remove(keyCode);
                }

                // Upon returning true, we have processed this signal.
                beenProcessed.add(keyCode);
                return true;
            }

            return false;
        }
        finally {
            stateLock.unlock();
        }
    }

    // See if a key has been pressed. Return false if the signal has already been processed by a client
    public boolean getReleaseKey(int keyCode) {
        stateLock.lock();
        try {
            // Return false if the signal has been processed. Otherwise, refer to getResetKey().
            if (beenProcessed.contains(keyCode)) {
                return false;
            }
        }
        finally {
            stateLock.unlock();
        }

        // At this point, the lock is unlocked. Otherwise, this method call would hang
        return getResetKey(keyCode);
    }

    // Call getResetKey on a group of keys. Return true if any of them are held down.
    public boolean getResetKeys(int... keyCodes) {
        boolean value = false;
        for (int code : keyCodes) {
            if (getResetKey(code)) {
                value = true;
            }
        }

        return value;
    }

    // Call getReleaseKey on a group of keys.
    public boolean getReleaseKeys(int... keyCodes) {
        boolean value = false;
        for (int code : keyCodes) {
            if (getReleaseKey(code)) {
                value = true;
            }
        }

        return value;
    }
}
