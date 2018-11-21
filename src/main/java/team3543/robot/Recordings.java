package team3543.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.Map;
import java.util.HashMap;

/**
 * For storing static copies of recordings
 *
 * Process:
 * - record paths on the bot
 * - recording will dump to the console
 * - copy and past into a RECORDINGS.put() as per below.
 */
public class Recordings {
    static Map<String, String> RECORDINGS = new HashMap<>();

    // All the recordings go here.
    static {
        // add each recording like this
        add("EMPTY", "[]");
    }

    ///////////////// Ingore below this line ////////////////

    static void add(String name, RobotScript script) {
        RECORDINGS.put(name, script.toJSON());
    }

    static void add(String name, String value) {
        RECORDINGS.put(name, value);
    }

    public static RobotScript getScript(String name) {
        if (RECORDINGS.containsKey(name)) {
            return RobotScript.fromJSON(RECORDINGS.get(name));
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
        chooser.addDefault("EMPTY", ScriptSource.fromJSONString(RECORDINGS.get("EMPTY")));
        for (String s : RECORDINGS.keySet()) {
            chooser.addObject(s, ScriptSource.fromJSONString(RECORDINGS.get(s)));
        }
        return chooser;
    }

    public static class RecordingChooser extends SendableChooser<ScriptSource> {
        public RecordingChooser() {
            super();
        }
    }

    interface ScriptSource {
        RobotScript getScript();

        public static ScriptSource fromJSONString(final String s) {
            return new ScriptSource() {
                @Override
                public RobotScript getScript() {
                    return RobotScript.fromJSON(s);
                }
            };
        }
    }
}

