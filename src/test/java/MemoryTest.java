import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class MemoryTest {
    private Memory memory;

    @Before
    public void setup() {
        this.memory = new Memory();
    }

    @Test
    public void testReadAndWrite() {
        memory.write(0x200, 99);
        assertEquals(99, memory.read(0x200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteOutOfRange() {
        memory.write(0x1000, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteToNegativeAddr() {
        memory.write(-1, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteToReservedSpace() {
        memory.write(0x0, 1);
    }

    @Test
    public void testLoadInputStreamToMemory() throws IOException {
        InputStream inputStream = new FileInputStream("PONG");
        memory.loadInputStream(inputStream);
        assertEquals(0x6a, memory.read(0x200));
        assertEquals(0x80, memory.read(0x2F0));
    }
}