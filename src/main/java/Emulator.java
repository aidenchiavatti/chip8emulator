import java.io.FileInputStream;
import java.io.IOException;

public class Emulator {
    public static void main(String[] args) {
        try {
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
