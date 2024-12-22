package Object;

public class BulletFactory {
    public static Bullet createBullet(double x, double y, float angle, double size, float speed) {
        return new Bullet(x, y, angle, size, speed);
    }
} 