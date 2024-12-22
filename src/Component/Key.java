package Component;

/**
 * Class to handle keyboard input states for game controls
 */
public class Key {
    // Movement controls
    private boolean key_right;    // D key - rotate right
    private boolean key_left;     // A key - rotate left
    private boolean key_space;    // Spacebar - speed up
    
    // Weapon controls
    private boolean key_j;        // J key - fire small bullet
    private boolean key_k;        // K key - fire big bullet
    
    // System controls
    private boolean key_enter;    // Enter key - start/restart game
    private boolean key_r;        // R key - reset high score

    // Getters and setters for key states
    public boolean isKey_enter() {
        return key_enter;
    }

    public void setKey_enter(boolean key_enter) {
        this.key_enter = key_enter;
    }

    public boolean isKey_right() {
        return key_right;
    }

    public void setKey_right(boolean key_right) {
        this.key_right = key_right;
    }

    public boolean isKey_left() {
        return key_left;
    }

    public void setKey_left(boolean key_left) {
        this.key_left = key_left;
    }

    public boolean isKey_space() {
        return key_space;
    }

    public void setKey_space(boolean key_space) {
        this.key_space = key_space;
    }

    public boolean isKey_j() {
        return key_j;
    }

    public void setKey_j(boolean key_j) {
        this.key_j = key_j;
    }

    public boolean isKey_k() {
        return key_k;
    }

    public void setKey_k(boolean key_k) {
        this.key_k = key_k;
    }

    public boolean isKey_r() {
        return key_r;
    }

    public void setKey_r(boolean key_r) {
        this.key_r = key_r;
    }
}
