import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {
    private GameWindow menu;
    private double currentScore;

    public GameOverPanel(GameWindow menu, double currentScore) {
        this.menu = menu;
        this.currentScore = currentScore;

        setLayout(new BorderLayout());

        // Create a label panel to display the game over message and scores
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setOpaque(false);

        // Create a label to display the game over message
        JLabel gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Courier New", Font.BOLD, 50));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a label to display the current score
        JLabel currentScoreLabel = new JLabel("Your Score: " + Math.round(currentScore));
        currentScoreLabel.setFont(new Font("Courier New", Font.PLAIN, 30));
        currentScoreLabel.setForeground(Color.WHITE);
        currentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a label to display the best score
        JLabel bestScoreLabel = new JLabel("Best Score: " + Math.round(menu.retrieveScore()));
        bestScoreLabel.setFont(new Font("Courier New", Font.PLAIN, 30));
        bestScoreLabel.setForeground(Color.WHITE);
        bestScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add labels to the label panel
        labelPanel.add(Box.createVerticalGlue());
        labelPanel.add(gameOverLabel);
        labelPanel.add(Box.createVerticalStrut(20)); // Add vertical spacing
        labelPanel.add(currentScoreLabel);
        labelPanel.add(bestScoreLabel);
        labelPanel.add(Box.createVerticalGlue());

        // Create a button panel for Play Again and Main Menu buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a Play Again button
        JButton playAgainButton = createButton("src/resources/play-again.png", "Play Again");
        playAgainButton.addActionListener(e -> handleButtonClick("Play Again"));

        // Create a Main Menu button
        JButton mainMenuButton = createButton("src/resources/main-menu.png", "Main Menu");
        mainMenuButton.addActionListener(e -> handleButtonClick("Main Menu"));

        // Add buttons to the button panel
        buttonPanel.add(playAgainButton);
        buttonPanel.add(Box.createHorizontalStrut(20)); // Add horizontal spacing
        buttonPanel.add(mainMenuButton);

        // Create an overlay panel
        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BoxLayout(overlayPanel, BoxLayout.Y_AXIS));
        overlayPanel.setBackground(new Color(0, 0, 0, 150)); // Adjust the alpha value for transparency
        overlayPanel.setPreferredSize(new Dimension(400, 300)); // Adjust the size as needed

        // Add components to the overlay panel
        overlayPanel.add(labelPanel);
        overlayPanel.add(Box.createVerticalStrut(20)); // Add vertical spacing
        overlayPanel.add(buttonPanel);

        // Add overlay panel to the center of the main panel
        add(overlayPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String imagePath, String buttonText) {
        ImageIcon icon = new ImageIcon(imagePath);
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(450, 100)); // Adjust the size as needed
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }

    private void handleButtonClick(String buttonText) {
        if (buttonText.equals("Play Again")) {
            // Reset the game and switch back to the game panel
            menu.getCardLayout().show(menu.getCardPanel(), "game");
            menu.getCardPanel().remove(GameOverPanel.this);
            menu.revalidate();
            menu.repaint();
            menu.getGamePanel().resetGame();
            menu.getGamePanel().setGameOn(true);
            menu.getGamePanel().setShieldOn(true);
            menu.getGamePanel().setShieldVisible(true);
            menu.requestFocusInWindow();
        } else if (buttonText.equals("Main Menu")) {
            // Switch back to the main menu
            menu.getCardLayout().show(menu.getCardPanel(), "menu");
        }
    }
}