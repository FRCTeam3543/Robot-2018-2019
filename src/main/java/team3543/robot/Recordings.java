package team3543.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * For storing static copies of recordings
 *
 * Process:
 * - record paths on the bot
 * - recording will dump to the console
 * - copy and past into a RECORDINGS.put() as per below.
 */
public class Recordings {
	public static final String NETWORK_TABLE = "recording";
    public static final String RECORD_SAVE_CHANNEL = "saveFromBot";
    public static final String RECORD_LOAD_CHANNEL = "loadToBot";

    static Map<String, RobotScript.ScriptSource> RECORDINGS = new HashMap<>();

    // All the recordings go here.
    static {
        // add each recording like this
        add("EMPTY", "[]");

        // but we will also read in the ones in the recordings/ directory
        // that gets bundled in the JAR.
        addRecordingFile("EMPTY");
        addRecordingFile("NONE");
    }

    ///////////////// Ingore below this line ////////////////

    static void add(String name, RobotScript script) {
        RECORDINGS.put(name, RobotScript.ScriptSource.wrap(script));
    }

    static void add(String name, String value) {
        RECORDINGS.put(name, RobotScript.ScriptSource.fromJSONString(value));
    }

    /**
     * Add JSON from a resource file.  Should be in the recordings/ folder as <name>.json
     * Note - will print the stack trace but silently do NOTHING if the file is missing
     */
    static void addRecordingFile(final String name) {
        String path = String.format("recordings/%s.json", name);
        try {
            RECORDINGS.put(name, () -> {
                try {
                    InputStream is = Recordings.class.getClassLoader().getResourceAsStream(path);
                    if (is == null) {
                        throw new IllegalArgumentException("Missing path");
                    }
                    InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                    int cread = -1;
                    char[] chars = new char[4096];
                    StringBuilder sb = new StringBuilder();
                    while (-1 != (cread = reader.read(chars))) {
                        sb.append(chars, 0, cread);
                    }
                    is.close();
                    return RobotScript.fromJSON(sb.toString());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return RobotScript.EMPTY;
                }
            });
        } catch(Exception ex) {
            ex.printStackTrace();
            // oops
        }
    }

    public static RobotScript getScript(String name) {
        if (RECORDINGS.containsKey(name)) {
            return RECORDINGS.get(name).getScript();
        }
        else {
            throw new IllegalArgumentException("No such script");
        }
    }

    public static String[] getScriptNames() {
        return (String [])RECORDINGS.keySet().toArray();
    }

    public static RecordingChooser chooser() {
        RecordingChooser chooser = new RecordingChooser();
        chooser.addDefault("EMPTY", RobotScript.ScriptSource.wrap(RobotScript.EMPTY));
        for (String s : RECORDINGS.keySet()) {
            chooser.addObject(s, RECORDINGS.get(s));
        }
        return chooser;
    }

    public static class RecordingChooser extends SendableChooser<RobotScript.ScriptSource> {
        public RecordingChooser() {
            super();
        }
    }
}

