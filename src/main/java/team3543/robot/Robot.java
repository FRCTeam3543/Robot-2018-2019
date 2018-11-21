package team3543.robot;

import java.io.*;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in
 * the project.
 */
public class Robot extends TimedRobot {
	public static Logger LOG = Logger.getLogger("Robot");

	////////////////// Variables

    final OI oi;    					// Operator Interface
    final Recorder recorder;            // Record/playback manager

    ////////////////// Subsystems
    final DriveLine driveLine;		    // manages driveline sensors and acutators
//    Claw claw;

    // Run modes
    Autonomous autonomous = null;		// handles autonomous part of the game

    /**
     * Constructor
     */
    public Robot() {
    	super();
    	driveLine = new DriveLine();
//    	claw = new Claw();
        oi = new OI(this);
        recorder = new Recorder(this);
    }

	/**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
    	oi.configure();
        calibrate();
        // start the driveLine compressor
        driveLine.reset();
//        claw.reset();
    }

	/**
     * This method is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    @Override
    public void disabledInit() {
        recorder.stopPlayback();
        if (autonomous != null) autonomous.cancel();
    	stopAll();
    }

    /**
     * This method is called periodically when disabled.  Probably can stay empty.
     */
    @Override
    public void disabledPeriodic() {
    	// periodic code for disabled mode should go here
    }

    /**
     * Initialize autonomous mode.
     *
     * Here, we simply start a pre-recorded script provided by the OI
     */
    @Override
    public void autonomousInit() {
        autonomous.setup();
        recorder.setScript(oi.getAutonomousScript());
        recorder.startPlayback();
    }

    /**
     * This function is called periodically during autonomous mode
     *
     */
    @Override
    public void autonomousPeriodic() {
        // this runs any scheduled commands
        // perform playback, if there is a script
        // Scheduler.getInstance().run();
        recorder.playback();
        if (autonomous != null) {
            autonomous.loop();
        }
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
        if (recorder.playingBack) {
            recorder.stopPlayback();    // whatever we are doing, stop playback
        }
        stopAll();
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        // read the operator interface and apply
        oi.loop();
        // Updating subsystems only writes state.  To actually make the robot do/move, call actuate()
        actuate();
        // record state, if recording
        recorder.record();
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
        recorder.stopPlayback();
        recorder.resetRecording();
        driveLine.stop();
//        claw.open();
        actuate();
    }

    /**
     * Perform any calibration.  Robot should not be moving!!!
     */
    void calibrate() {
    	driveLine.calibrate();
    }

    /**
     * Returns the name of the class ("Robot")
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Actually actuate the robot.  Called at the END of the periodics.
     *
     */
    protected void actuate() {
        // here, subsystems actuate() methods are called.
        // This should make them actually do something
        this.driveLine.actuate();
//        this.claw.actuate();
    }

    public State getState() {
        State state = new State();
        // we add copies of the state
        state.driveLineState = this.driveLine.state.copy();
//        state.clawState = this.claw.state.copy();
        return state;
    }

    public void setState(State state) {
        // if you add new subsystems, you need to add their states here
        this.driveLine.state = state.driveLineState;
//        this.claw.state = state.clawState;
    }

    /**
     * Require all subsystems in the command provided. This can be used by commands that need them all.
     */
    public Subsystem[] getAllSubsystems() {
        return new Subsystem[] {
            driveLine
//            ,claw
        };
    }

    public static class State {
        // You need a state object here for each subsystem
        // You need a state object here for each subsystem
        DriveLine.State driveLineState = new DriveLine.State();
//        Claw.State clawState = new Claw.State();
    }

}
