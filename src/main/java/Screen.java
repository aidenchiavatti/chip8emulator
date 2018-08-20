public class Screen {

    private boolean[][] values;

    public Screen() {
        this.values = new boolean[64][32];
    }

    public void clear() {
        this.values = new boolean[64][32];
    }

    public boolean getPixel(int x, int y) {
        return values[x][y];
    }

    public void drawPixel(int x, int y, boolean value) {
        this.values[x][y] = value;
    }
}
