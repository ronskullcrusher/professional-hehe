import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends JFrame {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int NUM_OBSTACLES = 10;
    private static final int OBSTACLE_MIN_SIZE = 50;
    private static final int OBSTACLE_MAX_SIZE = 150;
    private static final int BALL_SIZE = 20;
    private static final int BALL_SPEED = 5;
    private static final Color OBSTACLE_COLOR = Color.RED;
    private static final Color BALL_COLOR = Color.BLUE;
    private static final Color GOAL_COLOR = Color.GREEN;

    private final Random random;
    private final List<Rectangle> obstacles;
    private Rectangle ball;
    private Rectangle goal;
    private Direction ballDirection;

    public Game() {
        setTitle("BrickNBash");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        random = new Random();
        obstacles = new ArrayList<>();
        generateObstacles();
        initializeGoalState();
        initializeBall();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        moveBall(Direction.LEFT);
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveBall(Direction.RIGHT);
                        break;
                    case KeyEvent.VK_UP:
                        moveBall(Direction.UP);
                        break;
                    case KeyEvent.VK_DOWN:
                        moveBall(Direction.DOWN);
                        break;
                }
            }
        });

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OBSTACLE_COLOR);
                for (Rectangle obstacle : obstacles) {
                    g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
                }

                g.setColor(BALL_COLOR);
                g.fillOval(ball.x, ball.y, ball.width, ball.height);
                g.setColor(GOAL_COLOR);
                g.fillRect(goal.x, goal.y, goal.width, goal.height);
            }
        };
        add(panel);
        setVisible(true);
    }

    private void initializeGoalState() {
        do {
            int goalWidth = BALL_SIZE * 2;
            int goalHeight = BALL_SIZE * 2;
            int x = random.nextInt(FRAME_WIDTH - goalWidth);
            int y = random.nextInt(FRAME_HEIGHT - goalHeight);
            goal = new Rectangle(x, y, goalWidth, goalHeight);
        } while (intersectsObstacle(goal));
    }

    private void initializeBall() {
        do {
            int x = random.nextInt(FRAME_WIDTH - BALL_SIZE);
            int y = random.nextInt(FRAME_HEIGHT - BALL_SIZE);
            ball = new Rectangle(x, y, BALL_SIZE, BALL_SIZE);
        } while (intersectsObstacle(ball));
        ballDirection = Direction.RIGHT;
    }

    private void generateObstacles() {
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            obstacles.add(generateUniqueObstacle());
        }
    }

    private Rectangle generateUniqueObstacle() {
        Rectangle obstacle;
        do {
            int obstacleWidth = random.nextInt(OBSTACLE_MAX_SIZE - OBSTACLE_MIN_SIZE) + OBSTACLE_MIN_SIZE;
            int obstacleHeight = random.nextInt(OBSTACLE_MAX_SIZE - OBSTACLE_MIN_SIZE) + OBSTACLE_MIN_SIZE;
            int x = random.nextInt(FRAME_WIDTH - obstacleWidth);
            int y = random.nextInt(FRAME_HEIGHT - obstacleHeight);
            obstacle = new Rectangle(x, y, obstacleWidth, obstacleHeight);
        } while (intersectsObstacle(obstacle));
        return obstacle;
    }

    private boolean intersectsObstacle(Rectangle rect) {
        for (Rectangle obstacle : obstacles) {
            if (obstacle.intersects(rect)) {
                return true;
            }
        }
        return false;
    }

    private void moveBall(Direction direction) {
        int dx = 0, dy = 0;
        switch (direction) {
            case LEFT:
                dx = -BALL_SPEED;
                break;
            case RIGHT:
                dx = BALL_SPEED;
                break;
            case UP:
                dy = -BALL_SPEED;
                break;
            case DOWN:
                dy = BALL_SPEED;
                break;
        }

        Rectangle newBall = new Rectangle(ball);
        newBall.translate(dx, dy);

        if (!newBall.intersects(goal) && !intersectsObstacle(newBall)) {
            ball = newBall;
            repaint();
        }else if(newBall.intersects(goal)){
        JOptionPane.showMessageDialog(this, "YAY YOU WIN !");
            System.exit(0);
        }else{
            JOptionPane.showMessageDialog(this, "Game Over!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}