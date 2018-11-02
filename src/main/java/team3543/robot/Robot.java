package team3543.robot;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
import team3543.robot.DriveLine;

//import team3543.base.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in
 * the project.
 */
public class Robot extends TimedRobot implements Recording.Recordable {
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
    Recording recording;

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
    	recording = new Recording(this);
    	driveLine = new DriveLine(this);
    	claw = new Claw(this);
    	oi = new OI(this);
    	autonomous = new Autonomous(this);
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
    	autonomous.cancel();
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
    }

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
    	// this runs any scheduled commands
        Scheduler.getInstance().run();
    	autonomous.loop();
    }

    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.
    	if (autonomous != null) {
    		autonomous.cancel();
    		autonomous = null;
    	}
    	teleop.setup();
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run(); // runs any scheduled commands
    	teleop.loop();
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
    	driveLine.stop();
    }

    /**
     * Perform any calibration.  Robot should not be moving!!!
     */
    void calibrate() {
    	driveLine.calibrate();
    }


    void initRecordables() {
        recordableSubsystems = new HashMap<>();
        // now add all the subsystems
        for (Field field : getClass().getFields()) {
            // only concerned with initialized fields that are not primitive types
            if (field != null
                        && !field.getType().isPrimitive()
                        && field.getType().isAssignableFrom(Subsystem.class)
                        && field.getType().isAssignableFrom(Recording.Recordable.class)) {
                Recording.Recordable r;
                try {
                    r = (Recording.Recordable) (field.get(this));
                    recordableSubsystems.put(r.getName(), r);
                } catch (IllegalArgumentException e) {
                    // Should never happen since this is self-access
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // Should never happen since this is self-access
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Record and playback
     */
    public void record(Recording.Recordable recordable, String op, double[] args) {
        if (recordableSubsystems == null) { // intialize if null
            initRecordables();
        }

        this.recording.record(String.format("%s.%s", recordable.getName(), op), args);
    }

    public void playback(String context, double[] args) {
        // get the subsystem as a local property using reflection and then play to it
        if (recordableSubsystems == null) { // intialize if null
            initRecordables();
        }
        // now play back
        String [] ssop = context.split(".", 2);
        if (ssop.length > 1 && recordableSubsystems.containsKey(ssop[0])) {
            recordableSubsystems.get(ssop[0]).playback(ssop[1], args);
        }
        else {
            LOG.warning(String.format("Skipping unknown context %s", context));
        }
    }

    Map<String, Recording.Recordable> recordableSubsystems = null;

    /**
     * Returns "Robot"
     */
    public String getName() {
        return "Robot";
    }
}
