// Subclass of abstract class projectile
public class Asteroid extends Projectile{
    // Connected to the game panel
    private GamePanel gamePanel;

    public Asteroid(double x, double y, GamePanel gamePanel) {
        super(x, y);
        this.gamePanel = gamePanel;
    }

    // Implementing the abstract method
    @Override
    public void move() {
        x -= gamePanel.getProjectileSpeed(); // The speed of the projectile is determined by the score in the game panel
    }
}
