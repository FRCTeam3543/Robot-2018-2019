package team3543.robot;

/**
 * Class to hold calibration constants, so they are easy to track down and tune.
 * 
 * Notes: the "final" keyword makes these properties read-only and the class non-extendable
 * 
 * @author mk
 *
 */
final public class Calibration {	
	
	final double SPEED_TRIM_OFF					= 1.0; 		// multiply by this when speed trim is off
	final double SPEED_TRIM_ON					= 0.5; 		// multiply by this when speed trim is on

	final double DEFAULT_ANGULAR_TOLERANCE 		= 0.5; 		// degrees
	final double DEFAULT_DISTANCE_TOLERANCE 	= 0.015; 	// m
	
	final double DRIVELINE_GYRO_SENSITIVITY 	= 0.007;
	final double DRIVELINE_ENCODER_DPP			= 0.00002049; // m per pulse	
	final double DRIVELINE_GYRO_GAIN			= 1/90;		// feedback gain speed/degrees	
	final double DRIVELINE_TRIM_DISTANCE		= 0.3;			// m, distance where we start trimming speed on automated approach
	final double DRIVELINE_TRIM_ANGLE			= 30;		// degrees, angle where we start trimming speed on automated turn
	
}
