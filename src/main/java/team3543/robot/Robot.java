package team3543.robot;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj.TimedRobot;

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
    	stopAll();
    }

    /**
     * Initialize autonomous mode.
     *
     * Here, we simply start a pre-recorded script provided by the OI
     */
    @Override
    public void autonomousInit() {
        recorder.setScript(oi.getAutonomousScript());
        recorder.startPlayback();
    }

    /**
     * This function is called periodically during autonomous mode
     */
    @Override
    public void autonomousPeriodic() {
        // this runs any scheduled commands
        // perform playback, if there is a script
        // Scheduler.getInstance().run();
        recorder.playback();
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
        // if recording or playing back, stop it
        recorder.stopPlayback();
        recorder.stopRecording();
        driveLine.stop();
        actuate();
        // claw.off();
    }

    /**
     * Call this to reset the bot
     */
    void resetTheBot() {
        stopAll();
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
        state.driveLine = this.driveLine.state.copy();
//        state.clawState = this.claw.state.copy();
        return state;
    }

    /**
     * Set the robot state
     *
     * This will cascade-set the states of all the actuators.
     */
    public void setState(State state) {
        // if you add new subsystems, you need to add their states here
        this.driveLine.state = state.driveLine;
//        this.claw.state = state.clawState;
    }

    /**
     * Describes Robot state.  This is used for recording and playback.
     *
     * Every subsystem involved in record/playback should have its own
     * State class, and an instance should be referenced here.  Record/playback
     * serializes this state to JSON every loop to make a recording, and a
     * deserialized instance is fed to the robot each loop during playback.
     */
    public static class State {
        // You need a state object here for each subsystem
        public DriveLine.State driveLine = new DriveLine.State();
//        Claw.State clawState = new Claw.State();
    }

}
