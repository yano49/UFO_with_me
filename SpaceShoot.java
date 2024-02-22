package SpaceShooterGame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

abstract class GameObject {
    int x, y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void move();

    public Rectangle getBounds(int width, int height) {
        return new Rectangle(x, y, width, height);
    }
}

class Projectile extends GameObject {
    public Projectile(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x += 10; // Adjust the movement speed for projectiles
    }
}

class AmmoPickup extends GameObject {
    public AmmoPickup(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 5; // Adjust the movement speed for ammo pickups
    }
}

class ShieldPowerUp extends GameObject {
    public ShieldPowerUp(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 3; // Adjust the movement speed for shield power-ups
    }
}

class BlackHole extends GameObject {
    public BlackHole(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 2; // Adjust the movement speed for black holes
    }
}

class SpaceCreature extends GameObject {
    public SpaceCreature(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 4; // Adjust the movement speed for space creatures
    }
}

class Player extends GameObject {
    private Dimension size;
    private ArrayList<Projectile> projectiles;

    public Player(int x, int y, int width, int height) {
        super(x, y);
        this.size = new Dimension(width, height);
        this.projectiles = new ArrayList<>();
    }

    public Dimension getSize() {
        return size;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void shoot() {
        projectiles.add(new Projectile(x + size.width, y + size.height / 2));
    }

    @Override
    public void move() {
        // Player movement logic (if any)
    }
}

class GamePanel extends JPanel {
    private final ArrayList<GameObject> gameObjects;
    private int ammoCount;
    private boolean shieldActive;
    private int shieldDuration;
    private int lives = 3;
    private int score = 0;
    private boolean gameOver = false;
    private Player player;

    private Image backgroundImage;

    public GamePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        gameObjects = new ArrayList<>();
        ammoCount = 100;
        shieldActive = false;
        shieldDuration = 0;

        player = new Player(0, 0, 100, 50);
        gameObjects.add(player);

        Timer timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    moveGameObjects();
                    updateGame();
                    checkCollisions();
                    checkGameOver();
                    repaint();
                }
            }
        });
        timer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                player.x = e.getX() - player.getSize().width / 2;
                player.y = e.getY() - player.getSize().height / 2;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) {
                    resetGame();
                } else {
                    player.shoot(); // Player shoots when mouse is clicked
                }
            }
        });
    }

    private void resetGame() {
        gameObjects.clear();
        player = new Player(0, 0, 100, 50);
        gameObjects.add(player);
        ammoCount = 100;
        shieldActive = false;
        shieldDuration = 0;
        lives = 3;
        score = 0;
        gameOver = false;
    }

    private void moveGameObjects() {
        Random random = new Random();

        // Move projectiles logic
        Iterator<Projectile> projectileIterator = player.getProjectiles().iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.move();
        }

        // Move other game objects (ammo pickups, shield power-ups, etc.)
        Iterator<GameObject> gameObjectIterator = gameObjects.iterator();
        while (gameObjectIterator.hasNext()) {
            GameObject gameObject = gameObjectIterator.next();
            gameObject.move();
            if (gameObject.x < 0) {
                gameObjectIterator.remove();
            }
        }

        // Generate new game objects
        if (random.nextDouble() < 0.05) {
            gameObjects.add(new AmmoPickup(getWidth(), random.nextInt(getHeight() + 1)));
        }
        if (random.nextDouble() < 0.03) {
            gameObjects.add(new BlackHole(getWidth(), random.nextInt(getHeight() + 1)));
        }
        if (random.nextDouble() < 0.02) {
            gameObjects.add(new SpaceCreature(getWidth(), random.nextInt(getHeight() + 1)));
        }
    }

    private void updateGame() {
        // Update game state logic (if any)
    }

    private void checkCollisions() {
        Iterator<Projectile> projectileIterator = player.getProjectiles().iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            for (GameObject gameObject : gameObjects) {
                if (gameObject instanceof AmmoPickup) {
                    // Ammo pickup collision logic
                    if (projectile.getBounds(30, 30).intersects(gameObject.getBounds(20, 20))) {
                        projectileIterator.remove();  // Use iterator's remove method
                        gameObjects.remove(gameObject);
                        ammoCount += 20; // Increase ammo count
                        score++; // Update the score
                    }
                } else if (gameObject instanceof BlackHole || gameObject instanceof SpaceCreature) {
                    // Obstacle collision logic
                    if (projectile.getBounds(30, 30).intersects(gameObject.getBounds(40, 40))) {
                        projectileIterator.remove();  // Use iterator's remove method
                        gameObjects.remove(gameObject);
                        score++; // Update the score
                    }
                }
            }
        }
    }

    private void checkGameOver() {
        if (lives <= 0) {
            gameOver = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                g.setColor(Color.gray);
                g.fillOval(gameObject.x, gameObject.y, player.getSize().width, player.getSize().height);
            } else if (gameObject instanceof Projectile) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillOval(gameObject.x, gameObject.y, 10, 10);
            } else if (gameObject instanceof AmmoPickup) {
                g.setColor(Color.GREEN);
                g.fillRect(gameObject.x, gameObject.y, 20, 20);
            } else if (gameObject instanceof BlackHole) {
                g.setColor(Color.yellow);
                g.fillOval(gameObject.x, gameObject.y, 40, 40);
            } else if (gameObject instanceof SpaceCreature) {
                g.setColor(Color.BLUE);
                g.fillOval(gameObject.x, gameObject.y, 50, 50);
            }
        }

        for (Projectile projectile : player.getProjectiles()) {
            g.setColor(Color.YELLOW);
            g.fillOval(projectile.x, projectile.y, 10, 10);
        }

        g.setColor(Color.WHITE);
        g.drawString("Ammo: " + ammoCount, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
        g.drawString("Score: " + score, 10, 60);

        if (gameOver) {
            // Display game over message and score
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String gameOverMessage = "Game Over";
            String scoreMessage = "Score: " + score;
            g.drawString(gameOverMessage, getWidth() / 2 - g.getFontMetrics().stringWidth(gameOverMessage) / 2, getHeight() / 2 - 20);
            g.drawString(scoreMessage, getWidth() / 2 - g.getFontMetrics().stringWidth(scoreMessage) / 2, getHeight() / 2 + 20);
            g.drawString("Click to play again", getWidth() / 2 - g.getFontMetrics().stringWidth("Click to play again") / 2, getHeight() / 2 + 60);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter Game");

        ImageIcon imageIcon = new ImageIcon("src/SpaceShooterGame/Space_Background2.png");
        Image backgroundImage = imageIcon.getImage();

        GamePanel gamePanel = new GamePanel(backgroundImage);

        frame.setLayout(new BorderLayout());
        frame.add(gamePanel, BorderLayout.CENTER);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
