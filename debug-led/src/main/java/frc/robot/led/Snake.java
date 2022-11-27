package frc.robot.led;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

public class Snake implements Led {

    enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    };
    int gridSideLength = 20;
    Translation2d[] snake;
    Direction direction = Direction.DOWN;
    XboxController controller = new XboxController(0);
    Timer timer = new Timer();
    Timer targetTimer = new Timer();
    Translation2d target;
    double targetSpawnInterval = 20.0;
    int score;
    NetworkTableEntry scoreEntry;

    public void start(AddressableLEDBuffer buffer, int length) {
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        NetworkTable table = inst.getTable("snake");
        scoreEntry = table.getEntry("score");

        snake = new Translation2d[] {
                new Translation2d(0, 1),
                new Translation2d(0, 2),
                new Translation2d(0, 3),
                new Translation2d(0, 4),
                new Translation2d(0, 5), 
        };
        score = 0;
        spawnTarget();
        timer.reset();
        timer.start(); 
        targetTimer.reset();
        targetTimer.start();
        
    }

    public void run(AddressableLEDBuffer buffer, int length) {
        updateDirection();

        if (timer.get() > .15) {
            moveSnake();
            timer.reset();
        }

        var foundTarget = hasFoundTarget();
        if(targetTimer.get() > .15 || foundTarget) {
            score += 1;
            spawnTarget();
            targetTimer.reset();
        }

        scoreEntry.setNumber(score);
        clearBuffer(buffer);
        drawTarget(buffer);
        drawSnake(buffer);
    }

    private void moveSnake() {
        for (var i = 0; i < snake.length - 1; i++) {
            snake[i] = snake[i + 1];
        }
        var headX = snake[snake.length - 1].getX();
        var headY = snake[snake.length - 1].getY();
        if (direction == Direction.UP) {
            headY--;
        } else if (direction == Direction.DOWN) {
            headY++;
        } else if (direction == Direction.RIGHT) { 
            headX--;
        } else if (direction == Direction.LEFT) {
            headX++;
        }
        snake[4] = new Translation2d((headX + gridSideLength) % gridSideLength, (headY + gridSideLength) % gridSideLength);
    }

    private void updateDirection() {
        var controllerDirection = getControllerDirection();
        if (controllerDirection == Direction.UP && direction != Direction.DOWN
                || controllerDirection == Direction.DOWN && direction != Direction.UP
                || controllerDirection == Direction.LEFT && direction != Direction.RIGHT
                || controllerDirection == Direction.RIGHT && direction != Direction.LEFT) {
            direction = controllerDirection;
        }
    }

    private Direction getControllerDirection() {
        var x = controller.getLeftX();
        var y = controller.getLeftY();
        if (y < -.2) {
            return Direction.UP;
        } else if (y > .2) {
            return Direction.DOWN;
        } else if (x < -.2) {
            return Direction.LEFT;
        } else if (x > .2) {
            return Direction.RIGHT;
        }
        return Direction.NONE;

    }

    private int getPointIndex(Translation2d point) {
        int x = (int) point.getX();
        int y = (int) point.getY();
        return y * gridSideLength + x;
    }

    private void clearBuffer(AddressableLEDBuffer buffer) {
        for (var i = 0; i < gridSideLength * gridSideLength; i++) {
            buffer.setRGB(i, 0, 0, 0);
        }
    }

    private void drawSnake(AddressableLEDBuffer buffer) {
        for (var i = 0; i < snake.length - 1; i+=2) { 
            buffer.setRGB(getPointIndex(snake[i]), 255, 0, 0);
        }
        buffer.setRGB(getPointIndex(snake[snake.length - 1]), 255, 150, 150);
    }

    private void drawTarget(AddressableLEDBuffer buffer) {
        buffer.setRGB(getPointIndex(target), 0, 255, 0);
    }

    private void spawnTarget() {
        var x = getRandomNumber(0, gridSideLength - 1);
        var y = getRandomNumber(0, gridSideLength - 1);
        target = new Translation2d(x,y);
    }

    private boolean hasFoundTarget() {
        return getPointIndex(target) == getPointIndex(snake[0]);
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
