import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;

public class Emulator {
    public static void main(String[] args) {
        try {
            Memory memory = new Memory();
            Screen screen = new Screen();
            Cpu cpu = new Cpu(screen, memory);
            Keyboard keyboard = new Keyboard(cpu);
            memory.loadInputStream(new FileInputStream("PONG"));

            JFrame frame = new JFrame("Chip8 Emulator");

            frame.addKeyListener(keyboard.getKeyListener());
            screen.init(frame);

            cpu.mainLoop();
        } catch (IOException e) {
            System.out.println("Error reading file");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
