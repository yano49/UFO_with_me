import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

public class SoundPlayer extends JFrame {
    private Clip clip;

    public SoundPlayer() {
        try {
            File file = new File("/Users/knight/Desktop/UFO_with_me/src/opening.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        // Automatically start playing when the JFrame starts
        clip.start();

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem stopMenuItem = new JMenuItem("Stop Music");
        stopMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.stop();
            }
        });
        optionsMenu.add(stopMenuItem);
        menuBar.add(optionsMenu);

        // Create a custom JPanel with a background image
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Create buttons with smaller size
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");

        // Set smaller size for buttons
        Dimension buttonSize = new Dimension(80, 30);
        playButton.setPreferredSize(buttonSize);
        stopButton.setPreferredSize(buttonSize);

        // Button actions
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.start();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.stop();
            }
        });

        // Add buttons to main panel
        mainPanel.add(playButton);
        mainPanel.add(stopButton);

        // Add components to JFrame
        this.add(mainPanel);
        this.setJMenuBar(menuBar);  // Add menu bar to JFrame
        this.setTitle("Sound Player");
        this.setSize(1200, 800); // Adjusted size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // Custom JPanel class with background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("/Users/knight/Desktop/UFO_with_me/src/Space_Background2.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SoundPlayer();
            }
        });
    }
}
