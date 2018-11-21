package team3543.robot;

import team3543.robot.Robot;

/**
 * This interface shows some example 
 * @author mk
 *
 */
public class SampleActivities {

//	/**
//	 * Drive in a square.
//	 * @param robot
//	 * @param sideLength in inches
//	 * @return
//	 */
//	public static Activity driveASquare(Robot robot, double sideLength, double maxSpeed) {
//		double dt = robot.calibration.DEFAULT_DISTANCE_TOLERANCE;
//		double at = robot.calibration.DEFAULT_ANGULAR_TOLERANCE;
//		return Activity.each(
//				robot.driveLine.driveStraight(sideLength, maxSpeed, dt),
//				robot.driveLine.turnByAngle(90, maxSpeed, at),
//				robot.driveLine.driveStraight(sideLength, maxSpeed, dt),
//				robot.driveLine.turnByAngle(90, maxSpeed, at),
//				robot.driveLine.driveStraight(sideLength, maxSpeed, dt),
//				robot.driveLine.turnByAngle(90, maxSpeed, at),
//				robot.driveLine.driveStraight(sideLength, maxSpeed, dt),
//				robot.driveLine.turnByAngle(90, maxSpeed, at)
//	    );
//	}
//
//	/**
//	 * Turn right around
//	 *
//	 * @param robot
//	 * @param sideLength in inches
//	 * @return
//	 */
//	public static Activity turnAround(Robot robot, double maxSpeed) {
//		return robot.driveLine.turnByAngle(180, maxSpeed, robot.calibration.DEFAULT_ANGULAR_TOLERANCE);
//	}
//
//	public static Activity someComplexTask(Robot robot) {
//
//		return Activity.each(
//				//robot.vision.acquireGearDrop(),
//				//robot.driveLine.driveStraight(robot.vision.distanceToGearDrop),
//				//robot.elbow.extend(),
//				//robot.wrist.open()
//		);
//	}
}
