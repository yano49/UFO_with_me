import java.awt.*;

// Abstract class projectile
public abstract class Projectile {
    double x, y;

    public Projectile(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Abstract method for abstract class
    public abstract void move();

    
    public Rectangle getBounds(int width, int height) {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
