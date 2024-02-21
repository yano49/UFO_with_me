import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class Laser extends JPanel {
    private int x;
    private int y;
    private int laserWidth = 10;
    private int laserHeight = 5;
    private int laserSpeed = 5;
    private GamePanel gamePanel;
    private int laserAmmo = 1;
    private boolean laserActivate = false;
    private Timer timer;

    public Laser(GamePanel gamePanel, int laserAmmo) {
        this.gamePanel = gamePanel;
        this.x = gamePanel.getUfoX() + 60;
        this.y = gamePanel.getUfoY() + 25;
        this.laserAmmo = laserAmmo;
        activateLaserMouseListener();
        repaint();
    }

    public void activateLaserMouseListener() {
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (laserAmmo > 0) {
                    laserActivate = true;
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
                    gamePanel.setScore(gamePanel.getScore() + 1);
                    // If the laser intersects with a projectile, remove the projectile
                    iterator.remove();
                }
            }
        }
    }


    public int getLaserWidth() {
        return laserWidth;
    }

    public int getLaserHeight() {
        return laserHeight;
    }
}
