package Object;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

/**
 * Class representing the player's aircraft in the game
 */
public class Player extends HpRender {
    // Constants and properties
    public static final double PLAYER_SIZE = 64;
    private final float MAX_SPEED = 1f;
    
    // Position coordinates
    private double x;
    private double y;
    
    // Movement properties
    private float speed = 0f;
    private float angle = 0f;
    private boolean speedUp;
    
    // Player state
    private boolean alive = true;
    
    // Visual elements
    private final Area playerShap;
    private final Image image;          // Normal plane image
    private final Image image_speed;    // Image when speeding up

    /**
     * Constructor - initializes player with default values
     */
    public Player() {
        // Initialize with 50 HP
        super(new HP(50, 50));
        
        // Load player images
        this.image = new ImageIcon(getClass().getResource("/Image/plane.png")).getImage();
        this.image_speed = new ImageIcon(getClass().getResource("/Image/plane_speed.png")).getImage();
        
        // Create player hitbox shape
        Path2D p = new Path2D.Double();
        p.moveTo(0, 15);
        p.lineTo(20, 5);
        p.lineTo(PLAYER_SIZE + 15, PLAYER_SIZE / 2);
        p.lineTo(20, PLAYER_SIZE - 5);
        p.lineTo(0, PLAYER_SIZE - 15);
        playerShap = new Area(p);
    }

    /**
     * Updates player position based on current speed and angle
     */
    public void update() {
        x += Math.cos(Math.toRadians(angle)) * speed;
        y += Math.sin(Math.toRadians(angle)) * speed;
    }

    /**
     * Changes player angle, keeping it within 0-359 degrees
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
     * Renders the player on screen
     */
    public void draw(Graphics2D g2) {
        // Save current transform
        AffineTransform oldTransform = g2.getTransform();
        
        // Move to player position
        g2.translate(x, y);
        
        // Rotate player image
        AffineTransform tran = new AffineTransform();
        tran.rotate(Math.toRadians(angle + 45), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        
        // Draw appropriate image based on speed state
        g2.drawImage(speedUp ? image_speed : image, tran, null);
        
        // Render HP bar
        hpRender(g2, getShape(), y);
        
        // Restore original transform
        g2.setTransform(oldTransform);
    }

    /**
     * Gets player's collision shape at current position and rotation
     */
    public Area getShape() {
        AffineTransform afx = new AffineTransform();
        afx.translate(x, y);
        afx.rotate(Math.toRadians(angle), PLAYER_SIZE / 2, PLAYER_SIZE / 2);
        return new Area(afx.createTransformedShape(playerShap));
    }

    /**
     * Increases player speed when accelerating
     */
    public void speedUp() {
        speedUp = true;
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        } else {
            speed += 0.01f;
        }
    }

    /**
     * Decreases player speed when not accelerating
     */
    public void speedDown() {
        speedUp = false;
        if (speed <= 0) {
            speed = 0;
        } else {
            speed -= 0.003f;
        }
    }

    /**
     * Resets player to initial state
     */
    public void reset() {
        alive = true;
        resetHP();
        angle = 0;
        speed = 0;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
