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
        x -= 5; // speed of the projectile
    }
}

class GamePanel extends JPanel {
    private GameWindow menu;
    private final ArrayList<Projectile> projectiles;
    private Image backgroundImage; // Added to store the background image
    private int ufoX = 0;
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(100, 50);
    private boolean gameOn = false;
    private Timer timer;
    private boolean collisionDetector = false;
    private boolean shieldOn = false;

    public GamePanel(Image backgroundImage, GameWindow window) {
        this.menu = window;
        projectiles = new ArrayList<>();
        this.backgroundImage = backgroundImage;
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOn) {
                    if (!collisionDetector) {
                        moveProjectiles();
                        repaint();
                    } else {
                        if (shieldOn) {

                        } else {
                            JOptionPane.showMessageDialog(null, "You have Died");
                            menu.getCardLayout().show(menu.getCardPanel(), "menu");
                            gameOn = false;
                            collisionDetector = false;
                            timer.stop();
                            resetGame();
                        }
                    }
                }
            }
        });
        timer.start();


        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameOn) {
                    ufoX = e.getX() - ufoSize.width / 2;
                    ufoY = e.getY() - ufoSize.height / 2;
                    repaint();
                }
            }
        });
    }


    private void moveProjectiles() {
        Random random = new Random();
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (random.nextDouble() < 0.05) {
            // Ensure the projectile is spawned outside the panel's bounds
            int spawnX = panelWidth + 30; // Spawn just beyond the right edge of the panel
            int spawnY = random.nextInt(panelHeight + 1); // Random y-coordinate within panel height
            projectiles.add(new Projectile(spawnX, spawnY));
        }

        // Create a list to store projectiles to be removed
        ArrayList<Projectile> projectilesToRemove = new ArrayList<>();

        for (Projectile projectile : projectiles) {
            projectile.move();
            if (projectile.x < 0) {
                // Mark projectiles that are out of bounds for removal
                projectilesToRemove.add(projectile);
            } else {
                collisionDetected(projectile);
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

        // Debug: Draw UFO bounding box
        g.drawRect(ufoX, ufoY, ufoSize.width-10, ufoSize.height);

        // Draw the red dots (projectiles)
        for (Projectile projectile : projectiles) {
            g.setColor(Color.LIGHT_GRAY);
            int dotSize = 30; // Set the size of the red dots
            g.fillOval(projectile.x, projectile.y, dotSize, dotSize);
        }
    }


    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    public void resetGame() {
        // Clear the list of projectiles
        projectiles.clear();

        // Reset any relevant variables
        // For example:
        ufoX = 0;
        ufoY = 0;

        timer.start();
    }


    public void collisionDetected(Projectile projectile) {
        Rectangle projectileBounds = new Rectangle(projectile.x, projectile.y, 30, 30);
        if (new Rectangle(ufoX, ufoY, ufoSize.width-10, ufoSize.height).intersects(projectileBounds)) {
            collisionDetector = true;
            System.out.println("Collision Detected");
        }
    }
}
