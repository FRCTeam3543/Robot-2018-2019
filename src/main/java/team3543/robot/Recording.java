package team3543.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Class for managing recording and playback
 *
 * Subsystems that participate in record and playback capability
 * @author mk
 *
 */
public class Recording {
	enum Mode { IDLE, RECORD, PLAYBACK }

	Robot robot;
	long startTime = System.currentTimeMillis();
	// stores the last recording
	RecordReel recordReel = new RecordReel();
	Iterator<Record> playbackReel;
	int reelPtr = 0;

	// stores a mapping of subsystem names to subsystem
	Map<String, Subsystem> registry = new HashMap<>();
	private Mode mode = Mode.IDLE;

	Recording(Robot robot) {
		this.robot = robot;
	}

	public void register(Subsystem subsystem) {
		registry.put(subsystem.getName(), subsystem);
	}

	long time() {
		return System.currentTimeMillis();
	}

	long elapsed() {
		return time() - startTime;
	}

	public void startPlayback() {
		if (mode == Mode.RECORD) {
			stopRecording();
		}
		reelPtr = 0;
		mode = Mode.PLAYBACK;
	}

	public void stopPlayback() {
		mode = Mode.IDLE;
	}

	public void startRecording() {
		if (mode == Mode.PLAYBACK) {
			stopPlayback();
		}
		startTime = time();
		recordReel.clear();
	}

	public void stopRecording() {
		mode = Mode.IDLE;
	}

	public void record(Subsystem subsystem, String method, double[] args) {
		if (mode == Mode.RECORD) {
			recordReel.add(new Record(elapsed(), subsystem.getName(), method, args));
		}
	}

	public void execute(Record record) {
		// need java.lang.reflect
		Subsystem subsystem = registry.get(record.subsystemName);
		((Recordable)subsystem).playback(record.methodName, record.args);
	}

	public boolean loop() {
		if (mode == Mode.PLAYBACK) {
			if (playbackReel.hasNext()) {
				Record record = playbackReel.next();
				// TODO - we need to check for clock drift
				execute(record);
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	}

	public static interface Recordable {
		void playback(String op, double[] args);
	}

	public static class RecordReel extends ArrayList<Record >{

	}

	public static class Record {
		long ts;	// timetamp
		String subsystemName;
		String methodName;
		double [] args;

		public Record(long ts, String subsystemName, String methodName, double args[]) {

		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int ctr = 0;
			for (double d: args) {
				if (ctr++ > 0) sb.append(",");
				sb.append(d);
			}
			return String.format("%l:%s.%s:%s", ts, subsystemName, methodName, sb.toString());
		}

		public static Record parse(String cmd) {
			String [] parts = cmd.split(":");
			if (parts.length < 3) throw new IllegalArgumentException(String.format("Malformed: %s", cmd));
			long ts = Long.parseLong(parts[0]);
			String[] ssop = parts[1].split(".");
			if (ssop.length < 2) throw new IllegalArgumentException(String.format("Malformed op: %s", cmd));
			String[] doubles = parts[2].split(",");
			double [] args = new double[doubles.length];
			int ctr = 0;
			for (String s : doubles) {
				args[ctr++] = Double.parseDouble(s);
			}
			return new Record(ts, ssop[0], ssop[1], args);
		}
	}
}
