package Object;

/**
 * Class representing a single particle in an explosion effect
 */
public class ModelBoom {
    // Size of the particle
    private final double size;
    
    // Angle of particle movement (in degrees)
    private final float angle;

    /**
     * Constructor for creating a new particle
     * @param size Size of the particle
     * @param angle Direction angle of the particle movement
     */
    public ModelBoom(double size, float angle) {
        this.size = size;
        this.angle = angle;
    }

    /**
     * Get particle size
     * @return Size of the particle
     */
    public double getSize() {
        return size;
    }

    /**
     * Get movement angle
     * @return Angle in degrees
     */
    public float getAngle() {
        return angle;
    }
}
