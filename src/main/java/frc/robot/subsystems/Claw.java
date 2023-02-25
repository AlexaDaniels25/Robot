// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxLimitSwitch;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RealConstants;

public class Claw extends SubsystemBase {
  private CANSparkMax clawMotor = new CANSparkMax(8, MotorType.kBrushless);

  private SparkMaxLimitSwitch forewardLimit =
      clawMotor.getForwardLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);
  private SparkMaxLimitSwitch reverseLimit =
      clawMotor.getReverseLimitSwitch(SparkMaxLimitSwitch.Type.kNormallyOpen);

  private boolean isClosed;
  private boolean isOpen;

  private PIDController holdPID =
      new PIDController(RealConstants.kClawP, RealConstants.kClawI, RealConstants.kClawD);

  private RelativeEncoder clawEnc = clawMotor.getEncoder();

  public Claw() {
    forewardLimit.enableLimitSwitch(true);
    reverseLimit.enableLimitSwitch(true);

    isClosed = forewardLimit.isPressed();
    isOpen = reverseLimit.isPressed();

    clawMotor.setIdleMode(IdleMode.kCoast);

    clawEnc.setPositionConversionFactor(RealConstants.clawConversionFactor);
    clawEnc.setPosition(25);

    clawMotor.setSoftLimit(SoftLimitDirection.kForward, RealConstants.clawForwardLimit);
    clawMotor.setSoftLimit(SoftLimitDirection.kReverse, RealConstants.clawReverseLimit);
  }

  public boolean queryClosed() {
    return isClosed;
  }

  public boolean queryOpen() {
    return isOpen;
  }

  public void setMotor(double speed) {
    double position = clawEnc.getPosition();
    if(position <= -RealConstants.clawForwardLimit && speed < 0){
      clawMotor.set(0);
    }
    else if(position >= -RealConstants.clawReverseLimit && speed > 0){
      clawMotor.set(0);
    }
    else{
    clawMotor.set(speed * RealConstants.clawSpeed);
    }
  }

  public void setMotorReverse() {
    clawMotor.set(-RealConstants.clawSpeed);
  }

  public void stopMotor() {
    clawMotor.set(0);
  }

  /*public void hold() {
    clawMotor.set(holdPID.calculate(clawEnc.getVelocity(), 0));
  }*/

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    isClosed = forewardLimit.isPressed();
    isOpen = reverseLimit.isPressed();
    
    SmartDashboard.putNumber("Claw Position", clawEnc.getPosition());
    SmartDashboard.putNumber("Claw Velocity", clawEnc.getVelocity());
    SmartDashboard.putNumber("Claw Hold PID", holdPID.calculate(clawEnc.getVelocity(), 0));
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
