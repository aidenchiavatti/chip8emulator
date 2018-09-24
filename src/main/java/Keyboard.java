import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard {
    private Cpu cpu;

    public Keyboard(Cpu cpu) {
        this.cpu = cpu;
    }

    public KeyListener getKeyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                cpu.setLastKeyPressed(e.getKeyCode() - 0x30);
            }
        };
    }
}
