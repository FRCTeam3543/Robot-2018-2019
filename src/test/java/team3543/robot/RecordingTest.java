package team3543.robot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordingTest {
    // MockyBot robot;
    MyRecordable recordable;
    Recording recording;

	@BeforeEach
	void setup() {
        recordable = new MyRecordable();
        recording = new Recording(recordable);
	}

	@Test
	void testStartAndStopRecording() {
        assertFalse(recording.isRecording());
        recording.startRecording();
        assertTrue(recording.isRecording());
        recording.stopRecording();
        assertFalse(recording.isRecording());
    }

	@Test
	void testRecord() {
        recording.startRecording();
        recording.record("test", nums());
        recording.record("test", nums());
        recording.record("test", nums());
        recording.stopRecording();
        assertEquals(3, recording.recordReel.size());
    }

	@Test
	void testRecordAndPlayback() {
        recording.startRecording();
        recording.record("test", nums());
        recording.record("test", nums());
        recording.record("test", nums());
        recording.stopRecording();
        assertEquals(3, recording.recordReel.size());
        recording.startPlayback();
        int ctr = 0;
        while (!recording.loop() && ctr++ < 1000) {
            // ok
        }
        // check the count
        assertEquals(3, ctr);
        assertEquals(3, recordable.playbackCtr);
    }

    double ctr = 0.0;
    double [] nums() {
        return new double[] { ctr++, ctr++ };
    }

    static class MyRecordable implements Recording.Recordable {
        int playbackCtr = 0;
        @Override
        public void playback(String op, double[] args) {
            playbackCtr++;
        }

        @Override
        public String getName() {
            return null;
        }

    }

    // static class MockSubsystem extends Subsystem {
    //     MockSubsystem(Robot robot) {

    //     }

    //     @Override
    //     public void initDefaultCommand() {
    //         // empty
    //     }
    // }

    // static class MockyBot extends TimedRobot {
    //     MockSubsystem sub1;
    //     MockSubsystem sub2;

    //     MockyBot() {
    //         sub1 = new MockSubsystem(this);
    //         sub2 = new MockSubsystem(this);
    //     }
    // }
}