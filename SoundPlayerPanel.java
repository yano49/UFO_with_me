import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

public class SoundPlayerPanel extends JPanel implements ActionListener {
    private Clip clip;
    private JButton openButton;
    private JButton closeButton;
    private GameWindow window;

    public SoundPlayerPanel(GameWindow window) {
        this.window = window;
        try {
            File file = new File("src/opening.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }


        setLayout(new FlowLayout());
        openButton = new JButton();
        openButton.setText("Open");
        openButton.addActionListener(this);

        closeButton = new JButton();
        closeButton.setText("Close");
        closeButton.addActionListener(this);

        add(openButton);
        add(closeButton);

        setOpaque(false);
    }


    public void playClip() {
        clip.start();
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    public void stopClip() {
        clip.stop();
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            window.soundPlayerPanel.playClip();
        } else if (e.getSource() == closeButton) {
            window.soundPlayerPanel.stopClip();
        }
    }
}
