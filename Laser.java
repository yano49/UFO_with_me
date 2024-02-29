import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class Laser extends JPanel {
    private int x;
    private int y;
    private int laserWidth = 10;
    private int laserHeight = 5;
    private int laserSpeed = 3;
    private GamePanel gamePanel;
    private boolean laserActivate = false;
    private Timer timer;
    private GameWindow menu;

    public Laser(GamePanel gamePanel, GameWindow window) {
        this.gamePanel = gamePanel;
        this.x = gamePanel.getUfoX() + 60;
        this.y = gamePanel.getUfoY() + 25;
        this.menu = window;
        activateLaserMouseListener();
        repaint();
    }

    public void activateLaserMouseListener() {
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gamePanel.getLaserAmmo() > 0) {
                    laserActivate = true;
                    if (menu.soundPlayerPanel.soundFxIsOn()) {
                        menu.soundPlayerPanel.playLaser();
                    }
                    gamePanel.minusAmmo(0.5);
                    x = gamePanel.getUfoX() + 60;
                    y = gamePanel.getUfoY() + 25;
                    timer = new Timer(5, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            shootLaser();
                            if (x > gamePanel.getWidth()) {
                                laserActivate = false;
                                timer.stop();
                            }
                            // Check for collision with projectiles
                            checkProjectileCollision();
                            repaint();
                        }
                    });
                    timer.start();
                }
            }
        });
    }

    public void ammoTimer() {
        Timer ammoTime = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gamePanel.getLaserAmmo() < 5) {
                    gamePanel.addAmmo(1);
                    gamePanel.repaint(); // Repaint the panel to update the ammo indicators
                }
            }
        });
        ammoTime.setRepeats(true); // Set the timer to repeat
        ammoTime.start(); // Start the timer
    }

    public void shootLaser() {
        x += laserSpeed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (laserActivate) {
            g.setColor(Color.red);
            g.fillRect(x, y, laserWidth, laserHeight);
        }
    }

    // Method to check if the laser intersects with any projectiles
    private void checkProjectileCollision() {
        Rectangle laserBounds = new Rectangle(x, y, laserWidth, laserHeight);
        synchronized (gamePanel.getProjectiles()) {
            Iterator<Projectile> iterator = gamePanel.getProjectiles().iterator();
            while (iterator.hasNext()) {
                Projectile projectile = iterator.next();
                Rectangle projectileBounds = new Rectangle((int) projectile.x, (int) projectile.y, 30, 30);
                if (laserBounds.intersects(projectileBounds)) {
                    if (menu.soundPlayerPanel.soundFxIsOn()) {
                        menu.soundPlayerPanel.playExplosion();
                    }
                    gamePanel.setScore(gamePanel.getScore() + 1);
                    // If the laser intersects with a projectile, remove the projectile
                    iterator.remove();
                }
            }
        }
    }

    public void paintLaserAmmo(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String ammoText = "Ammo :";
        int ammoTextWidth = g.getFontMetrics().stringWidth(ammoText);
        g.drawString(ammoText, gamePanel.getWidth() - 200, 37);

        int ammoX = gamePanel.getWidth() - 180 + ammoTextWidth; // Adjust for padding
        int ammoY = 20;

        for (int i = 0; i < gamePanel.getLaserAmmo(); i++) {
            g.fillRect(ammoX, ammoY, 10, 20);
            ammoX += 20;
        }
    }



    public int getLaserWidth() {
        return laserWidth;
    }

    public int getLaserHeight() {
        return laserHeight;
    }
}
