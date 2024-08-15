
# BrickNBash Game

BrickNBash is a simple Java Swing-based game where the player controls a ball to navigate through obstacles and reach the goal.

## How to Play

1. **Objective**: Guide the ball to reach the green goal without colliding with any red obstacles.
2. **Controls**: Use the arrow keys to move the ball left, right, up, or down.
3. **Win Condition**: When the ball successfully reaches the goal, a "YOU WON" message appears.
4. **Lose Condition**: If the ball collides with any obstacle, a "Game Over!" message appears, and the game ends.

## Features

- Randomly generated obstacles with varying sizes and positions for replayability.
- Randomly placed goal to provide a new challenge in each game session.
- Smooth ball movement with collision detection to ensure fair gameplay.
- Color-coded elements for easy identification: red for obstacles, blue for the ball, and green for the goal.
- Game window size is fixed at 800x600 pixels to maintain consistent gameplay experience.

## Instructions for Running the Game

1. Ensure you have Java installed on your system.
2. Compile the `Game.java` file.
3. Run the compiled Java program.
4. Use the arrow keys to control the ball and navigate through obstacles to reach the goal.

## Development Notes

- The game is developed using Java Swing, providing a simple and lightweight GUI framework for desktop applications.
- Object-oriented programming principles are utilized for modular and organized code structure.
- Random number generation is used to create dynamic obstacle positions and sizes, adding variability to each game session.
- Key listener is implemented to capture user input for controlling the ball's movement.
- Collision detection algorithms are employed to handle interactions between the ball, obstacles, and the goal.

## Acknowledgments

- This game is created as a learning project and for entertainment purposes.
- Inspired by classic arcade games that challenge players' agility and problem-solving skills.

Enjoy playing BrickNBash! If you encounter any issues or have suggestions for improvement, feel free to provide feedback.

---

# BrickNBash Reinforcement Learning

BrickNBash Reinforcement Learning is a Java Swing-based game that implements reinforcement learning techniques to train an agent to navigate through obstacles and reach the goal. The game environment consists of a ball, red obstacles, and a green goal.

## Features

- **Reinforcement Learning**: The game employs Q-learning, a popular reinforcement learning technique, to train the agent.
- **State Representation**: States are represented using the relative positions of the ball, obstacles, and goal.
- **Action Selection**: The agent chooses actions (movement directions) based on Q-values learned during training.
- **Exploration vs. Exploitation**: Balances exploration and exploitation using an exploration rate that decays over time.
- **Reward System**: The agent receives rewards for reaching the goal and penalties for colliding with obstacles or hitting the boundaries.
- **Training and Testing Modes**: Supports both training and testing modes to observe the agent's learning progress.
- **Persistence**: Saves and loads Q-table to/from a file to retain learned knowledge across sessions.

## How to Play

1. **Objective**: Guide the ball to reach the green goal while avoiding red obstacles.
2. **Controls**: The agent controls the ball's movements autonomously.
3. **Training Mode**: The agent learns through trial and error, adjusting its behavior based on rewards and penalties.
4. **Testing Mode**: Evaluate the agent's performance after training by observing its navigation through the environment.

## Instructions for Running the Game

1. Ensure you have Java installed on your system.
2. Compile the `Main.java` file.
3. Run the compiled Java program with one of the following modes:
   - `train`: Initiates the training mode to train the agent.
   - `test`: Initiates the testing mode to evaluate the agent's performance.
   Example: `java Main train` or `java Main test`

## Development Notes

- **Java Swing**: The game utilizes Java Swing for GUI components and rendering.
- **Q-learning**: Implements Q-learning algorithm for reinforcement learning, updating Q-values based on rewards and exploration.
- **State Representation**: Represents game states as features for Q-learning, capturing relevant information for decision-making.
- **Exploration vs. Exploitation**: Balances exploration (random actions) and exploitation (choosing actions based on learned Q-values) to discover optimal strategies.
- **Persistence**: Saves and loads the Q-table to/from a text file for persistence and reusability of learned knowledge.

## Acknowledgments

- This project is developed as an educational demonstration of reinforcement learning concepts.
- Inspired by the application of reinforcement learning in autonomous agent navigation and decision-making.

Enjoy exploring BrickNBash Reinforcement Learning! If you encounter any issues or have suggestions for improvement, feel free to provide feedback.

---
