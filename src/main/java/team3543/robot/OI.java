package team3543.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the actions of the robot.
 *
 * Anything that connects a physical operator to robot actions should be
 * described in this class.
 *
 */
public class OI {

	final Robot robot;    // we need a reference to the robot

	DriveLine.DriveMode driveMode = DriveLine.DriveMode.ARCADE;

	public static final String SCRIPT_CHOOSER = "Playback RobotScript";

	public static final int DEFAULT_LEFT_JOYSTICK_PORT	= 0;
	public static final int DEFAULT_RIGHT_JOYSTICK_PORT	= 1;

	public static final int TRIGGER_BUTTON				= 1;
	public static final int THUMB_BUTTON				= 2;

    public JoystickButton switchDriveMode;
    public Joystick leftJoystick;
    public Joystick rightJoystick;

//    final NetworkTable networkTable;

	Recordings.RecordingChooser recordingChooser;

    public OI(Robot robot) {
    	this.robot = robot;
//    	networkTable = NetworkTableInstance.getDefault().getTable(Recordings.NETWORK_TABLE);
    	initJoysticks(DEFAULT_LEFT_JOYSTICK_PORT, DEFAULT_RIGHT_JOYSTICK_PORT);
    	recordingChooser = Recordings.chooser();
    }

	/**
	 * Called during robotInit.  Put setup code for the OI in here.
	 */
    public void configure() {
		// here, we want to create a chooser for autonomous mode
		SmartDashboard.putData(SCRIPT_CHOOSER, Recordings.chooser());

		// Let's also listen for a change on the RECORD_LOAD_CHANNEL, so we can load
        // the playback script from elsewhere
//        networkTable.addEntryListener(RECORD_LOAD_CHANNEL, (table, key, entry, value, flags) -> {
//            Robot.LOG.info("Loading new script");
//            robot.recorder.setScript(RobotScript.fromJSON(value.getString()));
//        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    }

    void initJoysticks(int left, int right) {
        rightJoystick = new Joystick(right);
        leftJoystick = new Joystick(left);
    }

	/**
	 * Code that runs every loop, just like in Arduino.  Read the inputs from the operator
	 * interface and then control the bot.
	 */
	void loop() {

		// IF we push the right joystick thumb button, STOP all teleop
		if (rightJoystick.getTriggerPressed()) {
			robot.stopAll();
			// and don't do anything else!
			return;
		}

		// RECORD
		controlRecord();

		// PLAYBACK
		controlPlayback();

		// If the robot is NOT playing back a recording, do the other stuff based on inputs
		if (robot.recorder.playingBack) {
			robot.recorder.playback();
		}
		else {
			// TODO - add other controls here, for example:
			// controlClaw()
			// drive the bot manually
			drive();
		}
	}

	/**
	 * Controls the recording functions.
	 */
	void controlRecord() {
		// If we press the reset button reset recording
		if (leftJoystick.getRawButtonReleased(Config.RESET_RECORD_BUTTON)) {
			robot.recorder.stopRecording();
			robot.recorder.resetRecording();
			SmartDashboard.putString("Record", "Reset");
		}
		// if we press the record button, ensure we are recoeding
		if (leftJoystick.getRawButtonPressed(Config.RECORD_BUTTON)) {
			robot.recorder.startRecording();
			SmartDashboard.putString("Record", "Active");
		}
		// if we release the record button, pause recording
		else if (leftJoystick.getRawButtonReleased(Config.RECORD_BUTTON)) {
			robot.recorder.stopRecording();
			SmartDashboard.putString("Record", "Stopped");
			robot.recorder.dumpRecording(); // will write it to the console
            // TODO - will this be too much data for a String field?  Can we make the text box big?
			SmartDashboard.putString(Recordings.RECORD_SAVE_CHANNEL, robot.recorder.getScript().toJSON());
		}
	}

	/**
	 * Controls driveline mode between arcade and tank using the left joystick trigger
	 */
	void controlDriveMode() {
		// if we click the trigger on the left joystick switch drive mode
		if (leftJoystick.getTriggerReleased()) {
			switchDriveMode();
		}
		SmartDashboard.putString("Drive Mode", driveMode.toString());
	}

	/**
	 * Controls driveline shifter using the shift up/down buttons on the left stick
	 */
	void controlShifter() {
		// if we click on the shifter buttons on the left stick, shift up/down
		if (leftJoystick.getRawButtonPressed(Config.SHIFT_HIGH_BUTTON)) {
			robot.driveLine.shiftHigh();
		}
		else if (leftJoystick.getRawButtonPressed(Config.SHIFT_LOW_BUTTON)) {
			robot.driveLine.shiftLow();
		}
		SmartDashboard.putString("Shift Mode", robot.driveLine.state.shiftMode.toString());
	}

	/**
	 * Control the playback functions of the record/playback interface.
	 */
	void controlPlayback() {
		// If we select a new playback script on the smart dashboard, stop playback and
		// load it into the bot

		// If we select the playback button, load the selected script and start playback
		if (leftJoystick.getRawButtonPressed(Config.RESET_PLAYBACK_BUTTON)) {
			robot.recorder.stopPlayback();
			robot.recorder.resetPlayback();
			SmartDashboard.putString("Playback", "Reset");
		}
		// if we hit the load playback button, load it
		if (leftJoystick.getRawButtonReleased(Config.LOAD_PLAYBACK_BUTTON)) {
			robot.recorder.setScript(RobotScript.EMPTY);
			RobotScript.ScriptSource scriptSource = recordingChooser.getSelected();
			if (scriptSource != null) {
				robot.recorder.setScript(scriptSource.getScript());
			}
		}
		if (leftJoystick.getRawButtonPressed(Config.PLAYBACK_BUTTON)) {
			robot.recorder.startPlayback();
			SmartDashboard.putString("Playback", "Active");
		}
		else if (leftJoystick.getRawButtonReleased(Config.PLAYBACK_BUTTON)) {
			robot.recorder.stopPlayback();
			SmartDashboard.putString("Playback", "Stopped");
		}
	}

	/**
	 * Drive the robot
	 *
	 * Delegates to tankDrive or arcadeDrive, depending on the setting of driveMode
	 */
	void drive() {
		// control the drive mode between arcade and tank
		controlDriveMode();

		// control the shifter between low and high
		controlShifter();

		if (driveMode == DriveLine.DriveMode.TANK) {
			tankDrive();
		}
		else {
			arcadeDrive();
		}
	}

	/**
	 * Arcade drive using the right joystick and squaredInputs mode
	 */
	void arcadeDrive() {
		robot.driveLine.arcadeDrive(rightJoystick.getY(), rightJoystick.getX(), true);
	}

	/**
	 * Tank drive using the left and right joysticks and squaredInputs mode
	 */
	void tankDrive() {
		robot.driveLine.tankDrive(leftJoystick.getY(), rightJoystick.getY(), true);
	}

	/**
	 * Switch drive mode between TANK and ARCADE
	 */
	void switchDriveMode() {
		if (driveMode == DriveLine.DriveMode.ARCADE) {
			driveMode = DriveLine.DriveMode.TANK;
		} else {
			driveMode = DriveLine.DriveMode.ARCADE;
		}
	}

	/**
	 * Get the RobotScript to run for the autonomous mode part of the game.
	 *
	 * It should be the one selected in the chooser.  Note if the game rules
	 * suggest some dynamic choice is required (like in 2018) we will need to
	 * change this.
	 */
	public RobotScript getAutonomousScript() {
		// fetch the selected autonomous script
		RobotScript.ScriptSource ss = recordingChooser.getSelected();
		if (ss == null) {
			return RobotScript.EMPTY;
		} else {
			return ss.getScript();
		}
	}
}

