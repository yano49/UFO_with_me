import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Game window is a frame
public class GameWindow extends JFrame implements ActionListener, KeyListener {
    // Some fields are added with the purpose of making them a global variable
    private JButton playButton;
    private JButton settingButton;
    private JPanel cardPanel;
    // Initializing a soundPlayer in the main frame makes it so that a method can change every panels's music
    public SoundPlayerPanel soundPlayerPanel;

    public GameWindow() {
        soundPlayerPanel = new SoundPlayerPanel(this);
        // Music will be already opened upon initial entry 
        soundPlayerPanel.playClip();

        // Retrieving the background image 
        ImageIcon originalSpaceBackground = new ImageIcon("src/Space_Background2.png");
        // Resizing the image according to the frame size
        Image resizedImage = originalSpaceBackground.getImage().getScaledInstance(StandardFrame.screenWidth, StandardFrame.screenHeight, Image.SCALE_SMOOTH);
        ImageIcon spaceBackground = new ImageIcon(resizedImage);

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(spaceBackground);
        // Adding the background label to the frame's content pane 
        setContentPane(backgroundLabel);

        // Creating the menu panel which is set in the createMenuPanel method
        JPanel menuPanel = createMenuPanel();
        // very important to set opacity of the panel so that the background image shows
        menuPanel.setOpaque(false);

        // Using a card layout to travel between different panels / interfaces
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(menuPanel, "menu");
        // very important to set opacity of the panel so that the background image shows
        cardPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        // Standard frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("UFO with me");
        setIconImage(new ImageIcon("src/iconImage.png").getImage());
        // Using the StandardFrame interface to set the size value
        setSize(StandardFrame.frameSize);
        // adding key listener to this frame / class
        addKeyListener(this);
        // Using the StandardFrame interface to set the frame to the middle of the screen when it pops up
        StandardFrame.setFrameMiddle(this);
    }

    private JPanel createMenuPanel() {
        // Setting the main general panel
        JPanel panel = new JPanel(new BorderLayout());

        // Creating the play button
        playButton = new JButton("Play");
        playButton.setBackground(new Color(30, 31, 33));
        playButton.setFont(new Font("Courier New", Font.BOLD, 35));
        playButton.setForeground(Color.GREEN);
        playButton.setFocusable(false);
        playButton.addActionListener(this);
        playButton.setOpaque(true);

        // Creating the setting button
        settingButton = new JButton("Settings");
        settingButton.setBackground(new Color(30, 31, 33));
        settingButton.setFont(new Font("Courier New", Font.BOLD, 35));
        settingButton.setForeground(Color.GREEN);
        settingButton.setFocusable(false);
        settingButton.addActionListener(this);
        settingButton.setOpaque(true);

        // Creating the grid panel to have the buttons in
        JPanel gridPanel = new JPanel(new GridLayout(4, 3, 0, 20));
        gridPanel.setOpaque(false);

        // Adding extra panels 
        for (int i = 0; i < 4; i++) {
            gridPanel.add(new ExtraPanels());
        }

        // Adding the play button
        gridPanel.add(playButton);
        gridPanel.add(new ExtraPanels());

        gridPanel.add(new ExtraPanels());
        // Adding the setting button
        gridPanel.add(settingButton);

        for (int i = 0; i < 4; i++) {
            gridPanel.add(new ExtraPanels());
        }

        // Creating an invisiable panel to add the title panel in 
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 400));
        topPanel.setOpaque(false);

        // Creating and adding the title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 31, 33));
        titlePanel.setPreferredSize(new Dimension(0, 250));
        JLabel imageLabel = new JLabel();
        // The title bar is made with an image to save time of designing elaborate styles
        ImageIcon titleImage = new ImageIcon("src/MenuTitle.png");
        imageLabel.setIcon(titleImage);
        titlePanel.add(imageLabel);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        return panel;
    }

    @Override
    // interface method used to detect if the button has been pressed
    // Once the button has been pressed it switches to a different card panel 
    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        if (e.getSource() == playButton) {
            GamePanel gamePanel = new GamePanel(new ImageIcon("src/Space_Background2.png").getImage());
            cardPanel.add(gamePanel, "game");
            cardLayout.show(cardPanel, "game");
        } else if (e.getSource() == settingButton) {
            SoundPlayerPanel soundPlayerPanel = new SoundPlayerPanel(this);
            cardPanel.add(soundPlayerPanel, "sound");
            cardLayout.show(cardPanel, "sound");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "menu");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

// created a custom panel which always has the 
class ExtraPanels extends JPanel {
    public ExtraPanels() {
        setOpaque(false);
    }
}
