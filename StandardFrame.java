import javax.swing.*;
import java.awt.*;

public interface StandardFrame {
    // Adding a predetermined size that we want to start of initially
    static Dimension frameSize = new Dimension(1200, 800);
    // Getting the screen size of any device that is using to run this
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Get Methods of the width and height
    static int screenWidth = screenSize.width;
    static int screenHeight = screenSize.height;

    // Makes it so that the frame opens in the upper middle location of your screen 
    static void setFrameMiddle(JFrame frame) {
        // Getting the middle position of the device's screen
        int middleScreenPosition = (screenSize.width - frame.getSize().width) / 2;
        // And then set location to that position
        frame.setLocation(middleScreenPosition, 0);
    }

}
