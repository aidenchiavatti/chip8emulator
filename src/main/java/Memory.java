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
        initSprites();
    }

    /**
     * write 8 bits to memory address
     * @param pos memory address. 0 -> MAX_SIZE
     * @param value 8 bit value to write
     */
    public void write(int pos, int value) {
        if(pos >= MAX_SIZE) {
            throw new IllegalArgumentException("Cannot write to address larger than max memory size");
        } else if(pos < 0) {
            throw new IllegalArgumentException("Cannot write to negative address");
        } else if(pos < 0x200) {
            throw new IllegalArgumentException("Cannot write to reserved address (0 -> 0x200)");
        }
        memory[pos] = (short)(value & 0xFF);
    }

    /**
     * read 8 bits from memory address
     * @param pos memory address. 0 -> MAX_SIZE
     * @return 8 bit value read
     */
    public short read(int pos) {
        return memory[pos];
    }

    /**
     * load bytes from input into memory
     * @param inputStream input, likely from a file
     * @throws IOException
     */
    public void loadInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        int address = 0x200;
        for(byte b: bytes) {
            write(address, b);
            address++;
        }
    }

    private void initSprites() {
        //init 0
        memory[0] = 0x60;
        memory[1] = 0x90;
        memory[2] = 0x90;
        memory[3] = 0x90;
        memory[4] = 0x60;
    }
}
