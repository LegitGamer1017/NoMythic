package com._2491nomythic.tempest.subsystems;

import com._2491nomythic.tempest.commands.drivetrain.Drive;
import com._2491nomythic.tempest.commands.drivetrain.QuadraticDrive;
import com._2491nomythic.tempest.commands.drivetrain.TwoStickDrive;
import com._2491nomythic.tempest.settings.Constants;
import com._2491nomythic.tempest.settings.Variables;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 * The system of motors, solenoids, encoders, and a gyro that allows us to drive the robot
 */
public class Drivetrain extends PIDSubsystem {
	private TalonSRX leftMaster, leftSlave, rightMaster, rightSlave;
	private AHRS gyro;
	private NetworkTable limeLight;
	private double currentPIDOutput;
	
	private static Drivetrain instance;
	
	public static Drivetrain getInstance() {
		if (instance == null) {
			instance = new Drivetrain();
		}
		return instance;
	}
	
	/**
	 * The system of motors, solenoids, encoders, and a gyroroscope that controls robot drivetrain movement
	 */
	private Drivetrain() {
		super("Drive", Variables.proportionalRotate, Variables.integralRotate, Variables.derivativeRotate);
		
		/* Instantiates NavX */
		try { 
			gyro = new AHRS(SerialPort.Port.kUSB); 	
		} 
		catch (Exception e) { 
			System.out.println("NavX instantiation failure! Check gyro USB cable"); 
			
			if (Variables.debugMode) { System.out.println(e); }
		}
		
		/* Configures Limelight */
		limeLight = NetworkTableInstance.getDefault().getTable("limelight");
		limeLight.getEntry("ledMode").setNumber(1);
		limeLight.getEntry("camMode").setNumber(1);
		
		/* Instantiates Drivetrain's Talons */
		try {
			leftMaster = new TalonSRX(Constants.driveTalonLeft1Channel);
			leftSlave = new TalonSRX(Constants.driveTalonLeft2Channel);
			rightMaster = new TalonSRX(Constants.driveTalonRight1Channel);
			rightSlave = new TalonSRX(Constants.driveTalonRight2Channel);
		} 
		catch (Exception e) {
			System.out.println("TalonSRX instantiation failure! Check CAN Bus, TalonSRX Decive ID's, and TalonSRX power");
			
			if (Variables.debugMode) { System.out.println(e); }
		}
		
		/* Sets Talon's H-bridge direction  */
		rightMaster.setInverted(true);
		rightSlave.setInverted(true);
		leftMaster.setInverted(false);
		leftSlave.setInverted(false);
		
		/* Binds slaves to masters using Talon's Follower mode */
		leftSlave.follow(leftMaster);
		rightSlave.follow(rightMaster);
		
		/* Configures Talon's Feedback Sensors */
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
		
		/* Corrects sensor direction to match throttle direction */
		leftMaster.setSensorPhase(true);
		rightMaster.setSensorPhase(true);
		
		/* Configures left and right output voltage limits */
		leftMaster.configNominalOutputForward(0, Constants.kTimeoutMs);
		leftMaster.configNominalOutputReverse(0, Constants.kTimeoutMs);
		leftMaster.configPeakOutputForward(1, Constants.kTimeoutMs);
		leftMaster.configPeakOutputReverse(-1, Constants.kTimeoutMs);
		
		rightMaster.configNominalOutputForward(0, Constants.kTimeoutMs);
		rightMaster.configNominalOutputReverse(0, Constants.kTimeoutMs);
		rightMaster.configPeakOutputForward(1, Constants.kTimeoutMs);
		rightMaster.configPeakOutputReverse(-1, Constants.kTimeoutMs);
		
		/* Configures FPID constants for Talon's Velocity mode */
		leftMaster.config_kF(Constants.kPIDLoopIdx, 0.2922857143, Constants.kTimeoutMs);
		leftMaster.config_kP(Constants.kPIDLoopIdx, 1, Constants.kTimeoutMs);
		leftMaster.config_kI(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
		leftMaster.config_kD(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
		
		rightMaster.config_kF(Constants.kPIDLoopIdx, 0.2922857143, Constants.kTimeoutMs);
		rightMaster.config_kP(Constants.kPIDLoopIdx, 1, Constants.kTimeoutMs);
		rightMaster.config_kI(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
		rightMaster.config_kD(Constants.kPIDLoopIdx, 0, Constants.kTimeoutMs);
	}
	
	/**
	 * Drives the robot forward or backward only
	 * @param speed The power fed to the vertical drive motors, ranging from -1 to 1, where negative values run the motors backwards
	 */
	public void drivePercentOutput(double speed){
		drivePercentOutput(speed, speed);
	}
	
	/**
	 * Drives the robot with each set of motors receiving an individual specific speed
	 * @param leftSpeed The power fed to the left drive motors, ranging from -1 to 1, where negative values run the motors backwards
	 * @param rightSpeed The power fed to the right drive motors, ranging from -1 to 1, where negative values run the motors backwards
	 */
	public void drivePercentOutput(double leftSpeed, double rightSpeed){
		driveLeftPercentOutput(leftSpeed);
		driveRightPercentOutput(rightSpeed);
	}
	
	/**
	 * Drives the left side of the robot
	 * @param speed The power fed to the motors, ranging from -1 to 1, where negative values run the motors backwards
	 */
	public void driveLeftPercentOutput(double speed){
		leftMaster.set(ControlMode.PercentOutput, speed * Variables.driveRestriction);
	}
	
	/**
	 * Drives the right side of the robot
	 * @param speed The power fed to the motors, ranging from -1 to 1, where negative values run the motors backwards
	 */
	public void driveRightPercentOutput(double speed){
		rightMaster.set(ControlMode.PercentOutput, speed * Variables.driveRestriction);
	}
	
	/**
	 * Drives the robot forward or backward only
	 * @param speed The speed of the wheels in inches per second
	 */
	public void driveVelocity(double speed){
		driveVelocity(speed, speed);
	}
	
	/**
	 * Drives the robot with each set of motors receiving an individual specific speed
	 * @param leftSpeed The speed of the left wheels in inches per second
	 * @param rightSpeed The speed of the right wheels in inches per second
	 */
	public void driveVelocity(double leftSpeed, double rightSpeed){
		driveLeftVelocity(leftSpeed);
		driveRightVelocity(rightSpeed);
	}
	
	/**
	 * Drives the left side of the robot
	 * @param speed The speed of the wheels in inches per second
	 */
	public void driveLeftVelocity(double speed){
		leftMaster.set(ControlMode.Velocity, speed);
	}

	
	/**
	 * Drives the right side of the robot
	 * @param speed The speed of the wheels in inches per second
	 */
	public void driveRightVelocity(double speed){
		rightMaster.set(ControlMode.Velocity, speed);
	}
	
	/**
	 * Stops all drive motion
	 */
	public void stop(){
		drivePercentOutput(0, 0);
	}
	
	/**
	 * Changes drive motors to Coast mode
	 */
	public void enableCoastMode() {
		leftMaster.setNeutralMode(NeutralMode.Coast);
		leftSlave.setNeutralMode(NeutralMode.Coast);
		rightMaster.setNeutralMode(NeutralMode.Coast);
		rightSlave.setNeutralMode(NeutralMode.Coast);
	}
	
	/**
	 * Changes drive motors to Brake mode
	 */
	public void enableBrakeMode() {
		leftMaster.setNeutralMode(NeutralMode.Brake);
		leftSlave.setNeutralMode(NeutralMode.Brake);
		rightMaster.setNeutralMode(NeutralMode.Brake);
		rightSlave.setNeutralMode(NeutralMode.Brake);
	}
	
	/**
	 * Resets the left and right encoders
	 */
	public void resetEncoders() {
		resetLeftEncoder();
		resetRightEncoder();
	}
	
	/**
	 * Resets the left drive encoder value to 0
	 */
	public void resetLeftEncoder() {
		leftMaster.setSelectedSensorPosition(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
	}
	
	/**
	 * Resets the right drive encoder value to 0
	 */
	public void resetRightEncoder() {
		rightMaster.setSelectedSensorPosition(0, Constants.kPIDLoopIdx, Constants.kTimeoutMs);
	}
	
	
	/**
	 * @return The value of the left drive encoder in inches
	 */
	public double getLeftEncoderDistance() {
		return leftMaster.getSelectedSensorPosition(0) * Constants.driveEncoderToInches;
	}
	
	//TODO Write JavaDocs
	
	public double getLeftEncoderDistanceRaw() {
		return leftMaster.getSelectedSensorPosition(0);
	}
	
	//TODO Write JavaDocs
	
	public double getRightEncoderDistanceRaw() {
		return rightMaster.getSelectedSensorPosition(0);
	}
	
	/**
	 * @return The value of the right drive encoder in inches
	 */
	public double getRightEncoderDistance() {
		return rightMaster.getSelectedSensorPosition(0) * Constants.driveEncoderToInches;
	}
	
	/**
	 * @return The average value of the two encoders in inches
	 */
	public double getDistance() {
		return (getRightEncoderDistance() + getLeftEncoderDistance()) / 2;
	}
	
	/**
	 * @return The speed of the left motor in RPS
	 */
	public double getLeftEncoderRate() {
		return leftMaster.getSelectedSensorVelocity(0) * Constants.driveEncoderVelocityToRPS;
	}
	
	/**
	 * @return The speed of the right motor in RPS
	 */
	public double getRightEncoderRate() {
		return rightMaster.getSelectedSensorVelocity(0) * Constants.driveEncoderVelocityToRPS;
	}
	
	/**
	 * 
	 * @return The average speed of both motors in RPS
	 */
	public double getEncoderRate() {
		return (getRightEncoderRate() + getLeftEncoderRate()) / 2;
	}
	
	/**
	 * @return The left driverail's velocity in NativeUnitsPer100Ms
	 */
	public double getLeftVelocityRaw() {
		return leftMaster.getSelectedSensorVelocity(Constants.kPIDLoopIdx);
	}
	
	/**
	 * @return The right driverail's velocity in NativeUnitsPer100Ms
	 */
	public double getRightVelocityRaw() {
		return rightMaster.getSelectedSensorVelocity(Constants.kPIDLoopIdx);
	}
	
	/**
	 * @return The left driverail's velocity in FeetPerSecond
	 */
	public double getLeftVelocity () {
		return rightMaster.getSelectedSensorVelocity(Constants.kPIDLoopIdx) / Constants.feetPerSecToNativeUnitsPer100Ms;
	}
	
	/**
	 * @return The right driverail's velocity in FeetPerSecond
	 */
	public double getRightVelocity () {
		return rightMaster.getSelectedSensorVelocity(Constants.kPIDLoopIdx) / Constants.feetPerSecToNativeUnitsPer100Ms;
	}
	
	/**
	 * Resets the value of the gyro to 0
	 */
	public void resetGyro() {
		gyro.reset();
	}
	
	/**
	 * @return The value of the gyro, corrected to a 0-360 range
	 */
	public double getGyroAngle(){
		return (gyro.getAngle()  % 360 + 360) % 360;
	}	
	
	/**
	 * @return The angle of the gyro, unmodified
	 */
	public double getRawGyroAngle(){
		return gyro.getAngle();
	}
	
	/**
	 * Gets the master left talon
	 * @return The master left talon
	 */
	public TalonSRX getLeftTalon() {
		return leftMaster;
	}
	
	/**
	 * Gets the master right talon
	 * @return The master right talon
	 */
	public TalonSRX getRightTalon() {
		return rightMaster;
	}
	
	/**
	 * Gets the drivetrain gyro
	 * @return The drivetrain gyro
	 */
	public AHRS getGyro() {
		return gyro;
	}
	
	/**
	 * Sets the default command of the drivetrain subsystem
	 * @param command The command to set. 1 = Drive, 2 = TwoStickDrive, 3 = QuadraticDrive
	 */
	public void chooseDefaultCommand(double command) {
		if (command == 1) {
			setDefaultCommand(new Drive());
		}
		else if (command == 3) {
			setDefaultCommand(new QuadraticDrive());
		}
		else {
			setDefaultCommand(new TwoStickDrive());
		}
	}
	
	public void initDefaultCommand() {
		
	}
	
	@Override
	public double returnPIDInput() {
		if (Variables.useGyroPID) {
			return getGyroAngle();
		}
		else {
			return getDistance();
		}
	}
	
	@Override
	protected void usePIDOutput(double output) {
		if(Variables.useGyroPID) {
			drivePercentOutput(0.8 * output, 0.8 * -output);
		}
		else {
			drivePercentOutput(output, output);
		}
	}
	
	/**
	 * Gets the value of the current PID output
	 * @return The current PID output
	 */
	public double getPIDOutput() {
		return currentPIDOutput;
	}
	
}

