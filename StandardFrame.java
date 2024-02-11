import javax.swing.*;
import java.awt.*;

public interface StandardFrame {

    static Dimension frameSize = new Dimension(1200, 800);
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static int screenWidth = screenSize.width;
    static int screenHeight = screenSize.width;

    static void setFrameMiddle(JFrame frame) {
        int middleScreenPosition = (screenSize.width - frame.getSize().width) / 2;
        frame.setLocation(middleScreenPosition, 0);
    }

}
