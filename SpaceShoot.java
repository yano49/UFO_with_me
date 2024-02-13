import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Projectile {
    int x, y;

    public Projectile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        x -= 10; // speed of the projectile
    }
}

class GamePanel extends JPanel {
    private final ArrayList<Projectile> projectiles;
    private Image backgroundImage; // Added to store the background image
    private int ufoX = 0;
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(100, 50);

    public GamePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        projectiles = new ArrayList<>();
        Timer timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveProjectiles();
                repaint();
            }
        });
        timer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                ufoX = e.getX() - ufoSize.width / 2;
                ufoY = e.getY() - ufoSize.height / 2;
                repaint();
            }
        });
    }

    private void moveProjectiles() {
        Random random = new Random();
        if (random.nextDouble() < 0.05) {
            projectiles.add(new Projectile(getWidth(), random.nextInt(getHeight() + 1)));
        }

        // Create a list to store projectiles to be removed
        ArrayList<Projectile> projectilesToRemove = new ArrayList<>();

        for (Projectile projectile : projectiles) {
            projectile.move();
            if (projectile.x < 0) {
                // Mark projectiles that are out of bounds for removal
                projectilesToRemove.add(projectile);
            }
        }

        // Remove projectiles that are out of bounds
        projectiles.removeAll(projectilesToRemove);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw the UFO
        g.setColor(Color.gray);
        g.fillOval(ufoX, ufoY, ufoSize.width, ufoSize.height);

        // Draw the red dots (projectiles)
        for (Projectile projectile : projectiles) {
            g.setColor(Color.LIGHT_GRAY);
            int dotSize = 30; // Set the size of the red dots
            g.fillOval(projectile.x, projectile.y, dotSize, dotSize);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SidewaysAsteroids");

        // Load the background image
        ImageIcon imageIcon = new ImageIcon("src/Space_Background2.png");
        Image backgroundImage = imageIcon.getImage();

        // Create the game panel with the background image
        GamePanel gamePanel = new GamePanel(backgroundImage);

        // Use BorderLayout for better layout management
        frame.setLayout(new BorderLayout());

        // Add the game panel to the center of the frame
        frame.add(gamePanel, BorderLayout.CENTER);

        // Set the size of the frame

        // Make the frame visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
