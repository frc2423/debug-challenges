package frc.robot.led;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;

public class Snake implements Led {

    enum Direction {
        UP, DOWN, LEFT, RIGHT, NONE
    };

    Translation2d[] snake;
    Direction direction = Direction.DOWN;
    XboxController controller = new XboxController(0);
    Timer timer = new Timer();

    public void start(AddressableLEDBuffer buffer, int length) {
        snake = new Translation2d[] {
                new Translation2d(0, 1),
                new Translation2d(0, 2),
                new Translation2d(0, 3),
                new Translation2d(0, 4),
                new Translation2d(0, 5),
        };
        timer.reset();
        timer.start();
    }

    public void run(AddressableLEDBuffer buffer, int length) {
        updateDirection();

        if (timer.get() > .15) {
            moveSnake();
            timer.reset();
        }

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
        } else if (direction == Direction.LEFT) {
            headX--;
        } else if (direction == Direction.RIGHT) {
            headX++;
        }
        snake[4] = new Translation2d((headX + 20) % 20, (headY + 20) % 20);
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
        return y * 20 + x;
    }

    private void drawSnake(AddressableLEDBuffer buffer) {
        for (var i = 0; i < 400; i++) {
            buffer.setRGB(i, 0, 0, 0);
        }
        for (var i = 0; i < snake.length - 1; i++) {
            buffer.setRGB(getPointIndex(snake[i]), 255, 0, 0);
        }
        buffer.setRGB(getPointIndex(snake[snake.length - 1]), 255, 150, 150);
    }
}
