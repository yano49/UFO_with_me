import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame implements ActionListener {
    private JButton playButton;
    private JButton settingButton;
    private JPanel cardPanel;

    public GameWindow() {
        ImageIcon originalSpaceBackground = new ImageIcon("src/Space_Background2.png");
        Image resizedImage = originalSpaceBackground.getImage().getScaledInstance(StandardFrame.screenWidth, StandardFrame.screenHeight, Image.SCALE_SMOOTH);
        ImageIcon spaceBackground = new ImageIcon(resizedImage);

        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setIcon(spaceBackground);
        setContentPane(backgroundLabel);

        // Creating the menu panel !!!
        JPanel menuPanel = createMenuPanel();
        menuPanel.setOpaque(false);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(menuPanel, "menu");
        cardPanel.setOpaque(false);

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("UFO with me");
        setIconImage(new ImageIcon("src/iconImage.png").getImage());
        setSize(StandardFrame.frameSize);
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

        // Creating the grid panel
        JPanel gridPanel = new JPanel(new GridLayout(4, 3, 0, 20));
        gridPanel.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            gridPanel.add(new ExtraPanels());
        }

        gridPanel.add(playButton);
        gridPanel.add(new ExtraPanels());

        gridPanel.add(new ExtraPanels());
        gridPanel.add(settingButton);

        for (int i = 0; i < 4; i++) {
            gridPanel.add(new ExtraPanels());
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(0, 400));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(30, 31, 33));
        titlePanel.setPreferredSize(new Dimension(0, 250));
        JLabel imageLabel = new JLabel();
        ImageIcon titleImage = new ImageIcon("src/MenuTitle.png");
        imageLabel.setIcon(titleImage);
        titlePanel.add(imageLabel);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            GamePanel gamePanel = new GamePanel(new ImageIcon("src/Space_Background2.png").getImage());
            cardPanel.add(gamePanel, "game");
            CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
            cardLayout.show(cardPanel, "game");
        } else if (e.getSource() == settingButton) {

        }
    }
}

class ExtraPanels extends JPanel {
    public ExtraPanels() {
        setOpaque(false);
    }
}
