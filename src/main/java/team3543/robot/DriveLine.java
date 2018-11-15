// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.
package team3543.robot;

import team3543.robot.Constants;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import static team3543.robot.Utils.arrGet;
import static team3543.robot.Utils.asBool;

import java.io.Serializable;

/**
 * Drive line subsystem
 *
 * Contains the left and right wheels, a gyro and a differential drive.
 *
 * These are grouped into a subsystem because all the sensors and actuators
 * work together.
 *
 */
public class DriveLine extends Subsystem implements IActuate {

    Robot robot;

    /////// This stores the state.  All the motive routines write to this, then
    // the actuate() method is called by the Robot at end-of-loop, and we "really"
    // update the outputs.  This so we can record and playback
    State state = new State();

	//////// Sensors
    private AnalogGyro gyro = null;
    private Encoder leftWheelEncoder = null;
    private Encoder rightWheelEncoder = null;

	//////// Actuators
    private SpeedController leftWheels = null;
    private SpeedController rightWheels = null;

    /////// Drive
    private DifferentialDrive differentialDrive = null;

    ////// Other variables
    private double trimDistance = 12.0;
    private double trimAngle 	= 30.0;
    private double gyroGain		= 1/90;

    public DriveLine(Robot robot) {
    	super("DriveLine");

        String name = getName();

        WPI_TalonSRX leftFrontTalon = new WPI_TalonSRX(robot.wiring.DRIVELINE_LEFT_FRONT_MOTOR_PORT);
        WPI_TalonSRX leftRearTalon = new WPI_TalonSRX(robot.wiring.DRIVELINE_LEFT_REAR_MOTOR_PORT);
        WPI_TalonSRX rightFrontTalon = new WPI_TalonSRX(robot.wiring.DRIVELINE_RIGHT_FRONT_MOTOR_PORT);
        WPI_TalonSRX rightRearTalon = new WPI_TalonSRX(robot.wiring.DRIVELINE_RIGHT_REAR_MOTOR_PORT);

        leftFrontTalon.setNeutralMode(NeutralMode.Brake);
        rightFrontTalon.setNeutralMode(NeutralMode.Brake);

        // right front is inverted
        rightFrontTalon.setInverted(true);

        leftRearTalon.follow(leftFrontTalon);
        rightRearTalon.follow(rightFrontTalon);

        leftWheels = leftFrontTalon;
        rightWheels = rightFrontTalon;

        differentialDrive = new DifferentialDrive(leftWheels, rightWheels);
        differentialDrive.setSafetyEnabled(true);
        differentialDrive.setSubsystem(name);

        LiveWindow.add(differentialDrive);

        LiveWindow.add(leftFrontTalon);
        LiveWindow.add(leftRearTalon);
        LiveWindow.add(rightFrontTalon);
        LiveWindow.add(rightRearTalon);

        // Initialize sensors
        gyro = new AnalogGyro(robot.wiring.DRIVELINE_GYRO_PORT);
        gyro.setSubsystem(name);
        gyro.setSensitivity(robot.calibration.DRIVELINE_GYRO_SENSITIVITY);

        leftWheelEncoder = new Encoder(robot.wiring.DRIVELINE_LEFT_ENCODER_PORT_1, robot.wiring.DRIVELINE_RIGHT_ENCODER_PORT_2, false, EncodingType.k4X);
        rightWheelEncoder = new Encoder(robot.wiring.DRIVELINE_RIGHT_ENCODER_PORT_1, robot.wiring.DRIVELINE_RIGHT_ENCODER_PORT_2, false, EncodingType.k4X);

        leftWheelEncoder.setDistancePerPulse(robot.calibration.DRIVELINE_ENCODER_DPP);
        rightWheelEncoder.setDistancePerPulse(robot.calibration.DRIVELINE_ENCODER_DPP);

        leftWheelEncoder.setPIDSourceType(PIDSourceType.kRate);
        rightWheelEncoder.setPIDSourceType(PIDSourceType.kRate);

        leftWheelEncoder.setSubsystem(name);
        rightWheelEncoder.setSubsystem(name);

        LiveWindow.add(gyro);
        LiveWindow.add(leftWheelEncoder);
        LiveWindow.add(rightWheelEncoder);

        trimDistance = robot.calibration.DRIVELINE_TRIM_DISTANCE;
        trimAngle = robot.calibration.DRIVELINE_TRIM_ANGLE;
        gyroGain = robot.calibration.DRIVELINE_GYRO_GAIN;

        this.robot = robot;
    }

    @Override
    public void initDefaultCommand() {

    }

