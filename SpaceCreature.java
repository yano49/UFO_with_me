public class SpaceCreature extends Projectile {

    public SpaceCreature(int x, int y) {
        super(x, y);
    }

    @Override
    public void move() {
        x -= 4;
    }
}
