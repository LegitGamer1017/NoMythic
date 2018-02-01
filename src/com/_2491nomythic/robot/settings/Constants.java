package com._2491nomythic.robot.settings;

/**
 * Various information needed for robot functionality that cannot be modified by the code itself
 */
public class Constants {
	
	//Drive
	public static final int driveTalonLeft1Channel = 1;
	public static final int driveTalonLeft2Channel = 2;
	public static final int driveTalonRight1Channel = 3;
	public static final int driveTalonRight2Channel = 4;
	
	//Intake
	public static final int intakeTalonLeft1Channel = 5;
	public static final int intakeTalonLeft2Channel = 6;
	public static final int intakeTalonRight1Channel = 7;
	public static final int intakeTalonRight2Channel = 8;
	public static final int intakeTalonBottomChannel = 9;
	public static final int intakeSolenoidActivateChannel = 2491; //TODO Change this to an actual solenoid channel.
	public static final int intakeSolenoidOpenChannel = 2491; //TODO Change this to an actual solenoid channel.

	//Shooter
	public static final int shooterTalonLeftAccelerateChannel = 12;
	public static final int shooterTalonLeftShootChannel = 14;
	public static final int shooterTalonRightAccelerateChannel = 13;
	public static final int shooterTalonRightShootChannel = 15;
	public static final int shooterElevatorChannel = 12;
	public static final int timeForShooterToSpinUp = 2;
	public static final int timeForShooterToRaise = 4;
	public static final double shooterHighScaleSpeed = 1; //TODO Find an actual value for this
	public static final double shooterMediumScaleSpeed = .8; //TODO Find an actual value for this
	public static final double shooterLowScaleSpeed = .6; //TODO Find an actual value for this
	public static final double shooterSwitchSpeed = .3; //TODO Find an actual value for this
	
	//Lights
	public static final int lightIC2Channel = 1;
	
	//CubeStorage
	public static final int cubeStorageTalonLeftChannel = 10;
	public static final int cubeStorageTalonRightChannel = 11;
		
	//Computation
	public static final double driveEncoderToInches = 1.066 * 2.4 * 1200.0 / 670 / 256; //TODO update for 2018. Currently in 2017 inches.
	public static final double encoderTicksToRPM = 2491;//TODO update with an actual value.
}