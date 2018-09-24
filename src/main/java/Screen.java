import javax.swing.*;
import java.awt.*;

/**
 * Screen component, composed of 64x32 bit array
 * @author Aiden Chiavatti
 */
//TODO: index should start at 1 for coordinates
public class Screen {

    private boolean[][] values;
    private JPanel[][] panels;

    public Screen() {
        this.values = new boolean[64][32];
        this.panels = new JPanel[64][32];
    }

    /**
     * clears the screen
     */
    public void clear() {
        this.values = new boolean[64][32];
    }

    /**
     * returns if pixel is set at x, y
     * @param x horizontal coordinate, 0 -> 63
     * @param y vertical coordinate, 0 -> 31
     * @return 1 if white, 0 if black
     */
    public boolean getPixel(int x, int y) {
        return values[x][y];
    }

    /**
     * set pixel at x, y to on/off
     * @param x horizontal coordinate, 0 -> 63
     * @param y vertical coordinate, 0 -> 31
     * @param value 1 if white, 0 if black
     */
    public void drawPixel(int x, int y, boolean value) {
        this.values[x][y] = value;
        if(value) {
            panels[x][y].setBackground(Color.WHITE);
        }
        else {
            panels[x][y].setBackground(Color.BLACK);
        }
    }

    /**
     * draws a row of 8 bits at (x, y)
     * @param x coordinate
     * @param y coordinate
     * @param rowData 8-bit encoded data
     */
    public boolean drawRow(int x, int y, short rowData) { //TODO: add return to javadoc
        boolean pixelWasTurnedOff = false;
        for(int i = 7; i >= 0; i--) {
            if (x + i < 64) {
                int bit = rowData & 1;
                int oldBit = this.values[x + i][y] ? 1 : 0;
                int newValue = oldBit ^ bit;
                if(newValue == 0 && oldBit == 1) {
                    pixelWasTurnedOff = true;
                }
                drawPixel(x + i, y, newValue == 1);
            }
//            else {
//                int wrappedX = (x + i) - 64;
//                int bit = rowData & 1;
//                int oldBit = this.values[x][y] ? 1 : 0;
//                int newValue = oldBit ^ bit;
//                if(newValue == 0 && oldBit == 1) {
//                    pixelWasTurnedOff = true;
//                }
//                drawPixel(wrappedX, y, newValue == 1);
//            }
            rowData >>= 1;
        }
        return pixelWasTurnedOff;
    }

    public void print()  {
        for(int row = 0; row < 32; row++) {
            for(int col = 0; col < 64; col++) {
                System.out.print(values[col][row] ? "1 ": "0 ");
            }
            System.out.println();
        }
    }

    public void init(JFrame frame) {
        GridLayout gridLayout = new GridLayout(32, 64);

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 64; j++) {
                JPanel panel = new JPanel();
                panel.setBackground(Color.BLACK);
                frame.add(panel);
                panels[j][i] = panel;
            }
        }
        frame.setLayout(gridLayout);
        frame.setSize(640, 320);
        frame.setVisible(true);
    }
}
