public class Asteroid extends Projectile{
    private GamePanel gamePanel;

    public Asteroid(double x, double y, GamePanel gamePanel) {
        super(x, y);
        this.gamePanel = gamePanel;
    }

    @Override
    public void move() {
        x -= gamePanel.getProjectileSpeed();
    }
}
