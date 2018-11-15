package team3543.robot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;


import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import team3543.robot.DriveLine;

//import team3543.base.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in
 * the project.
 */
public class Robot extends TimedRobot {
	public static Logger LOG = Logger.getLogger("Robot");

	/////////////// Variables //////////////
	// SendableChooser<Command> chooser = new SendableChooser<>();

    OI oi;    					// Operator Interface
    Wiring wiring;				// All wiring goes in the Wiring file
    Calibration calibration;	// All calibration codes in the Calibration file
    Geometry geometry;			// All geometry values and calcs in the Geometry file

    // Subsystems
    DriveLine driveLine;		// manages driveline sensors and acutators
    Claw claw;

    // Record/playback
    Robot.RecordedRobotState script = new Robot.RecordedRobotState();   // manages the record/playback data
    boolean recording;          // if on, robot should be recording
    boolean playingBack;        // if on, robot should be playing back
    int playbackPosition = 0;  // tracks where we are in the playback sequence

    // Run modes
    Autonomous autonomous;		// handles autonomous part of the game
    Teleop teleop;				// handles teleop part of the game

    /**
     * Constructor
     */
    public Robot() {
    	super();
    	// This order is important, please don't change it!
    	wiring = new Wiring();
    	calibration = new Calibration();
    	geometry = new Geometry(this);
    	driveLine = new DriveLine(this);
    	claw = new Claw(this);
    	oi = new OI(this);
    	// autonomous = new Autonomous(this);
        teleop = new Teleop(this);
	}

	/**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
    	oi.configure();
        calibrate();
    }

    @Override
	public void startCompetition() {
		super.startCompetition();
	}

	/**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    @Override
    public void disabledInit() {
        stopPlayback();
    	// autonomous.cancel();
    	teleop.cancel();
    	stopAll();
    }

    @Override
    public void disabledPeriodic() {
    	// periodic code for disabled mode should go here
    }

    @Override
    public void autonomousInit() {
        autonomous.setup();
        // TODO - get the autonomous playback we want to execute, and load it
        initAutonomousPlayback();
        this.startPlayback();
    }

    void initAutonomousPlayback() {
        // TODO - I need to be filled with a script!
    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
        // this runs any scheduled commands
        // perform playback, if there is a script
        // Scheduler.getInstance().run();
        playback();
        autonomous.loop();
        // note - we don't record in autonomous mode
        // Updating subsystems only writes state.  To actually make the robot do/move, call actuate()
        actuate();
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
    	if (autonomous != null) {
    		autonomous.cancel();
        }
        if (playingBack) {
            this.stopPlayback();    // whatever we are doing, stop playback
        }
    	teleop.setup();
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        // first, if we are playing back and the interrupt trigger has NOT been pressed,
        // playback.  The RIGHT JOYSTICK TRIGGER cancels a playback in progress
        if (oi.rightJoystick.getTriggerPressed()) {
            this.stopPlayback();
        }
        // if we're playing back, do that, otherwise go through normal teleop
        if (this.playingBack) {
            playback();
        }
        else { // run the normal teleop loop and actuate
            teleop.loop();
            // record state, if recording
            record();
        }
        // Updating subsystems only writes state.  To actually make the robot do/move, call actuate()
        actuate();
    }

    /**
     * Call this to stop anything the bot is doing
     */
    void stopAll() {
    	driveLine.stop();
    }

    /**
     * Call this to reset the bot
     */
    void resetTheBot() {
        pausePlayback();
        resetRecording();
        driveLine.stop();
        claw.open();
        actuate();
    }

    /**
     * Perform any calibration.  Robot should not be moving!!!
     */
    void calibrate() {
    	driveLine.calibrate();
    }

    /**
     * Returns "Robot"
     */
    public String getName() {
        return "Robot";
    }

    /**
     * Actually actuate the robot.  Called at the END of the periodics.
     *
     */
    protected void actuate() {
        // here, subsystems actuate() methods are called.
        this.driveLine.actuate();
        this.claw.actuate();
    }

    ///////// Record //////
    //
    public void resetRecording() {
        this.pauseRecording();
        this.script.clear();
    }

    public void pauseRecording() {
        this.recording = false;
    }

    public void startRecording() {
        this.recording = true;
    }

    void record() {
        if (this.recording) {
            this.script.add(this.getState());
        }
    }

    //////////////////// Playback //////
    public void setScript(RecordedRobotState script) {
        this.script.clear();
        this.script.addAll(script);
    }

    /**
     * Convenience method, calls setScript and then startPlayback
     */
    public void startPlayback(RecordedRobotState script) {
        setScript(script);
        startPlayback();
    }

    /**
     * Starts playback of the loaded script from point 0
     */
    public void startPlayback() {
        this.playbackPosition = 0;
        this.resumePlayback();
    }

    public void pausePlayback() {
        this.playingBack = false;
    }

    public void resumePlayback() {
        this.playingBack = true;
    }

    public void stopPlayback() {
        this.playingBack = false;
    }

    void playback() {
        if (this.playingBack && this.playbackPosition < this.script.size()) {
            this.setState(this.script.get(this.playbackPosition++));
        }
    }

    public static class State {
        // You need a state object here for each
        DriveLine.State driveLineState = new DriveLine.State();
        Claw.State clawState = new Claw.State();
    }

    public static class RecordedRobotState extends ArrayList<State> {
    }

    public State getState() {
        State state = new State();
        // we add copies of the state
        state.driveLineState = this.driveLine.state.copy();
        state.clawState = this.claw.state.copy();
        return state;
    }

    public void setState(State state) {
        // if you add new subsystems, you need to add their states here
        this.driveLine.state = state.driveLineState;
        this.claw.state = state.clawState;
    }

    /**
     * Require all subsystems in the command provided. This can be used by commands that need them all.
     */
    public Subsystem[] getAllSubsystems() {
        return new Subsystem[] {
            driveLine,
            claw
        };
    }
}
