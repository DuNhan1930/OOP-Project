package Object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Class responsible for rendering health bar visualization
 */
public class HpRender {
    // Reference to HP object being rendered
    private final HP hp;

    /**
     * Constructor
     * @param hp HP object to be rendered
     */
    public HpRender(HP hp) {
        this.hp = hp;
    }

    /**
     * Renders the health bar
     * @param g2 Graphics context
     * @param shape Shape to render HP bar relative to
     * @param y Vertical offset
     */
    protected void hpRender(Graphics2D g2, Shape shape, double y) {
        // Only render HP bar if health is not full
        if (hp.getCurrentHp() != hp.getMAX_HP()) {
            // Calculate vertical position above the shape
            double hpY = shape.getBounds().getY() - y - 10;
            
            // Draw background bar (gray)
            g2.setColor(new Color(70, 70, 70));
            g2.fill(new Rectangle2D.Double(0, hpY, Player.PLAYER_SIZE, 2));
            
            // Draw health bar (red)
            g2.setColor(new Color(253, 91, 91));
            double hpSize = hp.getCurrentHp() / hp.getMAX_HP() * Player.PLAYER_SIZE;
            g2.fill(new Rectangle2D.Double(0, hpY, hpSize, 2));
        }
    }

    /**
     * Reduces current HP by specified amount
     * @param cutHP Amount of HP to reduce
     * @return true if entity is still alive, false if HP <= 0
     */
    public boolean updateHP(double cutHP) {
        hp.setCurrentHp(hp.getCurrentHp() - cutHP);
        return hp.getCurrentHp() > 0;
    }

    /**
     * Get current HP value
     */
    public double getHP() {
        return hp.getCurrentHp();
    }

    /**
     * Reset HP to maximum value
     */
    public void resetHP() {
        hp.setCurrentHp(hp.getMAX_HP());
    }
}
