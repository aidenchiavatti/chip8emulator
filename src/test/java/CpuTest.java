import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CpuTest {

    @Mock
    private Screen screen;

    private Cpu cpu;

    @Before
    public void setup() {
        cpu = new Cpu(screen);
    }

    @Test
    public void testExecuteOpcodes() {
        cpu.executeOpcode(0x00E0);
        verify(screen, times(1)).clear();
    }

    @Test
    public void testLoadImmediate() {
        cpu.executeOpcode(0x6010); //set V0 register to 0x10
        assertEquals(0x10, cpu.getV(0x0));

        cpu.executeOpcode(0x6FAA);
        assertEquals(0xAA, cpu.getV(0xF));
    }

    @Test
    public void testAddImmediate() {
        cpu.executeOpcode(0x6010); //init value at V0 with 0x10

        cpu.executeOpcode(0x7010); //add 0x10 to V0
        assertEquals(0x20, cpu.getV(0));

        cpu.executeOpcode(0x70F6); //test overflow
        assertEquals(0x16, cpu.getV(0));
    }

    @Test
    public void testAssign() {
        cpu.executeOpcode(0x6B15);//init Vb with 0x15

        cpu.executeOpcode(0x8AB0); //move 0x15 from Vb into Va
        assertEquals(0x15, cpu.getV(0xA));
    }

    @Test
    public void testOr() {
        cpu.executeOpcode(0x6a10); //init Va with 0x10
        cpu.executeOpcode(0x6b01); //init Vb with 0x01

        cpu.executeOpcode(0x8ab1); //Va = Va OR Vb
        assertEquals(0x11, cpu.getV(0xa));
    }

    @Test
    public void testAnd() {
        cpu.executeOpcode(0x6a13); //init Va with 0x13
        cpu.executeOpcode(0x6b02); //init Vb with 0x02

        cpu.executeOpcode(0x8ab2); //Va = Va AND Vb
        assertEquals(0x02, cpu.getV(0xa));
    }

    @Test
    public void testXor() {
        cpu.executeOpcode(0x6a13); //init Va with 0x13
        cpu.executeOpcode(0x6b02); //init Vb with 0x02

        cpu.executeOpcode(0x8ab3); //Va = Va XOR Vb
        assertEquals(0x11, cpu.getV(0xa));
    }

    @Test
    public void testAddWithCarryFlag() {
        cpu.executeOpcode(0x6a01); //init Va with 0x01
        cpu.executeOpcode(0x6b11); //init Vb with 0x11
        cpu.executeOpcode(0x6cF3); //init Vb with 0x02

        cpu.executeOpcode(0x8ab4); //Va = Va + Vb, Vf = 0 (no carry)
        assertEquals(0x12, cpu.getV(0xa));
        assertEquals(0x0, cpu.getV(0xf));

        cpu.executeOpcode(0x8bc4); //Va = Va + Vb, Vf = 1 (carry)
        assertEquals(0x4, cpu.getV(0xb));
        assertEquals(0x1, cpu.getV(0xf));
    }

    @Test
    public void testSubtractWithBorrow() {
        cpu.executeOpcode(0x6a10); //init Va with 0x10
        cpu.executeOpcode(0x6b0a); //init Vb with 0x0a
        cpu.executeOpcode(0x6c16); //init Vc with 0x16

        cpu.executeOpcode(0x8ab5); //Va = Va - Vb
        assertEquals(0x6, cpu.getV(0xa));
        assertEquals(0x1, cpu.getV(0xf));

        cpu.executeOpcode(0x8bc5); //Vb = Vb - Vc
        assertEquals(0xF4, cpu.getV(0xb));
        assertEquals(0x0, cpu.getV(0xf));
    }

    @Test
    public void testLeastSigBitStore() {
        cpu.executeOpcode(0x6a03); //init Va with 0x03
        cpu.executeOpcode(0x6b08); //init Vb with 0x08

        cpu.executeOpcode(0x8a06); //Va >>= 1, Vf = 1
        assertEquals(0x01, cpu.getV(0xa));
        assertEquals(0x1, cpu.getV(0xf));

        cpu.executeOpcode(0x8b06); //Va >>= 1, Vf = 0
        assertEquals(0x04, cpu.getV(0xb));
        assertEquals(0x0, cpu.getV(0xf));
    }

    @Test
    public void voidTestFlippedSubtractWithBorrow() {
        cpu.executeOpcode(0x6a10); //init Va with 0x10
        cpu.executeOpcode(0x6b0a); //init Vb with 0x0a
        cpu.executeOpcode(0x6c16); //init Vc with 0x16

        cpu.executeOpcode(0x8ba7); //Vb = Va - Vb
        assertEquals(0x6, cpu.getV(0xb));
        assertEquals(0x1, cpu.getV(0xf));

        cpu.executeOpcode(0x8ca7); //Vc = Va - Vc
        assertEquals(0xfa, cpu.getV(0xc));
        assertEquals(0x0, cpu.getV(0xf));
    }

    @Test
    public void voidTestShiftLeftAndStoreMostSigBit() {
        cpu.executeOpcode(0x6a0a); //init Va with 0x0a
    }
}