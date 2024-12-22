package Component;

import Object.Bullet;
import Object.Effect;
import Object.Player;
import Object.Rocket;
import Object.Star;
import Object.Sound.Sound;
import Object.BulletFactory;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import java.io.*;

public class PanelGame extends JComponent {

    // Graphics and rendering variables
    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private final boolean start = true;
    private Key key;
    private int shotTime;

    // Game FPS settings
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;

    // Main game objects
    private Sound sound;              // Game sounds
    private Player player;            // Player ship
    private LinkedList<Bullet> bullets;     // List of bullets
    private List<Rocket> rockets;     // List of enemy rockets
    private List<Effect> boomEffects; // Explosion effects
    private int score = 0;            // Player score
    private List<Star> stars = new ArrayList<>();  // Background stars
    private float currentRocketSpeed = 0.3f;       // Current rocket speed

    // Game state
    private boolean gameStarted = false;
    private int highestScore = 0; // Variable to store highest score

    /**
     * Initialize game and start the game loop
     */
    public void start() {
        loadHighScore();
        width = getWidth();
        height = getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        initStars();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    long startTime = System.nanoTime();
                    drawBackground();
                    if (gameStarted) {
                        drawGame();
                    } else {
                        drawStartScreen();
                    }
                    render();
                    long time = System.nanoTime() - startTime;
                    if (time < TARGET_TIME) {
                        long sleep = (TARGET_TIME - time) / 1000000;
                        sleep(sleep);
                    }
                }
            }
        });
        initObjectGame();
        initKeyboard();
        initBullets();
        thread.start();
    }

    /**
     * Update rocket speed based on current score
     * Speed increases as score gets higher
     */
    private void checkAndUpdateRocketSpeed() {
        float newSpeed = 0.3f + (score / 2) * 0.05f;
        
        if (newSpeed != currentRocketSpeed) {
            currentRocketSpeed = newSpeed;
            for (Rocket rocket : rockets) {
                rocket.setSpeed(currentRocketSpeed);
            }
        }
    }

    /**
     * Add new rockets to the game
     * Number of rockets increases with score
     * Rockets spawn from both sides of the screen
     */
    private void addRocket() {
        Random ran = new Random();
        int numRockets = 2 + (score / 2);
        
        if (numRockets > 10) {
            numRockets = 10;
        }
        
        // Add rockets from left side
        for (int i = 0; i < numRockets/2; i++) {
            int locationY = ran.nextInt(height - 50) + 25;
            Rocket rocket = new Rocket();
            rocket.changeLocation(0, locationY);
            rocket.changeAngle(0);
            rocket.setSpeed(currentRocketSpeed);
            rockets.add(rocket);
        }
        
        // Add rockets from right side
        for (int i = 0; i < numRockets/2; i++) {
            int locationY = ran.nextInt(height - 50) + 25;
            Rocket rocket = new Rocket();
            rocket.changeLocation(width, locationY);
            rocket.changeAngle(180);
            rocket.setSpeed(currentRocketSpeed);
            rockets.add(rocket);
        }
    }

    /**
     * Initialize all game objects and start rocket spawning thread
     */
    private void initObjectGame() {
        // Initialize game sound system
        sound = new Sound();
        
        // Create player object and set starting position
        player = new Player();
        player.changeLocation(150, 150);
        
        // Initialize empty lists for rockets and explosion effects
        rockets = new ArrayList<>();
        boomEffects = new ArrayList<>();
        
        // Create and start a new thread for rocket spawning
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    // Only spawn rockets if the game has started
                    if (gameStarted) {
                        addRocket();
                    }
                    // Wait 3 seconds before next spawn
                    sleep(3000);
                }
            }
        }).start();
    }

    /**
     * Reset game to initial state
     * Clears all objects and resets score
     */
    private void resetGame() {
        score = 0;
        currentRocketSpeed = 0.3f;
        rockets.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
        gameStarted = false; // Reset to start screen
    }

    /**
     * Initialize keyboard controls and input handling
     * Sets up continuous movement and shooting
     */
    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check if the game has not started
                if (!gameStarted) {
                    // Start the game when the Enter key is pressed
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        gameStarted = true;
                        return;
                    } 
                    // Reset the high score when the 'R' key is pressed from the start screen
                    else if (e.getKeyCode() == KeyEvent.VK_R) {
                        resetHighScore();
                    }
                }
                
                if (gameStarted) {
                    if (e.getKeyCode() == KeyEvent.VK_A) {
                        key.setKey_left(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_D) {
                        key.setKey_right(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        key.setKey_space(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_J) {
                        key.setKey_j(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_K) {
                        key.setKey_k(true);
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        key.setKey_enter(true);
                    }
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (gameStarted) {
                    if (e.getKeyCode() == KeyEvent.VK_A) {
                        key.setKey_left(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_D) {
                        key.setKey_right(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        key.setKey_space(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_J) {
                        key.setKey_j(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_K) {
                        key.setKey_k(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        key.setKey_enter(false);
                    } else if (e.getKeyCode() == KeyEvent.VK_R) {
                        key.setKey_r(false);
                    }   
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                float s = 0.5f;
                while (start) {
                    if (player.isAlive()) {
                        float angle = player.getAngle();
                        if (key.isKey_left()) {
                            angle -= s;
                        }
                        if (key.isKey_right()) {
                            angle += s;
                        }
                        if (key.isKey_j() || key.isKey_k()) {
                            if (shotTime == 0) {
                                if (key.isKey_j()) {
                                    // Create and add a standard bullet
                                    bullets.addFirst(BulletFactory.createStandardBullet(player));
                                } else {
                                    // Create and add a big bullet
                                    bullets.addFirst(BulletFactory.createBigBullet(player));
                                }
                                sound.soundShoot();
                            }
                            shotTime++;
                            if (shotTime == 15) {
                                shotTime = 0;
                            }
                        } else {
                            shotTime = 0;
                        }
                        if (key.isKey_space()) {
                            player.speedUp();
                        } else {
                            player.speedDown();
                        }
                        player.update();
                        player.changeAngle(angle);
                    } else {
                        if (key.isKey_enter()) {
                            resetGame();
                        }
                    }
                    // Update rocket positions and check collisions
                    for (int i = 0; i < rockets.size(); i++) {
                        Rocket rocket = rockets.get(i);
                        if (rocket != null) {
                            rocket.update();
                            if (!rocket.check(width, height)) {
                                rockets.remove(rocket);
                            } else {
                                if (player.isAlive()) {
                                    checkPlayer(rocket);
                                }
                            }
                        }
                    }
                    sleep(5);
                }
            }
        }).start();
    }

    /**
     * Initialize bullet system and management thread
     * Handles bullet movement and cleanup
     */
    private void initBullets() {
        bullets = new LinkedList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    // Update and check bullets
                    for (int i = 0; i < bullets.size(); i++) {
                        Bullet bullet = bullets.get(i);
                        if (bullet != null) {
                            bullet.update();
                            checkBullets(bullet);
                            if (!bullet.check(width, height)) {
                                bullets.remove(bullet);
                            }
                        } else {
                            bullets.remove(bullet);
                        }
                    }
                    // Update explosion effects
                    for (int i = 0; i < boomEffects.size(); i++) {
                        Effect boomEffect = boomEffects.get(i);
                        if (boomEffect != null) {
                            boomEffect.update();
                            if (!boomEffect.check()) {
                                boomEffects.remove(boomEffect);
                            }
                        } else {
                            boomEffects.remove(boomEffect);
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    /**
     * Check collisions between bullets and rockets
     * Handles damage, destruction and effects
     */
    private void checkBullets(Bullet bullet) {
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                // Create collision area
                Area area = new Area(bullet.getShape());
                area.intersect(rocket.getShape());
                
                // Check for collision
                if (!area.isEmpty()) {
                    // Add hit effect
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    
                    // Update rocket health and check for destruction
                    if (!rocket.updateHP(bullet.getSize() + 5)) {
                        score++;
                        checkAndUpdateRocketSpeed();
                        rockets.remove(rocket);
                        sound.soundDestroy();
                        
                        // Calculate explosion center
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                        
                        // Add explosion effects
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                        boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                        boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                        boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                    } else {
                        sound.soundHit();
                    }
                    bullets.remove(bullet);
                }
            }
        }
    }

    /**
     * Check collisions between player and rockets
     * Handles player damage and game over condition
     */
    private void checkPlayer(Rocket rocket) {
        if (rocket != null) {
            Area area = new Area(player.getShape());
            area.intersect(rocket.getShape());
            if (!area.isEmpty()) {
                double rocketHp = rocket.getHP();
                // Check rocket destruction
                if (!rocket.updateHP(player.getHP())) {
                    rockets.remove(rocket);
                    sound.soundDestroy();
                    double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                    double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                    // Add explosion effects
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }
                // Check player destruction
                if (!player.updateHP(rocketHp)) {
                    player.setAlive(false);
                    sound.soundDestroy();
                    double x = player.getX() + Player.PLAYER_SIZE / 2;
                    double y = player.getY() + Player.PLAYER_SIZE / 2;
                    // Add explosion effects
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }
            }
        }
    }

    /**
     * Draw game background with gradient and stars
     * Creates space-like atmosphere
     */
    private void drawBackground() {
        // Create gradient background
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(0, 0, 20), 
            0, height, new Color(20, 20, 40)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
        
        // Update and draw stars
        for (Star star : stars) {
            star.update();
            star.draw(g2);
        }
        
        // Reset composite to fully opaque
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    /**
     * Draw all game objects
     * Includes player, bullets, rockets, effects and UI
     */
    private void drawGame() {
        // Draw player if alive
        if (player.isAlive()) {
            player.draw(g2);
        }
        
        // Draw bullets
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
        
        // Draw rockets
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                rocket.draw(g2);
            }
        }
        
        // Draw explosion effects
        for (int i = 0; i < boomEffects.size(); i++) {
            Effect boomEffect = boomEffects.get(i);
            if (boomEffect != null) {
                boomEffect.draw(g2);
            }
        }
        
        // Draw score
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString("Score : " + score, 10, 20);
        g2.drawString("Highest Score : " + highestScore, 10, 40);
        
        // Draw game over screen if player is dead
        if (!player.isAlive()) {
            updateHighScore();
            String text = "GAME OVER";
            String textKey = "Press Enter to Continue ...";
            g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D r2 = fm.getStringBounds(text, g2);
            double textWidth = r2.getWidth();
            double textHeight = r2.getHeight();
            double x = (width - textWidth) / 2;
            double y = (height - textHeight) / 2;
            g2.drawString(text, (int) x, (int) y + fm.getAscent());
            
            g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
            fm = g2.getFontMetrics();
            r2 = fm.getStringBounds(textKey, g2);
            textWidth = r2.getWidth();
            textHeight = r2.getHeight();
            x = (width - textWidth) / 2;
            y = (height - textHeight) / 2;
            g2.drawString(textKey, (int) x, (int) y + fm.getAscent() + 50);
        }
    }

    /**
     * Render the current frame to screen
     */
    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Utility function to pause thread
     * Used for controlling game timing
     */
    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Initialize background stars
     * Creates random star pattern
     */
    private void initStars() {
        stars.clear();
        Random rand = new Random();
        int numStars = rand.nextInt(100) + 100;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star(width, height));
        }
    }

    /**
     * Draw the start screen with game title and control instructions
     */
    private void drawStartScreen() {
        g2.setColor(Color.WHITE);
        
        // Draw game title
        g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
        String title = "PLANE AND ROCKET";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, height / 3);
        
        // Draw highest score
        g2.setFont(getFont().deriveFont(Font.PLAIN, 20f));
        String highScoreText = "Highest Score: " + highestScore;
        fm = g2.getFontMetrics();
        int highScoreX = (width - fm.stringWidth(highScoreText)) / 2;
        g2.drawString(highScoreText, highScoreX, height / 3 + fm.getHeight() + 10);
        
        // Draw control instructions
        String[] controls = {
            "Controls:",
            "A/D - Rotate ship",
            "SPACE - Speed up", 
            "J - Small bullet",
            "K - Big bullet",
            "",
            "Press ENTER to start",
            "Press R to reset Highest Score"
        };
        
        fm = g2.getFontMetrics();
        int y = height / 2;
        for (String line : controls) {
            int x = (width - fm.stringWidth(line)) / 2;
            g2.drawString(line, x, y);
            y += fm.getHeight() + 10;
        }
    }

    /**
     * Update the highest score if current score is higher
     */
    private void updateHighScore() {
        if (score > highestScore) {
            highestScore = score;
            saveHighScore(); // Save the new high score to file
        }
    }

    /**
     * Save the highest score to file
     */
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Component/highscore.txt"))) {
            writer.write(String.valueOf(highestScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the highest score from file
     */
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Component/highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                highestScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the highest score to zero
     */
    private void resetHighScore() {
        highestScore = 0;
        saveHighScore();
    }
}