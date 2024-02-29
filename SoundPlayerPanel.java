import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;

public class SoundPlayerPanel extends JPanel implements ActionListener {
    private Clip musiClip;
    private Clip explosionClip;
    private Clip laserClip;
    private JButton musicButton;
    private boolean musicOn = true;
    private boolean soundFxOn = true;
    private JButton soundFxButton;
    private GameWindow window;
    private int verticalGap;
    private ImageIcon MusicOn;
    private ImageIcon MusicOff;
    private ImageIcon SoundOn;
    private ImageIcon SoundOff;

    public SoundPlayerPanel(GameWindow window) {
        this.window = window;
        try {
            File musicFile = new File("src/resources/opening.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            musiClip = AudioSystem.getClip();
            musiClip.open(audioStream);

            AudioInputStream laserAudioStream = AudioSystem.getAudioInputStream(new File("src/resources/LaserSoundEffect.wav"));
            laserClip = AudioSystem.getClip();
            laserClip.open(laserAudioStream);

            AudioInputStream explosionAudioStream = AudioSystem.getAudioInputStream(new File("src/resources/DeathExplosionSoundEffect.wav"));
            explosionClip = AudioSystem.getClip();
            explosionClip.open(explosionAudioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        MusicOn = new ImageIcon("src/resources/MusicOn.png");
        MusicOff = new ImageIcon("src/resources/MusicOff.png");

        SoundOn = new ImageIcon("src/resources/SoundOn.png");
        SoundOff = new ImageIcon("src/resources/SoundOff.png");

        // Set initial vertical gap based on frame height
        calculateVerticalGap();

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, verticalGap));

        musicButton = new JButton(MusicOn);
        musicButton.setPreferredSize(new Dimension(450, 100));
        musicButton.addActionListener(this);

        soundFxButton = new JButton(SoundOn);
        soundFxButton.setPreferredSize(new Dimension(450, 100));
        soundFxButton.addActionListener(this);

        add(musicButton);
        add(soundFxButton);

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

    public void playMusicClip() {
        musiClip.loop(Clip.LOOP_CONTINUOUSLY);
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    public void stopMusicClip() {
        musiClip.stop();
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    public void playExplosion() {
        explosionClip.stop();
        explosionClip.setFramePosition(0);
        explosionClip.start();
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    public void playLaser() {
        laserClip.stop(); // Stop the clip if it's already playing
        laserClip.setFramePosition(0); // Reset to the beginning
        laserClip.start(); // Start playing the clip
        // This function makes it so that the esc buttons work after pressing the button
        window.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == musicButton) {
            if (musicOn) {
                musicButton.setIcon(MusicOff);
                window.soundPlayerPanel.stopMusicClip();
                musicOn = false;
            } else {
                musicButton.setIcon(MusicOn);
                window.soundPlayerPanel.playMusicClip();
                musicOn = true;
            }
        } else if (e.getSource() == soundFxButton) {
            if (soundFxOn) {
                soundFxButton.setIcon(SoundOff);
                soundFxOn = false;
                window.soundPlayerPanel.soundFxOn = false;
                window.requestFocusInWindow();
            } else {
                soundFxButton.setIcon(SoundOn);
                soundFxOn = true;
                window.soundPlayerPanel.soundFxOn = true;
                window.requestFocusInWindow();
            }
        }
    }

    public boolean soundFxIsOn() {
        return soundFxOn;
    }
}
