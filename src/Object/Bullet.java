package Object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class Bullet {
    private double x;
    private double y;
    private final Shape shape;
    private final Color color = new Color(255, 255, 255);
    private final float angle;
    private double size;
    private float speed = 1f;

    public Bullet(double x, double y, float angle, double size, float speed) {
        x += Player.PLAYER_SIZE / 2 - (size / 2);
        y += Player.PLAYER_SIZE / 2 - (size / 2);
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.size = size;
        this.speed = speed;
        
        // Create triangle shape
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(0, 0);           // Bottom point
        triangle.lineTo(size/2, size);   // Top point
        triangle.lineTo(size, 0);        // Bottom right point
        triangle.closePath();            // Connect to starting point
        
        this.shape = triangle;
    }

    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    public boolean check(int width, int height) {
        if (x <= -size || y < -size || x > width || y > height) {
            return false;
        } else {
            return true;
        }
    }

    public void draw(Graphics2D g2) {
        AffineTransform oldTransform = g2.getTransform();
        g2.setColor(color);
        g2.translate(x, y);
        
        // Rotate triangle in the direction of movement
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angle - 90), size/2, size/2);
        g2.transform(at);
        
        g2.fill(shape);
        g2.setTransform(oldTransform);
    }

    public Shape getShape() {
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(Math.toRadians(angle - 90), size/2, size/2);
        return new Area(at.createTransformedShape(shape));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public double getCenterX() {
        return x + size / 2;
    }

    public double getCenterY() {
        return y + size / 2;
    }

}
