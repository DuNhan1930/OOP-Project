package Component;

import Object.Bullet;
import Object.Effect;
import Object.Player;
import Object.Rocket;
import Object.Sound.Sound;
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
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;

public class PanelGame extends JComponent {

    private Graphics2D g2;
    private BufferedImage image;
    private int width;
    private int height;
    private Thread thread;
    private boolean start = true;
    private Key key;
    private int shotTime;

    //  Game FPS
    private final int FPS = 60;
    private final int TARGET_TIME = 1000000000 / FPS;
    //  Game Object
    private Sound sound;
    private Player player;
    private List<Bullet> bullets;
    private List<Rocket> rockets;
    private List<Effect> boomEffects;
    private int score = 0;
    private List<Star> stars = new ArrayList<>();
    private float currentRocketSpeed = 0.3f;

    // Thêm biến để kiểm tra trạng thái game
    private boolean gameStarted = false;

    public void start() {
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

    private void checkAndUpdateRocketSpeed() {
        float newSpeed = 0.3f + (score / 2) * 0.05f;
        
        if (newSpeed != currentRocketSpeed) {
            currentRocketSpeed = newSpeed;
            for (Rocket rocket : rockets) {
                rocket.setSpeed(currentRocketSpeed);
            }
            System.out.println("Score: " + score + " - New Speed: " + newSpeed);
        }
    }

    private void addRocket() {
        Random ran = new Random();
        int numRockets = 2 + (score / 2);
        
        if (numRockets > 10) {
            numRockets = 10;
        }
        
        for (int i = 0; i < numRockets/2; i++) {
            int locationY = ran.nextInt(height - 50) + 25;
            Rocket rocket = new Rocket();
            rocket.changeLocation(0, locationY);
            rocket.changeAngle(0);
            rocket.setSpeed(currentRocketSpeed);
            rockets.add(rocket);
        }
        
        for (int i = 0; i < numRockets/2; i++) {
            int locationY = ran.nextInt(height - 50) + 25;
            Rocket rocket = new Rocket();
            rocket.changeLocation(width, locationY);
            rocket.changeAngle(180);
            rocket.setSpeed(currentRocketSpeed);
            rockets.add(rocket);
        }
        
        System.out.println("Score: " + score + " - Rockets per wave: " + numRockets);
    }

    private void initObjectGame() {
        sound = new Sound();
        player = new Player();
        player.changeLocation(150, 150);
        rockets = new ArrayList<>();
        boomEffects = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
                    addRocket();
                    sleep(3000);
                }
            }
        }).start();
    }

    private void resetGame() {
        score = 0;
        currentRocketSpeed = 0.3f;
        rockets.clear();
        bullets.clear();
        player.changeLocation(150, 150);
        player.reset();
        gameStarted = false; // Reset về màn hình bắt đầu
    }

    private void initKeyboard() {
        key = new Key();
        requestFocus();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameStarted && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameStarted = true;
                    return;
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
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 5, 3f));
                                } else {
                                    bullets.add(0, new Bullet(player.getX(), player.getY(), player.getAngle(), 15, 3f));
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

    private void initBullets() {
        bullets = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (start) {
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

    private void checkBullets(Bullet bullet) {
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                Area area = new Area(bullet.getShape());
                area.intersect(rocket.getShape());
                if (!area.isEmpty()) {
                    boomEffects.add(new Effect(bullet.getCenterX(), bullet.getCenterY(), 3, 5, 60, 0.5f, new Color(230, 207, 105)));
                    if (!rocket.updateHP(bullet.getSize() + 5)) {
                        score++;
                        checkAndUpdateRocketSpeed();
                        rockets.remove(rocket);
                        sound.soundDestroy();
                        double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                        double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
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

    private void checkPlayer(Rocket rocket) {
        if (rocket != null) {
            Area area = new Area(player.getShape());
            area.intersect(rocket.getShape());
            if (!area.isEmpty()) {
                double rocketHp = rocket.getHP();
                if (!rocket.updateHP(player.getHP())) {
                    rockets.remove(rocket);
                    sound.soundDestroy();
                    double x = rocket.getX() + Rocket.ROCKET_SIZE / 2;
                    double y = rocket.getY() + Rocket.ROCKET_SIZE / 2;
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }
                if (!player.updateHP(rocketHp)) {
                    player.setAlive(false);
                    sound.soundDestroy();
                    double x = player.getX() + Player.PLAYER_SIZE / 2;
                    double y = player.getY() + Player.PLAYER_SIZE / 2;
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.05f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 5, 5, 75, 0.1f, new Color(32, 178, 169)));
                    boomEffects.add(new Effect(x, y, 10, 10, 100, 0.3f, new Color(230, 207, 105)));
                    boomEffects.add(new Effect(x, y, 10, 5, 100, 0.5f, new Color(255, 70, 70)));
                    boomEffects.add(new Effect(x, y, 10, 5, 150, 0.2f, new Color(255, 255, 255)));
                }

            }
        }

    }

    private void drawBackground() {
        GradientPaint gp = new GradientPaint(
            0, 0, new Color(0, 0, 20), 
            0, height, new Color(20, 20, 40)
        );
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
        
        for (Star star : stars) {
            star.update();
            star.draw(g2);
        }
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawGame() {
        if (player.isAlive()) {
            player.draw(g2);
        }
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            if (bullet != null) {
                bullet.draw(g2);
            }
        }
        for (int i = 0; i < rockets.size(); i++) {
            Rocket rocket = rockets.get(i);
            if (rocket != null) {
                rocket.draw(g2);
            }
        }
        for (int i = 0; i < boomEffects.size(); i++) {
            Effect boomEffect = boomEffects.get(i);
            if (boomEffect != null) {
                boomEffect.draw(g2);
            }
        }
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 15f));
        g2.drawString("Score : " + score, 10, 20);
        if (!player.isAlive()) {
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

    private void render() {
        Graphics g = getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    private void sleep(long speed) {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            System.err.println(ex);
        }
    }

    private void initStars() {
        stars.clear();
        Random rand = new Random();
        int numStars = rand.nextInt(100) + 100;
        for (int i = 0; i < numStars; i++) {
            stars.add(new Star());
        }
    }

    private void drawStartScreen() {
        g2.setColor(Color.WHITE);
        
        // Vẽ tiêu đề game
        g2.setFont(getFont().deriveFont(Font.BOLD, 50f));
        String title = "PLANE AND ROCKET";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, height / 3);
        
        // Vẽ hướng dẫn điều khiển
        g2.setFont(getFont().deriveFont(Font.PLAIN, 20f));
        String[] controls = {
            "Controls:",
            "A/D - Rotate ship",
            "SPACE - Speed up",
            "J - Small bullet",
            "K - Big bullet",
            "",
            "Press ENTER to start"
        };
        
        fm = g2.getFontMetrics();
        int y = height / 2;
        for (String line : controls) {
            int x = (width - fm.stringWidth(line)) / 2;
            g2.drawString(line, x, y);
            y += fm.getHeight() + 10;
        }
    }

    private class Star {
        int x, y;
        float alpha = 1.0f;
        float size;
        
        public Star() {
            Random rand = new Random();
            x = rand.nextInt(width);
            y = rand.nextInt(height);
            size = rand.nextFloat() * 2 + 1;
        }
        
        public void update() {
            alpha = (float) (0.5f + Math.random() * 0.5f);
        }
        
        public void draw(Graphics2D g2) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.WHITE);
            g2.fill(new Rectangle2D.Double(x, y, size, size));
        }
    }
}
