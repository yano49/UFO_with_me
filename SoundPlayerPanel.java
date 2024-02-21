import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;

public class SoundPlayerPanel extends JPanel implements ActionListener {
    private Clip clip;
    private JButton musicButton;
    private boolean musicOn = true;
    private boolean soundOn = true;
    private JButton soundButton;
    private GameWindow window;
    private int verticalGap;
    private ImageIcon MusicOn;
    private ImageIcon MusicOff;
    private ImageIcon SoundOn;
    private ImageIcon SoundOff;

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
        MusicOn = new ImageIcon("src/MusicOn.png");
        MusicOff = new ImageIcon("src/MusicOff.png");

        SoundOn = new ImageIcon("src/SoundOn.png");
        SoundOff = new ImageIcon("src/SoundOff.png");

        // Set initial vertical gap based on frame height
        calculateVerticalGap();

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, verticalGap));

        musicButton = new JButton(MusicOn);
        musicButton.setPreferredSize(new Dimension(450, 100));
        musicButton.addActionListener(this);

        soundButton = new JButton(SoundOn);
        soundButton.setPreferredSize(new Dimension(450, 100));
        soundButton.addActionListener(this);

        add(musicButton);
        add(soundButton);

        setOpaque(false);

        // Add component listener to listen for frame resize events
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateVerticalGap();
                updateLayout();
            }
        });
    }

    // Method to calculate the vertical gap based on frame height
    private void calculateVerticalGap() {
        verticalGap = (int) (window.getSize().getHeight() * 0.35); // Adjust the factor as needed
    }

    // Method to update the layout with the new vertical gap
    private void updateLayout() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 100, verticalGap));
        revalidate(); // Refresh the panel layout
    }

    public void playClip() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
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
        if (e.getSource() == musicButton) {
            if (musicOn) {
                musicButton.setIcon(MusicOff);
                window.soundPlayerPanel.stopClip();
                musicOn = false;
            } else {
                musicButton.setIcon(MusicOn);
                window.soundPlayerPanel.playClip();
                musicOn = true;
            }
        } else if (e.getSource() == soundButton) {
            if (soundOn) {
                soundButton.setIcon(SoundOff);
                soundOn = false;
                window.requestFocusInWindow();
            } else {
                soundButton.setIcon(SoundOn);
                soundOn = true;
                window.requestFocusInWindow();
            }
        }
    }
}
