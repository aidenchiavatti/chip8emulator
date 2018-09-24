import java.util.concurrent.TimeUnit;

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
    private int delayTimer;
    private int lastKeyPressed = -1;

    public Cpu(Screen screen, Memory memory) {
        this.screen = screen;
        this.memory = memory;
        this.v = new short[16];
        this.delayTimer = 100;
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
        if(opcode == 0xFFFF) { // TODO: temporary NOOP
            return;
        }
        switch(opcodeFirstDigit) {
            case 0x0:
                switch(opcode & 0xFF) {
                    case 0xE0:                                   //00E0
                        screen.clear(); break;
                    case 0xEE:                                   //00EE
                        currentAddress = returnAddress; break;
                    default: unknownOpcode(opcode);
                } break;
            case 0x1:                                            //1NNN
                n = (short)(opcode & 0xFFF);
                currentAddress = n; break;
            case 0x2:                                            //2NNN
                returnAddress = currentAddress;
                currentAddress = opcode & 0xFFF; break;
            case 0x3:                                            //3XNN
                x = (opcode & 0xF00) >> 8;
                n = (short)(opcode & 0xFF);
                currentAddress = v[x] == n ? currentAddress + 2 : currentAddress; break;
            case 0x4:
                x = (opcode & 0xF00) >> 8;
                n = (short)(opcode & 0xFF);
                currentAddress = v[x] == n ? currentAddress: currentAddress + 2; break;
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
                } break;
            case 0xa:
                i = opcode & 0xFFF;
               // System.out.println(String.format("Setting I: 0x%04x Opcode: 0x%04x", i, opcode));
                break;                 //ANNN
            case 0xc:                                            //CXNN
                int random = (int) (Math.random() * 256);
                x = (opcode & 0xF00) >> 8;
                n = opcode & 0xFF;
                v[x] = (short) (random & n); break;
            case 0xd:                                            //DXYN
                x = (opcode & 0xF00) >> 8;
                y = (opcode & 0x0F0) >> 4;
                n = opcode & 0xF;
                if (v[0xb] != 0x0c) {
                    System.out.println("Y value changed.");
                    System.out.println(String.format("Drawing Sprite at addr: 0x%04x", i));
                    System.out.println(String.format("X: 0x%04X Y: 0x%04X", v[x], v[y]));
                }
                // System.out.println(String.format("Drawing Sprite at addr: 0x%04x", i));
                // System.out.println(String.format("X: 0x%04X Y: 0x%04X", v[x], v[y]));
                for(int row = 0; row < n; row++) {
                    // System.out.println(String.format("0x%04X", memory.read(i + row )));
                    boolean pixelWasTurnedOff = screen.drawRow(v[x] - 1, v[y] + row, memory.read(i + row ));
                    v[0xf] = pixelWasTurnedOff ? (short)1 : (short)0;
                } break;
            case 0xe:
                switch(opcode & 0xF) {
                    case 0x1:
                        x = (opcode & 0xF00) >> 8;
                        //System.out.println("Looking for key: " + v[x]);
                        //System.out.println("Found: " + lastKeyPressed);
                        if(v[x] == lastKeyPressed) {
                            System.out.println("Key Pressed Found: " + v[x]);
                            lastKeyPressed = -1;
                        } else {
                            currentAddress += 2;
                        }
                        break;
                    default: unknownOpcode(opcode);
                } break;
            case 0xf:
                x = (opcode & 0xF00) >> 8;
                switch (opcode & 0xff) {
                    case 0x07:
                        v[x] = (short) delayTimer;                       //FX07
                    case 0x15:                                   //FX15
                        delayTimer = v[x]; break;
                    case 0x18:
                        System.out.println("skipping setting sound timer"); // TODO: do sound timer?
                        break;
                    case 0x29:
                        i = memory.spriteLocation(v[x]); break;
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

        int operationsSinceTimerTick = 0;
        while(opcode != 0)  {
            if(operationsSinceTimerTick > 5) {
                operationsSinceTimerTick = 0;
                delayTimer--;
//                if(delayTimer == 0) {
//                    delayTimer = 100;
//                }
            }

            //System.out.println(String.format("0x%04X", opcode));
            this.executeOpcode(opcode);
            //screen.print();
            opcode = memory.read(currentAddress++) << 8;
            opcode += memory.read(currentAddress++);
            operationsSinceTimerTick++;
            try {
                Thread.sleep(1);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDelayTimer() {
        return delayTimer;
    }

    private void unknownOpcode(int opcode) {
        throw new IllegalArgumentException(String.format("Opcode 0x%04X does not exist.", opcode));
    }

    public void setLastKeyPressed(int key) {
        this.lastKeyPressed = key;
    }
}
