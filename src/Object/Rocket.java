package Object;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

/**
 * Class representing an enemy rocket in the game
 */
public class Rocket extends HpRender {
    // Constants
    public static final double ROCKET_SIZE = 50;
    
    // Position coordinates
    private double x;
    private double y;
    
    // Movement properties
    private float speed = 0.3f;
    private float angle = 0;
    
    // Visual elements
    private final Image image;
    private final Area rocketShap;

    /**
     * Constructor - initializes rocket with default values
     */
    public Rocket() {
        // Initialize with 20 HP
        super(new HP(20, 20));
        
        // Load rocket image
        this.image = new ImageIcon(getClass().getResource("/Image/rocket.png")).getImage();
        
        // Create rocket hitbox shape
        Path2D p = new Path2D.Double();
        p.moveTo(0, ROCKET_SIZE / 2);
        p.lineTo(15, 10);
        p.lineTo(ROCKET_SIZE - 5, 13);
        p.lineTo(ROCKET_SIZE + 10, ROCKET_SIZE / 2);
        p.lineTo(ROCKET_SIZE - 5, ROCKET_SIZE - 13);
        p.lineTo(15, ROCKET_SIZE - 10);
        rocketShap = new Area(p);
    }

    /**
     * Updates rocket position based on current speed and angle
     */
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    /**
     * Changes rocket angle, keeping it within 0-359 degrees
     */
    public void changeAngle(float angle) {
        if (angle < 0) {
            angle = 359;
        } else if (angle > 359) {
            angle = 0;
        }
        this.angle = angle;
    }

    /**
     * Renders the rocket on screen
     */
    public void draw(Graphics2D g2) {
        // Save current transform
        AffineTransform oldTransform = g2.getTransform();
        
        // Move to rocket position
        g2.translate(x, y);
        
        // Rotate rocket image
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), ROCKET_SIZE / 2, ROCKET_SIZE / 2);
        
        // Draw rocket image
        g2.drawImage(image, tran, null);
        
        // Get current shape and render HP bar
        Shape shap = getShape();
        hpRender(g2, shap, y);
        
        // Restore original transform
        g2.setTransform(oldTransform);
    }

    /**
     * Gets rocket's collision shape at current position and rotation
     */
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), ROCKET_SIZE / 2, ROCKET_SIZE / 2);
        return new Area(afx.createTransformedShape(rocketShap));
    }

    /**
     * Checks if rocket is within game boundaries
     * @param width Game area width
     * @param height Game area height
     * @return true if rocket is within bounds, false otherwise
     */
    public boolean check(int width, int height) {
        Rectangle size = getShape().getBounds();
        if (x <= -size.getWidth() || y < -size.getHeight() || x > width || y > height) {
            return false;
        } else {
            return true;
        }
    }

    // Getters and setters
    public void changeLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public float getAngle() {
        return angle;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }
}
