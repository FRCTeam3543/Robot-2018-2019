package team3543.robot;

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

	Recordable base;
	long startTime = System.currentTimeMillis();
	// stores the last recording
	RecordReel recordReel = new RecordReel();
	Iterator<Record> playbackReel;
	int reelPtr = 0;

	// stores a mapping of subsystem names to subsystem
	Map<String, Subsystem> registry = new HashMap<>();
	private Mode mode = Mode.IDLE;

	Recording(Recordable base) {
		this.base = base;
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
		playbackReel = recordReel.iterator();
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
		mode = Mode.RECORD;
	}

	public void stopRecording() {
		mode = Mode.IDLE;
	}

	public boolean isRecording() {
		return mode == Mode.RECORD;
	}

	public Mode getMode() {
		return this.mode;
	}

	public void record(String context, double[] args) {
		if (mode == Mode.RECORD) {
			recordReel.add(new Record(elapsed(), context, args));
		}
	}

	public void execute(Record record) {
		// need java.lang.reflect
		this.base.playback(record.context, record.args);
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

	public static interface Recorder {
		void record(Recordable recordable, String op, double[] args);
	}

	public static interface Recordable {
		void playback(String op, double[] args);
		String getName();
	}

	public static class RecordReel extends ArrayList<Record >{
		public static final long serialVersionUID = 0L;
	}

	public static class Record {
		long ts;	// timetamp
		String context;
		double [] args;

		public Record(long ts, String context, double args[]) {
			this.context = context;
			this.args = args;
			this.ts = ts;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			int ctr = 0;
			for (double d: args) {
				if (ctr++ > 0) sb.append(",");
				sb.append(d);
			}
			return String.format("%l:%s.%s:%s", ts, context, sb.toString());
		}

		public static Record parse(String cmd) {
			String [] parts = cmd.split(":");
			if (parts.length < 2) throw new IllegalArgumentException(String.format("Malformed: %s", cmd));
			long ts = Long.parseLong(parts[0]);
			String ctx = parts[1];
			String[] doubles = parts[2].split(",");
			double [] args = new double[doubles.length];
			int ctr = 0;
			for (String s : doubles) {
				args[ctr++] = Double.parseDouble(s);
			}
			return new Record(ts, ctx, args);
		}
	}
}
