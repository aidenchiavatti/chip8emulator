import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CpuTest {

    @Mock
    private Screen screen;

    @Mock
    private Memory memory;

    private Cpu cpu;

    @Before
    public void setup() {
        cpu = new Cpu(screen, memory);
    }

    @Test
    public void testDisplayClear() {
        cpu.executeOpcode(0x00E0);
        verify(screen, times(1)).clear();
    }

    @Test
    public void testCallSubroutine() {
        cpu.executeOpcode(0x2240);
        assertEquals(0x040, cpu.getCurrentAddress());
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
    public void testFlippedSubtractWithBorrow() {
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
    public void testShiftLeftAndStoreMostSigBit() {
        cpu.executeOpcode(0x6a0a); //init Va with 0x0a
        cpu.executeOpcode(0x6ba0); //init Vb with 0xb0

        cpu.executeOpcode(0x8a0E); //shift Va left 1 bit, store carry in VF
        assertEquals(0x14, cpu.getV(0xa));
        assertEquals(0x0, cpu.getV(0xf));

        cpu.executeOpcode(0x8b0E); //shift Vb left 1 bit, store carry in VF
        assertEquals(0x40, cpu.getV(0xb));
        assertEquals(0x1, cpu.getV(0xf));
    }

    @Test
    public void testLoadAddressIntoI() {
        cpu.executeOpcode(0xA123);
        assertEquals(0x123, cpu.getI());
    }

    @Test
    public void testDraw() {
        cpu.executeOpcode(0x6a01); //init Va with 1
        cpu.executeOpcode(0x6b01); //init Vb with 1
        cpu.executeOpcode(0xa200); //init I with address 0x000

        //mocks memory starting at I
        when(memory.read(0)).thenReturn((short)0x80); //1000 0000
        when(memory.read(1)).thenReturn((short)0x01); //0000 0001
        when(memory.read(2)).thenReturn((short)0x84); //1000 1000
        when(memory.read(3)).thenReturn((short)0xFF); //1111 1111

        cpu.executeOpcode(0xdab4); //draw 8x4 sprite at 1,1
        verify(screen, times(1)).drawRow(0, 0, (short)0x80);
        verify(screen, times(1)).drawRow(0, 1, (short)0x01);
        verify(screen, times(1)).drawRow(0, 2, (short)0x84);
        verify(screen, times(1)).drawRow(0, 3, (short)0xFF);
        assertEquals(0, cpu.getV(0xf));

        when(memory.read(0)).thenReturn((short)0x0); //0000 0000
        when(screen.drawRow(0, 0, (short)0x0)).thenReturn(true);
        cpu.executeOpcode(0xdab1); //draw 8x1 sprite at 1,1
        verify(screen, times(1)).drawRow(0, 0, (short)0x0);
        assertEquals(1, cpu.getV(0xf));
    }

    @Test
    public void testMainLoop() {
        when(memory.read(0)).thenReturn((short)0x6A);
        when(memory.read(1)).thenReturn((short)0x02);
        when(memory.read(2)).thenReturn((short)0x6B);
        when(memory.read(3)).thenReturn((short)0x0C);
        cpu.mainLoop();
        assertEquals(0x02, cpu.getV(0xa));
        assertEquals(0x0c, cpu.getV(0xb));
    }

    @Test
    public void testSetBcd() {
        cpu.executeOpcode(0xA300); //init I = 300
        cpu.executeOpcode(0x6080); //init v0 = 128
        cpu.executeOpcode(0xf033);
        verify(memory, times(1)).write(cpu.getI(), 1);
        verify(memory, times(1)).write(cpu.getI() + 1, 2);
        verify(memory, times(1)).write(cpu.getI() + 2, 8);

    }
}