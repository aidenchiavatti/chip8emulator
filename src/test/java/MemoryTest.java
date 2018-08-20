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
        memory.write(0, 99);
        assertEquals(99, memory.read(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteOutOfRange() {
        memory.write(0x1000, 99);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteToNegativeAddr() {
        memory.write(-1, 99);
    }

    @Test
    public void testLoadInputStreamToMemory() throws IOException {
        InputStream inputStream = new FileInputStream("PONG");
        memory.loadInputStream(inputStream);
        assertEquals(0x6a, memory.read(0));
        assertEquals(0x80, memory.read(0xF0));
    }
}