import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ScreenTest {

    private Screen screen;

    @Before
    public void setup() {
        screen = new Screen();
    }

    @Test
    public void testClear() {
        screen.clear();
        for(int x = 0; x < 64; x++) {
            for(int y = 0; y < 32; y++) {
                assertFalse(screen.getPixel(x, y));
            }
        }
    }

    @Test
    public void testDrawPixel() {
        screen.drawPixel(0, 0, true);
        assertTrue(screen.getPixel(0, 0));
    }

    @Test
    public void testDrawRow() {
        screen.clear();
        boolean pixelTurnedOff = screen.drawRow(0, 0, (short)0x80);
        assertTrue(screen.getPixel(0, 0));
        assertFalse(screen.getPixel(1, 0));
        assertFalse(screen.getPixel(2, 0));
        assertFalse(screen.getPixel(3, 0));
        assertFalse(screen.getPixel(4, 0));
        assertFalse(screen.getPixel(5, 0));
        assertFalse(screen.getPixel(6, 0));
        assertFalse(screen.getPixel(7, 0));
        assertFalse(pixelTurnedOff);

        pixelTurnedOff = screen.drawRow(0, 0, (short)0x4F);
        assertFalse(screen.getPixel(0, 0));
        assertTrue(screen.getPixel(1, 0));
        assertFalse(screen.getPixel(2, 0));
        assertFalse(screen.getPixel(3, 0));
        assertTrue(screen.getPixel(4, 0));
        assertTrue(screen.getPixel(5, 0));
        assertTrue(screen.getPixel(6, 0));
        assertTrue(screen.getPixel(7, 0));
        assertTrue(pixelTurnedOff);
    }
}