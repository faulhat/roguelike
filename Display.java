import java.util.Arrays;
import java.util.ArrayList;

public class Display {
    public static class RenderException extends Exception {
        public RenderException(String in) {
            super("Error! This string was too long to be displayed: " + in);
        }
    }

    public final int width, height;

    // Array of lines representing the state of the display
    private final char[][] displayState;

    public char getCharAt(int lineno, int colno) {
        return displayState[lineno][colno];
    }

    // Cursor position
    private int lineno, colno;

    public Display(int width, int height) {
        this.width = width;
        this.height = height;

        displayState = new char[height][width]; // height first, since it's an array of lines.

        lineno = 0;
        colno = 0;
    }

    public void clear() {
        for (int i = 0; i < height; i++) {
            Arrays.fill(displayState[i], ' ');
        }

        lineno = 0;
        colno = 0;
    }

    public void print(String in) throws RenderException {
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(in.split("\n")));

        // Make sure there's still room to print everything
        if (lines.size() >= height - lineno) {
            throw new RenderException(in);
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Make sure there's still room to print this line
            if (line.length() >= width - colno) {
                throw new RenderException(line);
            }

            for (int j = 0; j < line.length(); j++) {
                displayState[lineno][colno] = line.charAt(j);
                colno++;
            }

            lineno++;
            colno = 0;
        }
    }

    public void println(String in) throws RenderException {
        print(in + "\n");
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out += displayState[i][j];
            }

            out += '\n';
        }

        return out;
    }
}
