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
}