    @Override
    public void periodic() {
        // Put code here to be run every loop
    	// blank on purpose
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void resetEncoders() {
    	leftWheelEncoder.reset();
    	rightWheelEncoder.reset();
    }

    public void resetGyro() {
    	gyro.reset();
    }

    /**
     * Gyro angle in degrees, relative to last resetGyro()
     *
     * @see resetGyro()
     * @return angle in degrees
     */
    public double getGyroAngle() {
    	return gyro.getAngle();
    }

    /**
     * Get the left wheel encoder distance, since the last reset
     *
     * @return
     */
    public double getLeftEncoderValue() {
    	return leftWheelEncoder.getDistance();
    }

    /**
     * Get the right wheel encoder distance, since the last reset
     *
     * @return
     */
    public double getRightEncoderValue() {
    	return rightWheelEncoder.getDistance();
    }

    /**
     * Get the distance traveled since last reset, based on average of left and right encoder value
     *
     * @return
     */
    public double getDistanceTraveled() {
    	return (getLeftEncoderValue() + getRightEncoderValue()) / 2;
    }

    public void resetAll() {
    	resetGyro();
    	resetEncoders();
    }

    public void calibrate() {
    	gyro.calibrate();
    	resetAll();
    }

    public void shiftHigh() {
        state.shiftMode = ShiftMode.HIGH;
    }

    public void shiftLow() {
        state.shiftMode = ShiftMode.LOW;
    }

    public void arcadeDrive(double magnitude, double curve, boolean squaredInputs) {
        state.driveMode = DriveMode.ARCADE;
        state.magnitudeOrLeft = magnitude;
        state.curveOrRight = curve;
        state.squaredInputs = squaredInputs;
    }

    public void tankDrive(double left, double right, boolean squaredInputs) {
        state.driveMode = DriveMode.TANK;
        state.magnitudeOrLeft = left;
        state.curveOrRight = right;
        state.squaredInputs = squaredInputs;
    }

    public void stop() {
    	differentialDrive.tankDrive(0, 0);
    }

    public void turn(Constants.RotationDirection direction, double speed) {
    	speed *= direction == Constants.RotationDirection.COUNTERCLOCKWISE ? -1 : 1;
    	tankDrive(speed, -speed, false);
    }

    public Activity driveStraight(final double distance, final double maxSpeed, final double tolerance) {
    	final double initialHeading = getGyroAngle();
    	final double initialDistance = getDistanceTraveled();
    	final double trimSlope = maxSpeed / trimDistance;

    	return Activity.from(() ->{
			double angleDifference = getGyroAngle() - initialHeading;
			double distanceDelta = getDistanceTraveled() - initialDistance;
			double distanceError = distance - distanceDelta;

			// we're done if we're at the setpoint
			if (Math.abs(distanceError) < tolerance) return true;

			// otherwise let's drive towards it, correcting for angle error
			// see http://wpilib.screenstepslive.com/s/3120/m/7912/l/85772-gyros-to-control-robot-driving-direction
			double rot = -angleDifference * gyroGain;
			rot = Utils.clip(rot, -1, 1);// between -1 and 1
			arcadeDrive(Utils.clip(distanceError * trimSlope, -maxSpeed, maxSpeed), rot, false);

			return false;
    	});
    }

    public Activity turnByAngle(final double angleInDegrees, final double maxSpeed, final double tolerance) {
    	final double initialAngle = getGyroAngle();
    	final double slope = maxSpeed / trimAngle;

    	return Activity.from(() -> {
			double angleError = getGyroAngle() - initialAngle;

			// we're done if we're at the setpoint
			if (Math.abs(angleError) < tolerance) return true;

			// otherwise let's turn towards the angle
			// see http://wpilib.screenstepslive.com/s/3120/m/7912/l/85772-gyros-to-control-robot-driving-direction
			double speed = Utils.clip(-angleError * slope, -maxSpeed, maxSpeed);

			tankDrive(speed, -speed, false);
			return false;
    	});
    }

    public void actuate() {
        // if shifting, shift
        if (state.shiftMode == ShiftMode.HIGH) {
            // ensure shift is high
        }
        else {
            // ensure shift is low
        }
        // now drive based on mode
        if (state.driveMode == DriveMode.ARCADE) {
            differentialDrive.arcadeDrive(state.magnitudeOrLeft, state.curveOrRight, state.squaredInputs);
        }
        else {
            differentialDrive.tankDrive(state.magnitudeOrLeft, state.curveOrRight, state.squaredInputs);
        }
    }

    public static enum ShiftMode { HIGH, LOW };
    public static enum DriveMode { TANK, ARCADE };

    /**
     * This is the state of the subsystem
     */
    public static class State {

        ShiftMode shiftMode = ShiftMode.HIGH;
        DriveMode driveMode = DriveMode.ARCADE;
        double magnitudeOrLeft = 0.0;
        double curveOrRight = 0.0;
        boolean squaredInputs = false;

        public State() {

        }

        public State(ShiftMode sm, DriveMode dm, double leftOrMag, double rightOrCurve, boolean sq) {
            this();
            this.shiftMode = sm;
            this.driveMode = dm;
            this.magnitudeOrLeft = leftOrMag;
            this.curveOrRight = rightOrCurve;
            this.squaredInputs = sq;
        }

        public State copy() {
            return new State(this.shiftMode, this.driveMode, this.magnitudeOrLeft, this.curveOrRight, this.squaredInputs);
        }
    }
}

