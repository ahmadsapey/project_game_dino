import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DinoGame extends JPanel implements ActionListener, MouseListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 300;
    private static final int GROUND_Y = 240;
    private static final int DINO_SIZE = 40;
    private static final int JUMP_SPEED = 16;
    private static final int GRAVITY = 1;

    private Timer timer;
    private boolean running = true;
    private boolean jumping = false;
    private int dinoY = GROUND_Y - DINO_SIZE;
    private int dy = 0;
    private int score = 0;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private ArrayList<Rectangle> birds = new ArrayList<>();
    private Random random = new Random();
    private int obstacleTimer = 0;
    private int obstacleInterval = 90;
    private int birdTimer = 0;
    private int birdInterval = 140;

    public DinoGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.white);
        addMouseListener(this);
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    private void drawGame(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        boolean nightMode = score > 500;

        if (nightMode) {
            g2.setColor(new Color(18, 24, 72));
        } else {
            g2.setColor(new Color(230, 230, 255));
        }
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        if (nightMode) {
            g2.setColor(new Color(255, 255, 224));
            g2.fillOval(650, 30, 50, 50);
            g2.setColor(new Color(255, 255, 255, 180));
            for (int i = 0; i < 40; i++) {
                int x = 20 + i * 18;
                int y = 20 + (i % 5) * 12;
                g2.fillOval(x, y, 3, 3);
            }
        }

        g2.setColor(nightMode ? new Color(56, 60, 65) : new Color(48, 42, 36));
        g2.fillRect(0, GROUND_Y + DINO_SIZE / 2, WIDTH, HEIGHT - GROUND_Y - DINO_SIZE / 2);

        g2.setColor(nightMode ? Color.white : new Color(80, 80, 80));
        g2.fillRect(50, dinoY, DINO_SIZE, DINO_SIZE);
        g2.setColor(nightMode ? Color.black : Color.black);
        g2.fillOval(58, dinoY + 10, 10, 10);
        g2.drawLine(55, dinoY + 30, 70, dinoY + 30);

        g2.setColor(new Color(34, 139, 34));
        for (Rectangle obs : obstacles) {
            g2.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        for (Rectangle bird : birds) {
            g2.setColor(nightMode ? Color.lightGray : Color.black);
            g2.fillOval(bird.x, bird.y, bird.width, bird.height);
            g2.drawLine(bird.x - 6, bird.y + bird.height / 2, bird.x + bird.width / 2, bird.y);
            g2.drawLine(bird.x + bird.width / 2, bird.y, bird.x + bird.width + 6, bird.y + bird.height / 2);
        }

        g2.setColor(nightMode ? Color.white : Color.black);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 10, 24);
        if (nightMode) {
            g2.drawString("Mode Malam", 10, 44);
        }

        if (!running) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 32));
            String msg = "Game Over - Klik untuk mulai ulang";
            FontMetrics fm = g2.getFontMetrics();
            int msgX = (WIDTH - fm.stringWidth(msg)) / 2;
            int msgY = HEIGHT / 2;
            g2.setColor(new Color(0, 0, 0, 180));
            g2.drawString(msg, msgX, msgY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            updateDino();
            updateObstacles();
            updateBirds();
            checkCollision();
            score++;
            obstacleTimer++;
            if (obstacleTimer >= obstacleInterval) {
                obstacleTimer = 0;
                addObstacle();
                if (obstacleInterval > 50) {
                    obstacleInterval--;
                }
            }

            if (score > 500) {
                birdTimer++;
                if (birdTimer >= birdInterval) {
                    birdTimer = 0;
                    addBird();
                    if (birdInterval > 70) {
                        birdInterval -= 2;
                    }
                }
            }
        }
        repaint();
    }

    private void updateDino() {
        if (jumping) {
            dinoY -= dy;
            dy -= GRAVITY;
            if (dinoY >= GROUND_Y - DINO_SIZE) {
                dinoY = GROUND_Y - DINO_SIZE;
                jumping = false;
                dy = 0;
            }
        }
    }

    private void updateObstacles() {
        Iterator<Rectangle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Rectangle obs = iterator.next();
            obs.x -= 6;
            if (obs.x + obs.width < 0) {
                iterator.remove();
            }
        }
    }

    private void updateBirds() {
        Iterator<Rectangle> iterator = birds.iterator();
        while (iterator.hasNext()) {
            Rectangle bird = iterator.next();
            bird.x -= 9;
            if (bird.x + bird.width < 0) {
                iterator.remove();
            }
        }
    }

    private void addObstacle() {
        int height = 20 + random.nextInt(30);
        Rectangle obs = new Rectangle(WIDTH, GROUND_Y - height, 20 + random.nextInt(10), height);
        obstacles.add(obs);
    }

    private void addBird() {
        int birdHeight = 12 + random.nextInt(8);
        int birdY = 100 + random.nextInt(60);
        Rectangle bird = new Rectangle(WIDTH, birdY, 32, birdHeight);
        birds.add(bird);
    }

    private void checkCollision() {
        Rectangle dinoHitbox = new Rectangle(50, dinoY, DINO_SIZE, DINO_SIZE);
        for (Rectangle obs : obstacles) {
            if (dinoHitbox.intersects(obs)) {
                running = false;
                timer.stop();
                return;
            }
        }
        for (Rectangle bird : birds) {
            if (dinoHitbox.intersects(bird)) {
                running = false;
                timer.stop();
                return;
            }
        }
    }

    private void resetGame() {
        running = true;
        jumping = false;
        dinoY = GROUND_Y - DINO_SIZE;
        dy = 0;
        score = 0;
        obstacles.clear();
        birds.clear();
        obstacleTimer = 0;
        obstacleInterval = 90;
        birdTimer = 0;
        birdInterval = 140;
        timer.start();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!running) {
            resetGame();
            return;
        }
        if (!jumping) {
            jumping = true;
            dy = JUMP_SPEED;
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chrome Dino 2D");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new DinoGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
