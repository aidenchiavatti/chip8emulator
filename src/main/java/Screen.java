/**
 * Screen component, composed of 64x32 bit array
 * @author Aiden Chiavatti
 */
//TODO: index should start at 1 for coordinates
public class Screen {

    private boolean[][] values;

    public Screen() {
        this.values = new boolean[64][32];
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
                if(this.values[x][y] && bit == 0) {
                    pixelWasTurnedOff = true;
                }
                drawPixel(x + i, y, bit == 1);
            }
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
}
