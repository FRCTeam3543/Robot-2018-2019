package team3543.robot;

/**
 * Specify the robot ports here, so they are easy to track down and change
 *
 * Note: the "final" keyword in front of the properties makes it read-only. In front of the class
 * makes it not extensible.
 *
 * @author mk
 *
 */
final public class Config {

	//////////////////// OPERATOR INTERFACE //////
	public static final int SHIFT_HIGH_BUTTON			= 5;		// FIXME - set SHIFT_HIGH_BUTTON
	public static final int SHIFT_LOW_BUTTON			= 6;		// FIXME - set SHIFT_LOW_BUTTON
	public static final int SWITCH_DRIVE_MODE_BUTTON	= 1;		// TRIGGER button on left stick
	public static final int RESET_PLAYBACK_BUTTON		= 9;		// BUTTON on left stick, hold down to play back
	public static final int LOAD_PLAYBACK_BUTTON		= 11;		// BUTTON on left stick, press to load from the dashboard list
	public static final int PLAYBACK_BUTTON				= 10;		// BUTTON on left stick, hold down to play back
	public static final int RECORD_BUTTON				= 7;		// BUTTON on left stick, hold down to record
	public static final int RESET_RECORD_BUTTON			= 8;		// BUTTON on left stick, click to reset the recording

	//////////////////// WIRING //////////////////
	//
	// DriveLine
	public static final int DRIVELINE_LEFT_FRONT_MOTOR_PORT 		= 0;
	public static final int DRIVELINE_LEFT_REAR_MOTOR_PORT 		= 1;
	public static final int DRIVELINE_RIGHT_FRONT_MOTOR_PORT 		= 2;
	public static final int DRIVELINE_RIGHT_REAR_MOTOR_PORT 		= 3;
	public static final int DRIVELINE_GYRO_PORT					= 0;
	public static final int DRIVELINE_LEFT_ENCODER_PORT_1			= 0;
	public static final int DRIVELINE_LEFT_ENCODER_PORT_2			= 1;
	public static final int DRIVELINE_RIGHT_ENCODER_PORT_1		= 2;
	public static final int DRIVELINE_RIGHT_ENCODER_PORT_2		= 3;

	public static final int COMPRESSOR_PORT = 5;
	public static final int DRIVELINE_SOLENOID_PORT_1 = 6;
	public static final int DRIVELINE_SOLENOID_PORT_2 = 7;

	//////////////////// CALIBRATION ///////////////
	//

	public static final double SPEED_TRIM_OFF					= 1.0; 		// multiply by this when speed trim is off
	public static final double SPEED_TRIM_ON					= 0.5; 		// multiply by this when speed trim is on

	public static final double DEFAULT_ANGULAR_TOLERANCE 		= 0.5; 		// degrees
	public static final double DEFAULT_DISTANCE_TOLERANCE 	= 0.015; 	// m

	public static final double DRIVELINE_GYRO_SENSITIVITY 	= 0.007;
	public static final double DRIVELINE_ENCODER_DPP			= 0.00002049; // m per pulse
	public static final double DRIVELINE_GYRO_GAIN			= 1/90;		// feedback gain speed/degrees
	public static final double DRIVELINE_TRIM_DISTANCE		= 0.3;			// m, distance where we start trimming speed on automated approach
	public static final double DRIVELINE_TRIM_ANGLE			= 30;		// degrees, angle where we start trimming speed on automated turn

	//////////////////// GEOMETRY //////////////////////////
	//

	/////////////////////////////////////////////////////////
	//////////////// Bot geometry
	public static final double WHEEL_RADIUS					= Geometry.in_to_m(6);						// m
	////////// Add geometry methods for the bot here (that are computed relative to component positions)

	/////////////////////////////////////////////////////////
	//////////////// Field geometry, from 2018 Game Manual
	// Should all be public static final double
	public static final double FIELD_TAPE_WIDTH				= Geometry.in_to_m(2);		// m
	public static final double FIELD_AUTO_LINE_DISTANCE		= 3.05;				// m
	public static final double FIELD_LENGTH 				= 16.46; 			// m
	public static final double FIELD_WIDTH 					= 8.23;	 			// m
	public static final double FIELD_CENTERLINE				= FIELD_LENGTH / 2;	// m

	public static final double FIELD_NULL_TERRITORY_LENGTH 	= 1.83; 			// m
	public static final double FIELD_NULL_TERRITORY_WIDTH 	= 2.42;				// m
}
