public class Cpu {
    private Screen screen;
    private short[] v;

    public Cpu(Screen screen) {
        this.screen = screen;
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

        switch(opcode) {
            case 0x00E0:
                screen.clear();
                break;
        }
        int opcodeFirstDigit = (opcode & 0xF000) >> 12;
        switch(opcodeFirstDigit) {
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

                }
                break;
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
}
