// Subclass of the abstract Projectile class
public class BlackHole extends Projectile {

    public BlackHole(int x, int y) {
        super(x, y);
    }

    // Abstract method of projectile class
    @Override
    public void move() {
        x -= 2;  // The speed of the blackhole projectile
    }
}
