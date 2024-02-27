public class SpaceCreature extends Projectile {

    private double speed = 5;
    private double amplitude = 100;  // Adjust the amplitude as needed
    private double frequency = 0.01;  // Adjust the frequency as needed
    private double angle = 0;
    private GamePanel gamePanel;

    public SpaceCreature(int x, int y, GamePanel gamePanel) {
        super(x, y);
        this.gamePanel = gamePanel;
    }

    @Override
    public void move() {
        x -= speed;
        y = (int) (amplitude * Math.sin(angle)) + gamePanel.getHeight() / 2;

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
