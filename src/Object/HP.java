package Object;

/**
 * Class representing health points (HP) system
 */
public class HP {
    // Maximum health points possible
    private double MAX_HP;
    
    // Current health points
    private double currentHp;

    /**
     * Get maximum HP value
     */
    public double getMAX_HP() {
        return MAX_HP;
    }

    /**
     * Set maximum HP value
     * @param MAX_HP The maximum HP to set
     */
    public void setMAX_HP(double MAX_HP) {
        this.MAX_HP = MAX_HP;
    }

    /**
     * Get current HP value
     */
    public double getCurrentHp() {
        return currentHp;
    }

    /**
     * Set current HP value
     * @param currentHp The current HP to set
     */
    public void setCurrentHp(double currentHp) {
        this.currentHp = currentHp;
    }

    /**
     * Constructor with initial HP values
     * @param MAX_HP Maximum HP value
     * @param currentHp Current HP value
     */
    public HP(double MAX_HP, double currentHp) {
        this.MAX_HP = MAX_HP;
        this.currentHp = currentHp;
    }

    /**
     * Default constructor
     */
    public HP() {
    }
}
