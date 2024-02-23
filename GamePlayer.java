// GamePanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

class GamePanel extends JPanel {
    private GameWindow menu;
    private final ArrayList<Projectile> projectiles;
    private Image backgroundImage; // Added to store the background image
    private int ufoX = 0;
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(80, 50);
    private boolean gameOn = false;
    private Timer timer;
    private boolean collisionDetect = false;
    private boolean shieldOn = true;
    private double score = 0;
    private double projectileSpeed = 5;
    private boolean displayStatus = false;
    private double laserAmmo = 5;
    private Laser laser;
    private boolean shieldVisible = true;
    private float alpha;
    private Random random = new Random();
    private double asteroidSpawnRate = 3; // Adjust spawn rates as needed
    private double blackHoleSpawnRate = 0.5;
    private double spaceCreatureSpawnRate = 0.8;
    private long second = 0;

    public GamePanel(Image backgroundImage, GameWindow window) {
        this.menu = window;
        projectiles = new ArrayList<>();
        this.backgroundImage = backgroundImage;
        setOpaque(false);

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOn) {
                    if (!collisionDetect) {
                        score += 0.01;
                        if (projectileSpeed <= 25) {
                            projectileSpeed += score * 0.00005;
                        }

                        spawnProjectiles();
                        moveProjectiles();
                        repaint();
                    } else {
                        try {
                            menu.saveScore(score);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        JOptionPane.showMessageDialog(null, "You have Died");
                        menu.getCardLayout().show(menu.getCardPanel(), "menu");
                        gameOn = false;
                        collisionDetect = false;
                        resetGame();
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

        laser = new Laser(this, window);
        laser.activateLaserMouseListener();
        laser.ammoTimer();

        shieldRegenTimer();
    }

    private void spawnProjectiles() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Spawn asteroids
        if (random.nextDouble() < asteroidSpawnRate / 100) {
            double spawnX = panelWidth + 30;
            double spawnY = random.nextInt(panelHeight + 1);
            projectiles.add(new Asteroid((int) spawnX, (int) spawnY,this));
        }

        // Spawn black holes
        if (random.nextDouble() < blackHoleSpawnRate / 100) {
            double spawnX = panelWidth + 30;
            double spawnY = random.nextInt(panelHeight + 1);
            projectiles.add(new BlackHole((int) spawnX, (int) spawnY));
        }

        // Spawn space creatures
        if (random.nextDouble() < spaceCreatureSpawnRate / 100) {
            double spawnX = panelWidth + 30;
            double spawnY = random.nextInt(panelHeight + 1);
            projectiles.add(new SpaceCreature((int) spawnX, (int) spawnY));
        }
    }
// when the objs are removed from arraylist it causes error.

    private void moveProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();
            if (projectile.x < 0) {
                iterator.remove();
            } else {
                collisionDetected(projectile);
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the UFO
        g.drawImage(new ImageIcon("src/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);

        // Draw the projectiles

        //to make sure the specific class from the loop
        for (Projectile projectile : projectiles) {
            if (projectile instanceof Asteroid) {
                Image asteroidImage = new ImageIcon("src/Asteroid.png").getImage();
                g.drawImage(asteroidImage, (int) projectile.x, (int) projectile.y, this);
            } else if (projectile instanceof BlackHole) {
                g.setColor(Color.pink);
                g.drawOval((int) projectile.x, (int) projectile.y, 100, 100);
            } else if (projectile instanceof SpaceCreature) {
                Image spaceCreatureImage = new ImageIcon("src/SpaceCreature.png").getImage();
                g.drawImage(spaceCreatureImage, (int) projectile.x, (int) projectile.y, this);
            }
        }

        // Draw the score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + Math.round(score), 10, 35); // Adjust the position as needed

        laser.paintLaserAmmo(g);
        laser.paintComponent(g);

        // Inside the paintComponent method
        if (shieldOn && shieldVisible) {
            Graphics2D g2d = (Graphics2D) g.create();
            alpha = 0.5f; // Set the transparency level for the shield
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(new Color(56, 182, 255));
            g2d.fillOval(ufoX - 10, ufoY - 10, ufoSize.width + 20, ufoSize.height + 20);
            g2d.dispose();
        }

        // Draw the speed in the middle right area
        if (displayStatus) {
            String speedText = "Speed: " + String.format("%.2f", projectileSpeed);
            FontMetrics speedFontMetrics = g.getFontMetrics();
            int speedStringWidth = speedFontMetrics.stringWidth(speedText);
            int speedX = getWidth() - speedStringWidth - 10; // 10 pixels margin from the right edge
            int speedY = getHeight() / 2 + speedFontMetrics.getHeight() / 2; // Vertically centered
            g.drawString(speedText, speedX, speedY);

            // Debug: Draw UFO bounding box
            g.setColor(Color.white);
            g.drawRect(ufoX, ufoY, ufoSize.width, ufoSize.height);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Try Hard Mode", getWidth() / 2 - 50, 35); // Adjust the position as needed
        }
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
        Rectangle projectileBounds;
        if (projectile instanceof BlackHole) {
            projectileBounds = new Rectangle((int) projectile.x, (int) projectile.y, 100, 100);
        } else {
            projectileBounds = new Rectangle((int) projectile.x, (int) projectile.y, 30, 30);
        }
        Rectangle ufoBounds = new Rectangle(ufoX, ufoY, ufoSize.width - 10, ufoSize.height);
        Rectangle laserBounds = new Rectangle(laser.getX(), laser.getY(), laser.getLaserWidth(), laser.getLaserHeight());
        if (ufoBounds.intersects(projectileBounds)) {
            if (!shieldOn) {
                collisionDetect = true;
                if (menu.soundPlayerPanel.soundFxIsOn()) {
                    menu.soundPlayerPanel.playExplosion();
                }
            } else {
                shieldCollision();
            }
        }
    }

    public void shieldCollision() {
        shieldOn = true; // Activate the shield
        shieldVisible = false;
        repaint();

        Timer gracePeriodTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shieldOn = false; // Deactivate the shield after the grace period
                repaint();
            }
        });
        gracePeriodTimer.setRepeats(false); // Ensure the timer only runs once
        gracePeriodTimer.start();
    }

    public void shieldRegenTimer() {
        Timer regenTimer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shieldOn = true;
                shieldVisible = true;
                repaint();
            }
        });
        regenTimer.setRepeats(true);
        regenTimer.start();
    }

    public void setShieldVisible(boolean shieldVisible) {
        this.shieldVisible = shieldVisible;
    }

    public void setShieldOn(boolean shieldOn) {
        this.shieldOn = shieldOn;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
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

    public double getLaserAmmo() {
        return laserAmmo;
    }

    public void addAmmo(int ammo) {
        laserAmmo += ammo;
    }

    public void minusAmmo(double ammo) {
        laserAmmo -= ammo;
    }
}
