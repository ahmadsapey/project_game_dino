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
    private Random random = new Random();
    private int obstacleTimer = 0;
    private int obstacleInterval = 90;

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
        g2.setColor(new Color(48, 42, 36));
        g2.fillRect(0, GROUND_Y + DINO_SIZE / 2, WIDTH, HEIGHT - GROUND_Y - DINO_SIZE / 2);

        g2.setColor(new Color(80, 80, 80));
        g2.fillRect(50, dinoY, DINO_SIZE, DINO_SIZE);
        g2.setColor(Color.black);
        g2.fillOval(58, dinoY + 10, 10, 10);
        g2.drawLine(55, dinoY + 30, 70, dinoY + 30);

        g2.setColor(Color.red.darker());
        for (Rectangle obs : obstacles) {
            g2.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        g2.setColor(Color.black);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Score: " + score, 10, 24);

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

    private void addObstacle() {
        int height = 20 + random.nextInt(30);
        Rectangle obs = new Rectangle(WIDTH, GROUND_Y - height, 20 + random.nextInt(10), height);
        obstacles.add(obs);
    }

    private void checkCollision() {
        Rectangle dinoHitbox = new Rectangle(50, dinoY, DINO_SIZE, DINO_SIZE);
        for (Rectangle obs : obstacles) {
            if (dinoHitbox.intersects(obs)) {
                running = false;
                timer.stop();
                break;
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
        obstacleTimer = 0;
        obstacleInterval = 90;
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
