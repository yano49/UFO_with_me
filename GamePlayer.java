// GamePanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

class Projectile {
    double x, y;
    private GamePanel gamePanel;

    public Projectile(double x, double y, GamePanel gamePanel) {
        this.x = x;
        this.y = y;
        this.gamePanel = gamePanel;
    }

    public void move() {
        x -= gamePanel.getProjectileSpeed(); // speed of the projectile
    }
}

class GamePanel extends JPanel {
    private GameWindow menu;
    private final ArrayList<Projectile> projectiles;
    private Image backgroundImage; // Added to store the background image
    private int ufoX = 0;
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(80, 50);
    private boolean gameOn = false;
    private Timer timer;
    private boolean collisionDetector = false;
    private boolean shieldOn = false;
    private double score = 0;
    private double projectileSpeed = 5;
    private boolean displayStatus = false;
    private int laserAmmo = 1;
    private Laser laser;
    private boolean showufoBound = false;


    public GamePanel(Image backgroundImage, GameWindow window) {
        this.menu = window;
        projectiles = new ArrayList<>();
        this.backgroundImage = backgroundImage;
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOn) {
                    if (!collisionDetector) {
                        score += 0.01;
                        if (projectileSpeed <= 25) {
                            projectileSpeed += score * 0.00005;
                        }

                        moveProjectiles();
                        repaint();
                    } else {
                        if (shieldOn) {
                            shieldOn = false;
                        } else {
                            try {
                                menu.saveScore(score);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            JOptionPane.showMessageDialog(null, "You have Died");
                            menu.getCardLayout().show(menu.getCardPanel(), "menu");
                            gameOn = false;
                            collisionDetector = false;
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

        laser = new Laser(this, laserAmmo);
        laser.activateLaserMouseListener();

    }

    private void moveProjectiles() {
        Random random = new Random();
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (random.nextDouble() < 0.05) {
            // Ensure the projectile is spawned outside the panel's bounds
            double spawnX = panelWidth + 30; // Spawn just beyond the right edge of the panel
            double spawnY = random.nextInt(panelHeight + 1); // Random y-coordinate within panel height
            projectiles.add(new Projectile(spawnX, spawnY, this));
        }

        // Use an iterator to iterate over the projectiles
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();
            if (projectile.x < 0) {
                // Remove projectiles that are out of bounds
                iterator.remove();
            } else {
                collisionDetected(projectile);
            }
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw the UFO
        g.drawImage(new ImageIcon("src/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);

        // Draw the red dots (projectiles)
        for (Projectile projectile : projectiles) {
            g.setColor(Color.LIGHT_GRAY);
            int dotSize = 30; // Set the size of the red dots
            g.fillOval((int) projectile.x, (int) projectile.y, dotSize, dotSize);
        }

        // Draw the score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + Math.round(score), 10, 30); // Adjust the position as needed

        // Draw the speed in the middle right area
        if (displayStatus) {
            String speedText = "Speed: " + String.format("%.2f", projectileSpeed);
            FontMetrics fontMetrics = g.getFontMetrics();
            int speedStringWidth = fontMetrics.stringWidth(speedText);
            int speedX = getWidth() - speedStringWidth - 10; // 10 pixels margin from the right edge
            int speedY = getHeight() / 2 + fontMetrics.getHeight() / 2; // Vertically centered
            g.drawString(speedText, speedX, speedY);

            // Debug: Draw UFO bounding box
            g.setColor(Color.white);
            g.drawRect(ufoX, ufoY, ufoSize.width, ufoSize.height);
        }

        laser.paintComponent(g);

        if (shieldOn) {

        }
    }

    public void setDisplayStatus(boolean displayStatus) {
        this.displayStatus = displayStatus;
    }

    public boolean getDisplayStatus() {
        return displayStatus;
    }

    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getUfoX() {
        return ufoX;
    }

    public int getUfoY() {
        return ufoY;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void removeProjectile(Projectile projectile) {
        projectiles.remove(projectile);
    }

    public void resetGame() {
        score = 0;
        projectileSpeed = 5;
        timer.stop();
        // Clear the list of projectiles
        projectiles.clear();

        // Reset any relevant variables
        // For example:
        ufoX = 0;
        ufoY = 0;

        timer.start();
    }

    public void collisionDetected(Projectile projectile) {
        Rectangle projectileBounds = new Rectangle((int) projectile.x, (int) projectile.y, 30, 30);
        Rectangle ufoBounds = new Rectangle(ufoX, ufoY, ufoSize.width - 10, ufoSize.height);
        Rectangle laserBounds = new Rectangle(laser.getX(), laser.getY(), laser.getLaserWidth(), laser.getLaserHeight());
        if (ufoBounds.intersects(projectileBounds)) {
            collisionDetector = true;
        }
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
