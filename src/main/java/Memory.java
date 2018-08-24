import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Memory component, composed of 8-bit addresses from 0x200 -> 0xFFF
 * Note: short is used to represent unsigned byte
 * @author Aiden Chiavatti
 */
public class Memory {
    private short[] memory;
    private final int BASE_ADDR = 0x200;
    private final int MAX_SIZE = 0x1000;

    public Memory() {
        this.memory = new short[MAX_SIZE];
    }

    /**
     * write 8 bits to memory offset
     * @param pos memory offset. 0 -> MAX_SIZE
     * @param value 8 bit value to write
     */
    public void write(int pos, int value) { //TODO: might need to remove offset and write to absolute address
        if(pos >= MAX_SIZE) {
            throw new IllegalArgumentException("Cannot write to address larger than max memory size");
        }
        if(pos < 0) {
            throw new IllegalArgumentException("Cannot write to negative address");
        }
        memory[pos + BASE_ADDR] = (short)(value & 0xFF);
    }

    /**
     * read 8 bits from memory offset
     * @param pos memory offset. 0 -> MAX_SIZE
     * @return 8 bit value read
     */
    public short read(int pos) {
        return memory[pos + BASE_ADDR];
    }

    /**
     * load bytes from input into memory
     * @param inputStream input, likely from a file
     * @throws IOException
     */
    public void loadInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        int offset = 0;
        for(byte b: bytes) {
            write(offset, b);
            offset++;
        }
    }
}
