package team3543.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static team3543.robot.Activity.when;
import static team3543.robot.Activity.unless;
import static team3543.robot.Activity.wrap;

/**
 * Encapsulates the telop mode robot functionality.
 * 
 * The Robot class constructs this object in teleopInit,
 * and calls loop() from teleopPeriodic().
 * 
 * @author mk
 */
public class Teleop {
	Robot robot;
	Activity.Sequence activities = Activity.all(); // an activity set that runs every time
		
	enum DriveMode { ARCADE, TANK };
	DriveMode driveMode = DriveMode.ARCADE;
	
	// Declare other properties here.
	double speedTrim = 1.0;
	
	public Teleop(Robot robot) {
		this.robot = robot;
		activities = Activity.all();
	}
	
	/**
	 * Called when this class is created by the superclass constructor.
	 * 
	 * You should put code here that initializes the main activities.
	 */
	void setup() {
		// put code run once at the start of teleop here
		// here's where you set up activities
		
		// for example, we listen to the thumb button on the right stick
		// and use it to trim the speed by 50%
		activities.add(			
			// always let the driver control manually with the sticks
			wrap(this::drive),	
			// allow the drive to trim the speed with the left thumb button
			when(robot.oi.leftJoystick.buttonPressed(OI.THUMB_BUTTON), 
					() -> {
						speedTrim = robot.calibration.SPEED_TRIM_ON;
						return false;
					}, 
					() -> {
						speedTrim = robot.calibration.SPEED_TRIM_OFF;
						return false;
					}),
			// after everything, update the dashboard
			wrap(this::updateDashboard)
		);
	}
		
	/**
	 * Code that runs every loop, just like in Arduino
	 */
	void loop() {
		// if we click the trigger on the left joystick switch drive mode
		if (robot.oi.leftJoystick.getRawButtonReleased(OI.TRIGGER_BUTTON)) {
			switchDriveMode();
		}
				
		// now do whatever else 
		// this will just call the loop() method of every registered activity
		// HOWEVER, we guard it with a button.  If the driver holds down the
		// right thumb control button. ALL teleop actions pause.
		unless(robot.oi.rightJoystick.buttonPressed(OI.THUMB_BUTTON), activities).loop();
	}
	
	void cancel() {
		robot.driveLine.stop();
	}
		
	/**
	 * Drive the robot
	 * 
	 * Delegates to tankDrive or arcadeDrive, depending on the setting of driveMode
	 */
    void drive() {
    	SmartDashboard.putString("Drive Mode", driveMode.toString());
    	if (driveMode == DriveMode.TANK) {
    		tankDrive();
    	}
    	else {
    		arcadeDrive();
    	}
    }
    
    /**
     * Implements arcade drive using the right joystick
     */
    void arcadeDrive() {
    	robot.driveLine.arcadeDrive(
    			robot.oi.rightJoystick.getY() * speedTrim * robot.oi.rightJoystick.getThrottle(), 
    			robot.oi.rightJoystick.getX(), 
    			true);
    }

    /**
     * Implements tank drive using the right joystick
     */    
    void tankDrive() {
    	robot.driveLine.tankDrive(
    			robot.oi.leftJoystick.getY() * speedTrim * robot.oi.rightJoystick.getThrottle(),
    			robot.oi.rightJoystick.getY() * speedTrim * robot.oi.rightJoystick.getThrottle(),
    			true
    	);
    }
    
    /**
     * Switch drive mode between TANK and ARCADE
     */
    void switchDriveMode() {
    	if (driveMode == DriveMode.ARCADE) {
    		driveMode = DriveMode.TANK;
    	} else {
    		driveMode = DriveMode.ARCADE;
    	}
    }
    
    /**
     * Update the dashboard.  
     */
    void updateDashboard() {
    	// Put code here to update the OI or SmartDashboard
    }

}

