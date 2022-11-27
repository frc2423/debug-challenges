// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.led.Dark;
import frc.robot.led.LedController;
import frc.robot.led.Snake;

public class Robot extends TimedRobot {

  private LedController ledController = new LedController(400);
 
  @Override
  public void robotInit() {
    ledController.add("dark", new Dark()); 
    ledController.add("snake", new Snake());
  }

  @Override
  public void robotPeriodic() {
    ledController.run();
  }

  @Override
  public void teleopInit() {
    ledController.set("snake");
  }
  @Override
  public void disabledInit() {
    ledController.set("dark");
  }
}
