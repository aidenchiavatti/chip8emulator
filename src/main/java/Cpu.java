/**
 * CPU component. Contains 16 8-bit registers (V0 -> VF)
 * @author Aiden Chiavatti
 */
public class Cpu {
    private Screen screen;
    private Memory memory;

    private short[] v; //8-bit registers, (V0 -> VF)
    private int i; //16-bit I register
    private int currentAddress;
    private int returnAddress;

    public Cpu(Screen screen, Memory memory) {
        this.screen = screen;
        this.memory = memory;
        this.v = new short[16];
    }

    /**
     * Perform operation based on opcode
     * @param opcode 2 byte hex code
     */
    public void executeOpcode(int opcode) {
        int x;
        int y;
        int n;

        int opcodeFirstDigit = (opcode & 0xF000) >> 12;
        switch(opcodeFirstDigit) {
            case 0x0:
                switch(opcode & 0xFF) {
                    case 0xE0:                                   //00E0
                        screen.clear(); break;
                    case 0xEE:                                   //00EE
                        currentAddress = returnAddress; break;
                    default: unknownOpcode(opcode);
                } break;
            case 0x2:                                            //2NNN
                returnAddress = currentAddress;
                currentAddress = opcode & 0xFFF; break;
            case 0x6: //6XNN
                x = (opcode & 0xF00) >> 8;
                v[x] = (short)(opcode & 0xFF);
                break;
            case 0x7: //7XNN
                x = (opcode & 0xF00) >> 8;
                v[x] += (short)(opcode & 0xFF);
                if(v[x] > 0xFF) {
                    v[x] -= 0x100;
                }
                break;
            case 0x8: //8XYn
                x = (opcode & 0xF00) >> 8;
                y = (opcode & 0x0F0) >> 4;
                n = opcode & 0xF;
                switch(n) {
                    case 0: v[x] = v[y]; break;                  //8XY0
                    case 1: v[x] = (short) (v[x] | v[y]); break; //8XY1
                    case 2: v[x] = (short) (v[x] & v[y]); break; //8XY2
                    case 3: v[x] = (short) (v[x] ^ v[y]); break; //8XY3
                    case 4: v[x] = (short) (v[x] + v[y]);        //8XY4
                        if(v[x] < 0x100) {
                            v[0xF] = 0; //no carry
                        } else {
                            v[x] -= 0x100;
                            v[0xF] = 1; //carry
                        } break;
                    case 5: v[x] = (short) (v[x] - v[y]);        //8XY5
                        if(v[x] < 0) {
                            v[x] += 0x100;
                            v[0xf] = 0; //borrow
                        } else {
                            v[0xf] = 1; //no borrow
                        } break;
                    case 6: v[0xf] = (short) (v[x] & 0x1);       //8XY6
                        v[x] >>= 1;
                        break;
                    case 7: v[x] = (short) (v[y] - v[x]);        //8XY7
                        if(v[x] < 0) {
                            v[x] += 0x100;
                            v[0xf] = 0; //borrow
                        } else {
                            v[0xf] = 1; //no borrow
                        } break;
                    case 0xE: v[0xf] = (short) ((opcode >> 8) & 0x1);  //8XYE
                        v[x] = (short) ((v[x] << 1) & 0xFF); //shift left 1, reduce to 8 bits
                        break;
                    default: unknownOpcode(opcode);
                }
            case 0xa: i = opcode & 0xFFF; break;                 //ANNN
            case 0xd:                                            //DXYN
                x = (opcode & 0xF00) >> 8;
                y = (opcode & 0x0F0) >> 4;
                n = opcode & 0xF;
                for(int row = 0; row < n; row++) {
                    boolean pixelWasTurnedOff = screen.drawRow(v[x] - 1, v[y] + row, memory.read(i + row ));
                    v[0xf] = pixelWasTurnedOff ? (short)1 : (short)0;
                } break;
            case 0xf:
                x = (opcode & 0xF00) >> 8;
                switch (opcode & 0xff) {
                    case 0x29:
                        i = v[x]; break;
                    case 0x33:                                   //FX33
                        short value = v[x];
                        memory.write(i + 2, value % 10);
                        value /= 10;
                        memory.write(i + 1, value % 10);
                        value /= 10;
                        memory.write(i, value);
                        break;
                    case 0x65:
                        for(int index = 0; index <= x; index++) {
                            v[index] = memory.read(i + index);
                        } break;
                    default: unknownOpcode(opcode);
                } break;
            default: unknownOpcode(opcode);
        }
    }

    /**
     * Retrieve value stored in Vx register
     * @param x register number (0x0 - 0xF)
     * @return 8 bit value
     */
    public short getV(int x) {
        return v[x];
    }

    public int getI() {
        return i;
    }

    public int getCurrentAddress() {
        return currentAddress;
    }

    /**
     * Main loop which begins at first address and executes each opcode. Memory should be loaded prior to calling
     * Ends when opcode of 0 is reached TODO: check this
     */
    public void mainLoop() {
        currentAddress = 0x200;
        int opcode = memory.read(currentAddress++) << 8;
        opcode += memory.read(currentAddress++);
        while(opcode != 0)  {
            System.out.println(String.format("0x%04X", opcode));
            this.executeOpcode(opcode);
            screen.print();
            opcode = memory.read(currentAddress++) << 8;
            opcode += memory.read(currentAddress++);
        }
    }

    private void unknownOpcode(int opcode) {
        throw new IllegalArgumentException(String.format("Opcode 0x%04X does not exist.", opcode));
    }
}
