// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Launcher extends SubsystemBase {
  /** Creates a new Launcher. */
  private CANSparkMax upper = new CANSparkMax(Constants.LaunchConstants.upperCANID, MotorType.kBrushless);

  private CANSparkMax lower = new CANSparkMax(Constants.LaunchConstants.lowerCANID, MotorType.kBrushless);

  private boolean tuningPIDS = false;

  private DoubleSolenoid angleSwitcher = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.LaunchConstants.angleSwitchForwardChannel, Constants.LaunchConstants.angleSwitchReverseChannel);

  public Launcher() {
    upper.getPIDController().setP(Constants.LaunchConstants.launcherP);
    upper.getPIDController().setI(Constants.LaunchConstants.launcherI);
    upper.getPIDController().setD(Constants.LaunchConstants.launcherD);

    lower.getPIDController().setP(Constants.LaunchConstants.launcherP);
    lower.getPIDController().setI(Constants.LaunchConstants.launcherI);
    lower.getPIDController().setD(Constants.LaunchConstants.launcherD);

    SmartDashboard.putNumber("Launch P", upper.getPIDController().getP());
    SmartDashboard.putNumber("Launch I", upper.getPIDController().getI());
    SmartDashboard.putNumber("Launch D", upper.getPIDController().getD());
  }

  public void setLaunchVelocity(double velocity) {
    upper.getPIDController().setReference(velocity, ControlType.kSmartVelocity);
    lower.getPIDController().setReference(velocity, ControlType.kSmartVelocity);
  }

  public Value getAnglePosition() {
    return angleSwitcher.get();
  }

  public void setAngle(Value value) {
    angleSwitcher.set(value);
  }

  public void switchAngle() {
    boolean switcherForward = (angleSwitcher.get() == Value.kForward);
    angleSwitcher.set(switcherForward ? Value.kReverse : Value.kForward);
  }

  public void togglePIDTuning() {
    tuningPIDS = !tuningPIDS;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if (tuningPIDS) {
      upper.getPIDController().setP(SmartDashboard.getNumber("Launch P", 0));
      upper.getPIDController().setI(SmartDashboard.getNumber("Launch I", 0));
      upper.getPIDController().setD(SmartDashboard.getNumber("Launch D", 0));

      lower.getPIDController().setP(SmartDashboard.getNumber("Launch P", 0));
      lower.getPIDController().setI(SmartDashboard.getNumber("Launch I", 0));
      lower.getPIDController().setD(SmartDashboard.getNumber("Launch D", 0));
    }
  }
}