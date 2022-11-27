package frc.robot.led;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public interface Led {
    default void start(AddressableLEDBuffer buffer, int length) {};
    default void run(AddressableLEDBuffer buffer, int length) {};
    default void end(AddressableLEDBuffer buffer, int length) {};
}
