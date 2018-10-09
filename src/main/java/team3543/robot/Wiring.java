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
final public class Wiring {

	// DriveLine
	final int DRIVELINE_LEFT_FRONT_MOTOR_PORT 		= 0;
	final int DRIVELINE_LEFT_REAR_MOTOR_PORT 		= 1;
	final int DRIVELINE_RIGHT_FRONT_MOTOR_PORT 		= 2;
	final int DRIVELINE_RIGHT_REAR_MOTOR_PORT 		= 3;
	final int DRIVELINE_GYRO_PORT					= 0;	
	final int DRIVELINE_LEFT_ENCODER_PORT_1			= 0;	
	final int DRIVELINE_LEFT_ENCODER_PORT_2			= 1;	
	final int DRIVELINE_RIGHT_ENCODER_PORT_1		= 2;	
	final int DRIVELINE_RIGHT_ENCODER_PORT_2		= 3;	
	
	// Claw
	
	final int CLAW_COMPRESSOR_PORT = 5;
	final int CLAW_SOLENOID_PORT_1 = 6;
	final int CLAW_SOLENOID_PORT_2 = 7;
	
}
