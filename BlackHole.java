public class BlackHole extends Projectile {

    public BlackHole(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 2;
    }
}
