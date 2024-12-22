package Object;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Star {
    // Star's position coordinates on the screen
    int x, y;
    // Star's transparency/opacity level (0.0 to 1.0)
    float alpha = 1.0f;
    // Star's size in pixels
    float size;
    // Screen dimensions
    private int width;
    private int height;

    /**
     * Creates a new star with random position and size
     * Position is within game screen bounds
     * Size ranges from 1-3 pixels
     */
    public Star(int width, int height) {
        this.width = width;
        this.height = height;
        Random rand = new Random();
        x = rand.nextInt(width);
        y = rand.nextInt(height);
        size = rand.nextFloat() * 2 + 1;
    }

    /**
     * Updates star's transparency to create a twinkling effect
     * Alpha varies between 0.5 and 1.0
     */
    public void update() {
        alpha = (float) (0.5f + Math.random() * 0.5f);
    }

    /**
     * Renders the star on the screen as a small rectangle
     * Uses the current alpha value for transparency
     * @param g2 Graphics context to draw with
     */
    public void draw(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(x, y, size, size));
    }
}