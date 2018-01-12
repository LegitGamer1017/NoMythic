package com._2491nomythic.robot.settings;

/**
 * Various information needed for robot functionality that cannot be modified by the code itself
 */
public class Constants {
	
	//Controllers
	public static final int ControllerOnePort = 0;
	public static final int ControllerTwoPort = 1;
	
	//Drive
	public static final int driveTalonLeft1Channel = 11;
	public static final int driveTalonLeft2Channel = 19;
	public static final int driveTalonLeft3Channel = 14;
	public static final int driveTalonRight1Channel = 12;
	public static final int driveTalonRight2Channel = 15;
	public static final int driveTalonRight3Channel = 13;
	public static final int driveEncoderLeftChannel1 = 3;
	public static final int driveEncoderLeftChannel2 = 4;
	public static final int driveEncoderCenterChannel1 = 5;
	public static final int driveEncoderCenterChannel2 = 6;
	public static final int driveEncoderRightChannel1 = 7;
	public static final int driveEncoderRightChannel2 = 8;
		
	//Computation
	public static final double driveEncoderToFeet = 1.0 / 670 / 256; //TODO update for 2018
	
}

