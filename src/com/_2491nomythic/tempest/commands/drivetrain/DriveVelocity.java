package com._2491nomythic.tempest.commands.drivetrain;

import com._2491nomythic.tempest.commands.CommandBase;
import com._2491nomythic.tempest.settings.Constants;
import com._2491nomythic.tempest.settings.ControllerMap;
import com._2491nomythic.tempest.settings.Variables;

/**
 * Drives the robot with actual linear acceleration as according to input from a driver's controller
 */
public class DriveVelocity extends CommandBase {
	private double turnSpeed, currentLeftSpeed, currentRightSpeed, lastLeftSpeed, lastRightSpeed, velocityAccelerationSpeed;
	
	/**
	 * Drives the robot with actual linear acceleration as according to input from a driver's controller
	 */
	public DriveVelocity() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(drivetrain);
		velocityAccelerationSpeed = Variables.accelerationSpeed * Constants.driveMaxSpeedRPS;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		turnSpeed = Constants.driveMaxSpeedRPS * 0.5 * oi.getAxisDeadzonedSquared(ControllerMap.driveController, ControllerMap.driveTurnAxis, 0.1);
		
		lastLeftSpeed = currentLeftSpeed;
		lastRightSpeed = currentRightSpeed;
		
		currentLeftSpeed = Constants.driveMaxSpeedRPS * -oi.getAxisDeadzonedSquared(ControllerMap.driveController, ControllerMap.driveMainAxis, 0.05) + turnSpeed;
		currentRightSpeed = Constants.driveMaxSpeedRPS * -oi.getAxisDeadzonedSquared(ControllerMap.driveController, ControllerMap.driveMainAxis, 0.05) - turnSpeed;
		
		if (Variables.useLinearAcceleration) {
			double leftAcceleration = (currentLeftSpeed - lastLeftSpeed);
			double signOfLeftAcceleration = leftAcceleration / Math.abs(leftAcceleration);
			if (Math.abs(leftAcceleration) > velocityAccelerationSpeed) { // otherwise the power is below accel and is fine
				if (Math.abs(currentLeftSpeed) - Math.abs(lastLeftSpeed) > 0) {
					//System.out.println(currentLeftSpeed + " was too high, setting to " + (lastLeftSpeed + (velocityAccelerationSpeed * signOfLeftAcceleration)));
					currentLeftSpeed = lastLeftSpeed + (velocityAccelerationSpeed * signOfLeftAcceleration);
					
				}
				// if the difference between the numbers is positive it is going up
			}
			double rightAcceleration = (currentRightSpeed - lastRightSpeed);
			double signOfRightAcceleration = rightAcceleration / Math.abs(rightAcceleration);
			if (Math.abs(rightAcceleration) > velocityAccelerationSpeed) { // otherwise the power is below 0.05 accel and is fine
				if (Math.abs(currentRightSpeed) - Math.abs(lastRightSpeed) > 0) {
					//System.out.println(currentRightSpeed + " was too high, setting to " + (lastRightSpeed + (velocityAccelerationSpeed * signOfRightAcceleration)));
					currentRightSpeed = lastRightSpeed + (velocityAccelerationSpeed * signOfRightAcceleration);
				}
				// if the difference between the numbers is positive it is going up
			}
		}
		
		
		drivetrain.driveVelocity(currentLeftSpeed + turnSpeed, currentRightSpeed - turnSpeed);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		drivetrain.stop();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}
