package Object;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Effect {
    // Position coordinates
    private final double x;
    private final double y;
    
    // Effect properties
    private final double max_distance;    // Maximum distance particles can travel
    private final int max_size;          // Maximum size of each particle
    private final Color color;           // Color of the effect
    private final int totalEffect;       // Total number of particles
    private final float speed;           // Movement speed of particles
    
    // Current state
    private double current_distance;      // Current travel distance
    private ModelBoom booms[];           // Array of particle objects
    private float alpha = 1f;            // Transparency value

    public Effect(double x, double y, int totalEffect, int max_size, double max_distance, float speed, Color color) {
        this.x = x;
        this.y = y;
        this.totalEffect = totalEffect;
        this.max_size = max_size;
        this.max_distance = max_distance;
        this.speed = speed;
        this.color = color;
        createRandom();
    }

    private void createRandom() {
        booms = new ModelBoom[totalEffect];
        float per = 360f / totalEffect;   // Divide circle into equal segments
        Random ran = new Random();
        
        // Create particles with random sizes at calculated angles
        for (int i = 1; i <= totalEffect; i++) {
            int r = ran.nextInt((int) per) + 1;
            int boomSize = ran.nextInt(max_size) + 1;
            float angle = i * per + r;    // Calculate spread angle
            booms[i - 1] = new ModelBoom(boomSize, angle);
        }
    }

    public void draw(Graphics2D g2) {
        // Save original transform and composite
        AffineTransform oldTransform = g2.getTransform();
        Composite oldComposite = g2.getComposite();
        
        g2.setColor(color);
        g2.translate(x, y);  // Move to effect center
        
        for (ModelBoom b : booms) {
            // Calculate particle position using polar coordinates
            double bx = Math.cos(Math.toRadians(b.getAngle())) * current_distance;
            double by = Math.sin(Math.toRadians(b.getAngle())) * current_distance;
            double boomSize = b.getSize();
            double space = boomSize / 2;
            
            // Fade out effect as particles reach maximum distance
            if (current_distance >= max_distance - (max_distance * 0.7f)) {
                alpha = (float) ((max_distance - current_distance) / (max_distance * 0.7f));
            }
            
            // Clamp alpha between 0 and 1
            if (alpha > 1) {
                alpha = 1;
            } else if (alpha < 0) {
                alpha = 0;
            }
            
            // Draw particle with current transparency
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(new Rectangle2D.Double(bx - space, by - space, boomSize, boomSize));
        }
        
        // Restore original graphics state
        g2.setComposite(oldComposite);
        g2.setTransform(oldTransform);
    }

    public void update() {
        current_distance += speed;  // Move particles outward
    }

    public boolean check() {
        return current_distance < max_distance;  // Check if effect is still active
    }
}
