import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class Emulator {
    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Chip8 Emulator");
            GridLayout gridLayout = new GridLayout(32, 64);
            JPanel panel = new JPanel();
            panel.setBackground(Color.BLUE);
            frame.add(panel);

            JPanel panelRed = new JPanel();
            panelRed.setBackground(Color.RED);
            frame.add(panelRed);
            frame.setLayout(gridLayout);
            frame.setSize(640, 320);
            frame.setVisible(true);

            Memory memory = new Memory();
            Screen screen = new Screen();
            Cpu cpu = new Cpu(screen, memory);
            memory.loadInputStream(new FileInputStream("PONG"));
            cpu.mainLoop();
        } catch (IOException e) {
            System.out.println("Error reading file");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
