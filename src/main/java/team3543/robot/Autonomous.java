package team3543.robot;

import static team3543.robot.Activity.*;

public class Autonomous {
	Robot robot;
	Activity.Sequence activities = Activity.all();
	
	// Declare other properties here.	
	
	/**
	 * Constructor
	 * 
	 * Just calls the superclass constructor.  Put code here that initializes 
	 * variables in the object.
	 * 
	 * @param robot
	 */
	public Autonomous(Robot robot) {		
		this.robot = robot;
	}

	/**
	 * Called once by the Robot.RunMode constructor when the instance is created
	 * 
	 * You should put code here that initializes the main activities
	 */
	void setup() {
		activities.push(
			// List your activities here, in the order they should run.
			// Each activity listed will run each loop	
			wrap(this::updateDashboard)
		);
	}
	
	/**
	 * Called every tick while autonomous mode is running
	 * 
	 */
	void loop() {
		// just loop on the activities
		activities.loop();
		updateDashboard();
	}
	
	/**
	 * Called once when autonomous mode ends
	 */
	void cancel() {
		
	}
	
	/**
	 * Update the dashboard
	 */
	void updateDashboard() {
		// add code here to write to the dashboard, etc.
	}
}
