// The game panel is where the main game runs
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
    private GameWindow menu; // Connected to the game window 
    private final ArrayList<Projectile> projectiles; 
    private double projectileSpeed = 5; // The initial speed of the projectile
    // The coordinates and size of the ufo
    private int ufoX = 0; 
    private int ufoY = 0;
    private Dimension ufoSize = new Dimension(80, 50);
    private boolean gameOn = false; // true when game panel is open 
    private Timer timer; 
    private boolean collisionDetect = false; // true when there is a collision
    private boolean shieldOn = true; // to track the status of the shield
    private boolean graceState = false; // to track whether the grace period is still active
    private boolean shieldVisible = true;  // to track whether if the shield is visible or not
    private double score = 0; // The initialization of the score
    private boolean displayStatus = false; // Tracks whether the try hard mode is on or not
    private double laserAmmo = 5;  // Tracking laser ammo in the game panel
    private Laser laser; // Connecting the laser class
    private float alpha;  // To control the opcaity 
    private Random random = new Random();
    // Individual projectile spawn rates 
    private double asteroidSpawnRate = 3;
    private double blackHoleSpawnRate = 0.5;
    private double spaceCreatureSpawnRate = 0.8;
    private BufferedImage[] backgroundImages;  // An array of the background images
    private BufferedImage backgroundImage1;
    private BufferedImage backgroundImage2;
    private BufferedImage backgroundImage3;
    private BufferedImage backgroundImage4;
    private BufferedImage backgroundImage5;
    private int imageIndex = 0; // The index of the image it currently is on
    private float imageAlpha; // The opacity value needed for the fading background animation transition 
    private Timer transitionTimer;  // The timer for the transition timer

    public GamePanel(GameWindow window) {
        this.menu = window;
        projectiles = new ArrayList<>();
        setOpaque(false);
        imageIndex = 0;
        imageAlpha = 0.0f;

        loadImage();
        if (isVisible()) {
            // Method that handles the background transition
            startTransitionTimer();
        }

        // The timer for the game projectiles to spawn
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOn) {
                    if (!collisionDetect) { 
                        // When there isn't a collision the score readily increases
                        // the projectile speed is increase comparative to the score but highest set to 25
                        score += 0.01;
                        if (projectileSpeed <= 25) {
                            projectileSpeed += score * 0.00005;
                        }

                        // Also spawn and handle projectile animations 
                        spawnProjectiles();
                        moveProjectiles();
                        repaint();
                    } else {
                        // When collsions is detected the score saving process begins
                        try {
                            menu.saveScore(score);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        // The Message dialog showing that you have died
                        JOptionPane.showMessageDialog(null, "You have Died");
                        // returns back to the main menu
                        menu.getCardLayout().show(menu.getCardPanel(), "menu");
                        // The game is not on in 
                        gameOn = false;
                        // Collision is reseted
                        collisionDetect = false;
                        // The game reseted
                        resetGame();
                    }
                }
            }
        });
        timer.start();

        // The mouse listner tracks the movement of the mouse
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

        // Laser functions are added
        laser = new Laser(this, window);
        laser.activateLaserMouseListener();
        laser.ammoTimer();

        // Adding the automatic shield regen timer 
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

    private void moveProjectiles() {
        // Iterator class is used to avoid exceptions which arrive from deleting elements in the array
        // This method is used to handle the movements of the projectiles and removing them once they are out of bounds
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
        // This part  is where the background is handled
        Graphics2D g2d2 = (Graphics2D) g.create();
        paintBackgroundImage(g2d2);

        g2d2.dispose();

        // This part draws the UFO 
        if (graceState) {
            // This part is to show when the grace period is active
            Graphics2D g2d = (Graphics2D) g.create();
            alpha = 0.5f; // Set the transparency level for the shield
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(new ImageIcon("src/resources/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);
            g2d.dispose();
        } else {
            g.drawImage(new ImageIcon("src/resources/ufoImage2transparent.png").getImage(), ufoX, ufoY, ufoSize.width, ufoSize.height, this);
        }

        // This part draws the different types of projectiles which are looping
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

        // This part draws the score 
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + Math.round(score), 10, 35); // Adjust the position as needed

        // This part drasws the laser when it's activated
        laser.paintLaserAmmo(g);
        laser.paintComponent(g);

        // This part draws the sheld 
        if (shieldOn && shieldVisible) {
            Graphics2D g2d = (Graphics2D) g.create();
            alpha = 0.5f; // Set the transparency level for the shield
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(new Color(56, 182, 255));
            g2d.fillOval(ufoX - 10, ufoY - 10, ufoSize.width + 20, ufoSize.height + 20);
            g2d.dispose();
        }

        // This part darws the text when the display status is on
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
            g.drawString("Try Hard Mode", getWidth() / 2 - 50, 35); // Adjusting the position relative the screen size
        }
    }

    public void resetGame() {
        // This method is to reset all the values
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
        // This is the part that handles that collisions detections 
        Rectangle projectileBounds;
        // Setting the boundaries of the objects
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
                    // Explosion sound effects when there is a collision
                    menu.soundPlayerPanel.playExplosion();
                }
            } else {
                // If the shield is on this activates
                shieldCollision();
            }
        }
    }

    public void shieldCollision() {
        shieldOn = true; // Activate the shield
        graceState = true; // A small period given to prevents the player from collisions
        shieldVisible = false;
        repaint();

        // The grace period is 1.5 seconds
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
        // The methods handles the part where the shield regenerates 
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
        // This part handles the part which counts when the new bacground changes 
        // Background changes in 30 seconds
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
        // This part handles the animation of the transition
        imageAlpha = 0; // Reset imageAlpha to 0 at the start of the transition
        Timer imageTransitionTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageAlpha += 2; // Can adjust the increment for smoother transition
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
        // This part handles the drawing of the animation and the transitions of the background
        
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
        // Loads the background images
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

    // Getter and Setters 

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
