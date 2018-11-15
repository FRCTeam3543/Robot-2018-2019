package team3543.robot;

import edu.wpi.first.wpilibj.command.Subsystem;

import java.io.Serializable;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * This is the pneumatic claw from 2017-2018 season, adapted for this codebase
 *
 * @author mk
 */
public class Claw extends Subsystem implements IActuate {

	State state = new State();

	DoubleSolenoid doubleSolenoid;
	Compressor airpusher;

	public Claw(Robot robot) {
		super("Claw");

		airpusher = new Compressor(robot.wiring.CLAW_COMPRESSOR_PORT);
		doubleSolenoid = new DoubleSolenoid(robot.wiring.CLAW_SOLENOID_PORT_1, robot.wiring.CLAW_SOLENOID_PORT_2);
	}

	public void open() {
		//doubleSolenoid.set(DoubleSolenoid.Value.kForward);
		state.open = true;
	}

	public void close() {
		//this.doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
		state.open = false;
	}

	public void actuate() {
		this.doubleSolenoid.set(isOpen() ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

	public boolean getPressureSwitchValve() {
		return airpusher.getPressureSwitchValue();
	}

	public boolean isCompressorEnabled() {
		return airpusher.enabled();
	}

	public boolean isOpen() {
		return state.open;
	}

	public void off() {
		doubleSolenoid.set(DoubleSolenoid.Value.kOff);
		state.open = false;
	}

	public void setClosedLoopControl(boolean b) {
		airpusher.setClosedLoopControl(b);
	}

	public void startCompressor() {
		airpusher.setClosedLoopControl(true);
		airpusher.start();
	}

	public void stopCompressor() {
		airpusher.stop();
	}

	public void reset() {
		setClosedLoopControl(true);
		startCompressor();
		close();
	}

	public Activity resetActivity() {
		return Activity.once(Activity.wrap(this::reset));
	}

	public Activity openActivity() {
		return Activity.wrap(this::isOpen, this::open);
	}

	public Activity closeActivity() {
		return Activity.wrap(() -> { return !isOpen(); }, this::close);
	}

	@Override
	protected void initDefaultCommand() {
		// nothing
	}

	public static class State {
		public boolean open = false;

		public State() {

		}

		public State(boolean open) {
			this();
			this.open = open;
		}

		public State copy() {
			return new State(this.open);
		}
	}
}
