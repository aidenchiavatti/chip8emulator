import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class Memory {
    private short[] memory;
    private final int BASE_ADDR = 0x200;
    private final int MAX_SIZE = 0x1000;

    public Memory() {
        this.memory = new short[0x1000];
    }

    public void write(int pos, int value) {
        if(pos >= MAX_SIZE) {
            throw new IllegalArgumentException("Cannot write to address larger than max memory size");
        }
        if(pos < 0) {
            throw new IllegalArgumentException("Cannot write to negative address");
        }
        memory[pos + BASE_ADDR] = (short)(value & 0xFF);
    }

    public short read(int pos) {
        return memory[pos + BASE_ADDR];
    }

    public void loadInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        int offset = 0;
        for(byte b: bytes) {
            write(offset, b);
            offset++;
        }
    }
}
