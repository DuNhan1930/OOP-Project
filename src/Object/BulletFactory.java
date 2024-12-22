package Object;

public class BulletFactory {
    public static Bullet createStandardBullet(Player player) {
        return new Bullet(player.getX(), player.getY(), player.getAngle(), 5, 3f); // Standard bullet
    }

    public static Bullet createBigBullet(Player player) {
        return new Bullet(player.getX(), player.getY(), player.getAngle(), 15, 3f); // Big bullet
    }
} 