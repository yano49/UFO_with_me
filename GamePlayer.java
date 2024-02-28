// GamePanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;
import javax.imageio.ImageIO;

class GamePanel extends JPanel {
    private GameWindow menu;
    private final ArrayList<Projectile> projectiles;
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
    private boolean graceState = false;
    private BufferedImage[] backgroundImages;
    private BufferedImage backgroundImage1;
    private BufferedImage backgroundImage2;
    private BufferedImage backgroundImage3;
    private BufferedImage backgroundImage4;
    private BufferedImage backgroundImage5;

    private int imageIndex = 0;
    private float imageAlpha;
    private Timer transitionTimer;

    public GamePanel(GameWindow window) {
        this.menu = window;
        projectiles = new ArrayList<>();
        setOpaque(false);
        imageIndex = 0;
        imageAlpha = 0.0f;

        loadImage();
        if (isVisible()) {
            startTransitionTimer();
        }

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
            projectiles.add(new SpaceCreature((int) spawnX, (int) spawnY, this));
        }
    }
// when the objs are removed from arraylist it causes error.

    private void moveProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            projectile.move();
            if (projectile.x < 0) {
                if (projectile instanceof SpaceCreature) {
                    SpaceCreature spaceCreature = (SpaceCreature)  projectile;
                    spaceCreature.respawn();
                } else {
                    iterator.remove();
                }
            } else {
                collisionDetected(projectile);
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d2 = (Graphics2D) g.create();
        paintBackgroundImage(g2d2);

        g2d2.dispose();

        // Draw the UFO
        if (graceState) {
            Graphics2D g2d = (Graphics2D) g.create();
            alpha = 0.5f; // Set the transparency level for the shield
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(new ImageIcon("src/resources/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);
            g2d.dispose();
        } else {
            g.drawImage(new ImageIcon("src/resources/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);
        }

        // Draw the projectiles

        //to make sure the specific class from the loop
        for (Projectile projectile : projectiles) {
            if (projectile instanceof Asteroid) {
                Image asteroidImage = new ImageIcon("src/resources/Asteroid.png").getImage();
                g.drawImage(asteroidImage, (int) projectile.x, (int) projectile.y, this);
            } else if (projectile instanceof BlackHole) {
                Image blackholeImage = new ImageIcon("src/resources/Blackhole.png").getImage();
                g.drawImage(blackholeImage, (int) projectile.x, (int) projectile.y,this);
            } else if (projectile instanceof SpaceCreature) {
                Image spaceCreatureImage = new ImageIcon("src/resources/SpaceCreature.png").getImage();
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

        imageIndex = 0;
        alpha = 0;

        timer.start();
    }

    public void collisionDetected(Projectile projectile) {
        Rectangle projectileBounds;
        if (projectile instanceof BlackHole) {
            projectileBounds = new Rectangle((int) projectile.x, (int) projectile.y, 300, 300);
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
        graceState = true;
        shieldVisible = false;
        repaint();

        Timer gracePeriodTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shieldOn = false; // Deactivate the shield after the grace period
                graceState = false;
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

    public void startTransitionTimer() {
        transitionTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transitionImage(); // Call the transitionImage method here
                repaint();
            }
        });
        transitionTimer.setRepeats(true);
        transitionTimer.start();
    }

    public void transitionImage() {
        imageAlpha = 0; // Reset imageAlpha to 0 at the start of the transition
        Timer imageTransitionTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageAlpha += 2; // Adjust the increment for smoother transition
                if (imageAlpha >= 100) {
                    imageAlpha = 0; // Reset imageAlpha to 0 when the animation completes
                    ((Timer) e.getSource()).stop(); // Stop the timer when the animation completes
                    imageIndex++;
                }
                repaint();
            }
        });
        imageTransitionTimer.setRepeats(true);
        imageTransitionTimer.start();
    }

    public void paintBackgroundImage(Graphics2D g2d) {
        if (imageIndex >= backgroundImages.length) {
            imageIndex = 0;
        }

        float currentAlpha = (float) (1.0 - (imageAlpha / 100.0));
        BufferedImage currentImage = backgroundImages[imageIndex];
        BufferedImage nextImage = backgroundImages[(imageIndex + 1) % backgroundImages.length];

        BufferedImage combined = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dCombined = combined.createGraphics();

        g2dCombined.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
        g2dCombined.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);

        if (imageAlpha > 0) {
            float nextAlpha = (float) (imageAlpha / 100.0);
            g2dCombined.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nextAlpha));
            g2dCombined.drawImage(nextImage, 0, 0, getWidth(), getHeight(), null);
        }

        g2d.drawImage(combined, 0, 0, null);
        g2dCombined.dispose();
    }

    public void loadImage() {
        try {
            backgroundImage1 = ImageIO.read(new File("src/resources/Space_Background2.png"));
            backgroundImage2 = ImageIO.read(new File("src/resources/Image2.png"));
            backgroundImage3 = ImageIO.read(new File("src/resources/Image3.png"));
            backgroundImage4 = ImageIO.read(new File("src/resources/Image4.png"));
            backgroundImage5 = ImageIO.read(new File("src/resources/Image5.png"));
            backgroundImages = new BufferedImage[] {
                    backgroundImage1,
                    backgroundImage2,
                    backgroundImage3,
                    backgroundImage4,
                    backgroundImage5,
            };

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
