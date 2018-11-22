package team3543.robot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordingsTest {

	@BeforeEach
	void setup() {
	}

    @Test
    public void testReadFromFile() {
        Recordings.addRecordingFile("TEST");
        RobotScript script = Recordings.getScript("TEST");
        assertEquals("[{\"driveLine\":{\"shiftMode\":\"HIGH\",\"driveMode\":\"TANK\",\"magnitudeOrLeft\":0.0,\"curveOrRight\":0.0,\"squaredInputs\":false}}]", script.toJSON());
    }

    @Test
    public void testReadFromFileFailsOnMissingFile() {
        Recordings.addRecordingFile("MISSING");
        RobotScript script = Recordings.getScript("MISSING");
        assertEquals("[]", script.toJSON());
    }
}