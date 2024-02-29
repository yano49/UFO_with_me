public class SpaceCreature extends Projectile {
    private double speed = 2;
    private double angle = 0;
    private double frequency = 0.01;  // Adjust the frequency as needed
    private GamePanel gamePanel;

    public SpaceCreature(int x, int y, GamePanel gamePanel) {
        super(x, y);
        this.gamePanel = gamePanel;
    }

    @Override
    public void move() {
        x -= speed;

        // Adjust the y position based on the difference between SpaceCreature's y and UFO's y
        double deltaY = gamePanel.getUfoY() - y;
        y += deltaY * 0.01; // Adjust the multiplier to control the follow speed

        angle += frequency;

        // Respawn when reaching the left edge
        if (x <= 0) {
            respawn();
        }
    }
    
    public void respawn() {
        x = gamePanel.getWidth() + 30;
        y = (int) (Math.random() * gamePanel.getHeight());
        angle = 0;
    }
}
