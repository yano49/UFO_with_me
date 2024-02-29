import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

// Game window is a frame
public class GameWindow extends JFrame implements ActionListener, KeyListener {
    // Some fields are added with the purpose of making them a global variable
    private JPanel menuPanel;
    private JButton playButton;
    private JButton settingButton;
    private JPanel cardPanel = new JPanel(new CardLayout());;
    // Initializing a soundPlayer in the main frame makes it so that a method can change every panel's music
    public SoundPlayerPanel soundPlayerPanel;
    private GamePanel gamePanel;
    private CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    public GameWindow() {
        try {
            createScoreFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        soundPlayerPanel = new SoundPlayerPanel(this);
        // Music will be already opened upon initial entry
        soundPlayerPanel.playMusicClip();


        // Retrieving the background image
        ImageIcon originalSpaceBackground = new ImageIcon("src/resources/Space_Background2.png");
        // Resizing the image according to the frame size
        Image resizedImage = originalSpaceBackground.getImage().getScaledInstance(StandardFrame.screenWidth, StandardFrame.screenHeight, Image.SCALE_SMOOTH);
        ImageIcon spaceBackground = new ImageIcon(resizedImage);

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(spaceBackground);
        // Adding the background label to the frame's content pane
        setContentPane(backgroundLabel);

        // Creating the menu panel which is set in the createMenuPanel method
        menuPanel = createMenuPanel();
        // very important to set opacity of the panel so that the background image shows
        menuPanel.setOpaque(false);

        // Using a card layout to travel between different panels / interfaces
        cardPanel.add(menuPanel, "menu");
        // very important to set opacity of the panel so that the background image shows
        cardPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        gamePanel = new GamePanel(this);
        cardPanel.add(gamePanel, "game");

        SoundPlayerPanel soundPlayerPanel = new SoundPlayerPanel(this);
        cardPanel.add(soundPlayerPanel, "sound");

        // Standard frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("UFO with me");
        setIconImage(new ImageIcon("src/resources/iconImage.png").getImage());
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
            gridPanel.add(new ExtraPanel());
        }

        // Adding the play button
        gridPanel.add(playButton);
        gridPanel.add(new ExtraPanel());

        gridPanel.add(new ExtraPanel());
        // Adding the setting button
        gridPanel.add(settingButton);

        for (int i = 0; i < 4; i++) {
            gridPanel.add(new ExtraPanel());
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
        ImageIcon titleImage = new ImageIcon("src/resources/MenuTitle.png");
        imageLabel.setIcon(titleImage);
        titlePanel.add(imageLabel);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel scorePanel = new JPanel() {
            // Override the paintComponent method to draw the score on the menu panel
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the score at a specific location on the menu panel
                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("Best Score: " + Math.round(retrieveScore()), 10, 30);
                repaint();
            }
        };
        scorePanel.setOpaque(false);
        topPanel.add(scorePanel);
        return panel;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }


    @Override
    // interface method used to detect if the button has been pressed
    // Once the button has been pressed it switches to a different card panel
    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        if (e.getSource() == playButton) {
            cardLayout.show(cardPanel, "game");
            gamePanel.setGameOn(true);
            gamePanel.setShieldOn(true);
            gamePanel.setShieldVisible(true);
        } else if (e.getSource() == settingButton) {
            cardLayout.show(cardPanel, "sound");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gamePanel.setGameOn(false);
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "menu");
            gamePanel.resetGame();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            if (!gamePanel.getDisplayStatus()) {
                gamePanel.setDisplayStatus(true);
            } else {
                gamePanel.setDisplayStatus(false);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void createScoreFile() throws IOException {
        File file = new File("score.txt");
        if (!file.exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                writer.write("0.0");
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    public void saveScore(double score) throws IOException {
        File file = new File("score.txt");
        String previousScore = "";
        if (file.exists()) {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            previousScore = bufferedReader.readLine();
            bufferedReader.close();
        }

        FileWriter writer = new FileWriter(file);

        if (previousScore != null && !previousScore.isEmpty()) {
            double doubleScore = Double.parseDouble(previousScore);
            if (doubleScore != 0) {
                if (doubleScore < score) {
                    writer.write(String.valueOf(score));
                } else {
                    writer.write(previousScore);
                }
            } else {
                writer.write(String.valueOf(score));
            }
        } else {
            writer.write(String.valueOf(score));
        }

        writer.close();
    }


    public double retrieveScore() {
        double score = 0.0;
        BufferedReader reader = null;
        try {
            File file = new File("score.txt");
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
                String strScore = reader.readLine();
                if (strScore != null) {
                    score = Double.parseDouble(strScore.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return score;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
