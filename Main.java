import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.io.*;

public class Main extends JFrame {
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int NUM_OBSTACLES = 5;
    private static final int OBSTACLE_MIN_SIZE = 50;
    private static final int OBSTACLE_MAX_SIZE = 150;
    private static final int BALL_SIZE = 20;
    private static final int BALL_SPEED = 5;
    private static final Color OBSTACLE_COLOR = Color.RED;
    private static final Color BALL_COLOR = Color.BLUE;
    private static final Color GOAL_COLOR = Color.GREEN;
    private double explorationRate = 0.5;
    private final double explorationDecay = 0.01; 
    private final double minExplorationRate = 0.1;
    private final Random random;
    private final List<Rectangle> obstacles;
    private Rectangle ball;
    private Rectangle goal;
    private Direction ballDirection;
    public Boolean isWon = false;
    public Boolean isLost = false;
    private final double learningRate = 0.1;
    private final double discountFactor = 0.9;
    private final int numActions = 4;
    private double[][] qTable;
    private int numWins = 0;
    private int numLosses = 0;
    
    public Main(String mode) {
        setTitle("Random Obstacles");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        random = new Random();
        obstacles = new ArrayList<>();
        generateObstacles();
        initializeGoalState();
        initializeBall();

        if (mode.equals("train") || mode.equals("test")) {
            qTable = new double[1000][numActions]; 
            if (mode.equals("test")) {
                loadQTableFromFile();
            }
        } else {
            System.out.println("Invalid mode. Please specify 'train' or 'test'.");
            System.exit(1);
        }

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

    public List<Integer> getGameState() {
        List<Integer> b = new ArrayList<>();
        boolean right = false, left = false, up = false, down = false;
        if (goal.getCenterX() < ball.getCenterX()) {
            left = true;
        } else {
            right = true;
        }
        if (goal.getCenterY() < ball.getCenterY()) {
            up = true;
        } else {
            down = true;
        }
        b.add(left ? 1 : 0);
        b.add(up ? 1 : 0);
        right = false;
        left = false;
        up = false;
        down = false;
        for (Rectangle obstacle : obstacles) {
            left = ball.intersects(obstacle.getX(), obstacle.getY(), obstacle.getWidth() + 10, obstacle.getHeight())
                    ? true
                    : left;
            right = ball.intersects(obstacle.getX() - 10, obstacle.getY(), obstacle.getWidth(), obstacle.getHeight())
                    ? true
                    : right;
            up = ball.intersects(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight() + 10)
                    ? true
                    : up;
            down = ball.intersects(obstacle.getX(), obstacle.getY() - 10, obstacle.getWidth(), obstacle.getHeight())
                    ? true
                    : down;
        }
        b.add(left ? 1 : 0);
        b.add(right ? 1 : 0);
        b.add(up ? 1 : 0);
        b.add(down ? 1 : 0);
        return b;
    }

    public int playStep(Direction d) {
        return moveBall(d);
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

    private int moveBall(Direction direction) {
        List<Integer> prevState = getGameState();
        int reward=0;
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
        if (newBall.x < 0 || newBall.x + newBall.width > FRAME_WIDTH ||
                newBall.y < 0 || newBall.y + newBall.height > FRAME_HEIGHT) {
            reward -= 5;
        }
        if (!newBall.intersects(goal) && !intersectsObstacle(newBall)) {
            if (getDistance(ball, goal) > getDistance(newBall, goal)) {
                reward += 1;
            } 
            else {
                reward -=1;
            }

            for (Rectangle obs : findNearestObstacles(ball,obstacles)) {
                if (getDistance(ball, obs) < getDistance(newBall, obs)) {
                    reward += 10;
                }else{
                    reward -=10;
                }
            }
            ball = newBall;
            repaint();
        } else if (newBall.intersects(goal)) {
            isWon = true;
            numWins++; 
            reward += 20;
        } else {
            isLost = true;
            numLosses++; 
            reward -= 20;
        }

        return reward;
    }
    public static double getDistance(Rectangle rect1, Rectangle rect2) {
        double centerX1 = rect1.getCenterX();
        double centerY1 = rect1.getCenterY();
        double centerX2 = rect2.getCenterX();
        double centerY2 = rect2.getCenterY();
        return Math.sqrt(Math.pow(centerX2 - centerX1, 2) + Math.pow(centerY2 - centerY1, 2));
    }

    public void updateQTable(List<Integer> state, int action, int reward) {
        int stateIndex = hash(state); 
        double maxQValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numActions; i++) {
            if (qTable[stateIndex][i] > maxQValue) {
                maxQValue = qTable[stateIndex][i];
            }
        }
        qTable[stateIndex][action] += learningRate * (reward + discountFactor * maxQValue - qTable[stateIndex][action]);
    }

    public void reset() {
        obstacles.clear();
        ball = null;
        goal = null;
        isLost = false;
        isWon = false;
        generateObstacles();
        initializeGoalState();
        initializeBall();
        int totalGames = numWins + numLosses;
        double winPercentage = totalGames > 0 ? ((double) numWins / totalGames) * 100 : 0;
        System.out.println("Total games won: " + numWins);
        System.out.println("Total games lost: " + numLosses);
        System.out.println("Average winning percentage: in "+ totalGames  +" games is " + winPercentage + "%");
    }

    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("train") && !args[0].equals("test"))) {
            System.out.println("Usage: java Main <mode>");
            System.out.println("Mode can be 'train' or 'test'.");
            System.exit(1);
        }
        Main a = new Main(args[0]);
        if(args[0].equals("test")){
            Timer timer = new Timer(100, e -> {
                List<Integer> state = a.getGameState();
                int action = a.chooseAction(state);
                int reward = a.playStep(Direction.values()[action]);
                a.updateQTable(state, action, reward);
                if (a.isWon || a.isLost)
                    a.reset();
            });
            timer.start();
        }else{
            while (true) {
                List<Integer> state = a.getGameState();
                int action = a.chooseAction(state);
                int reward = a.playStep(Direction.values()[action]);
                a.updateQTable(state, action, reward);
                if(args[0].equals("train"))
                a.saveQTableToFile();
                if (a.isWon || a.isLost)
                    a.reset();
            }
        }
    }

    public int chooseAction(List<Integer> state) {
        double explorationRate = getExplorationRate(); 
        int stateIndex = hash(state); 
        if (random.nextDouble() < explorationRate) {
            return random.nextInt(numActions); 
        } else {
            double maxQValue = Double.NEGATIVE_INFINITY;
            int bestAction = 0;
            for (int i = 0; i < numActions; i++) {
                if (qTable[stateIndex][i] > maxQValue) {
                    maxQValue = qTable[stateIndex][i];
                    bestAction = i;
                }
            }
            return bestAction;
        }
    }

    private double getExplorationRate() {
        explorationRate -= explorationDecay;
        return Math.max(explorationRate, minExplorationRate); 
    }

    private int hash(List<Integer> state) {
        int hash = 0;
        int multiplier = 1;
        for (int i = 0; i < state.size(); i++) {
            hash += state.get(i) * multiplier;
            multiplier *= 2; 
        }
        return hash % 1000; 
    }

    private void loadQTableFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("qtable.txt"))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int col = 0; col < values.length; col++) {
                    qTable[row][col] = Double.parseDouble(values[col]);
                    System.out.println(qTable[row][col]);
                }
                row++;
            }
        } catch (IOException e) {
            System.out.println("Error reading Q-table from file: " + e.getMessage());
        }
    }

    private void saveQTableToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("qtable.txt"))) {
            for (double[] row : qTable) {
                StringBuilder sb = new StringBuilder();
                for (double value : row) {
                    sb.append(value).append(",");
                }
                sb.deleteCharAt(sb.length() - 1); 
                pw.println(sb.toString());
            }
        } catch (IOException e) {
            System.out.println("Error saving Q-table to file: " + e.getMessage());
        }
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    public static Rectangle[] findNearestObstacles(Rectangle ball, List<Rectangle> obstacles) {
        int maxDistance = 10; 
        int nearestCount = 2; 
        int ballCenterX = ball.x + ball.width / 2;
        int ballCenterY = ball.y + ball.height / 2;
        PriorityQueue<Rectangle> nearestObstaclesQueue = new PriorityQueue<>(nearestCount, Comparator.comparingDouble(o -> distance(ballCenterX, ballCenterY, o)));
        for (Rectangle obstacle : obstacles) {
            double distance = distance(ballCenterX, ballCenterY, obstacle);
            if (distance <= maxDistance) {
                nearestObstaclesQueue.offer(obstacle);
                if (nearestObstaclesQueue.size() > nearestCount) {
                    nearestObstaclesQueue.poll();
                }
            }
        }
        Rectangle[] nearestObstacles = new Rectangle[nearestObstaclesQueue.size()];
        nearestObstaclesQueue.toArray(nearestObstacles);

        return nearestObstacles;
    }

    private static double distance(int ballCenterX, int ballCenterY, Rectangle obstacle) {
        int obstacleCenterX = obstacle.x + obstacle.width / 2;
        int obstacleCenterY = obstacle.y + obstacle.height / 2;
        return Math.sqrt(Math.pow(ballCenterX - obstacleCenterX, 2) + Math.pow(ballCenterY - obstacleCenterY, 2));
    }
}