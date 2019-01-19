package team3543.robot;


/**
 * Manages recording and playback for the robot
 *
 */
public class Recorder {

    final Robot robot;                      // reference to the robot
    RobotScript script = new RobotScript();   // manages the record/playback data
    boolean recording = false;          // if on, robot should be recording
    boolean playingBack = false;        // if on, robot should be playing back
    int playbackPosition = 0;  // tracks where we are in the playback sequence

    public Recorder(Robot robot) {
        this.robot = robot;
    }

    /**
     * Stop recording if recording, and clear the current script.
     */
    public void resetRecording() {
        this.stopRecording();
        this.script.clear();
    }

    /**
     * Stop recording.
     */
    public void stopRecording() {
        this.recording = false;
    }

    /**
     * Start recording.
     *
     * Note that this will be "from where you left off" unless you call resetRecording first.
     *
     */
    public void startRecording() {
        this.recording = true;
    }

    /**
     * Dump the most recent recording to the the log, and the the appropriate network table.
     *
     */
    public void dumpRecording() {
        Robot.LOG.info(script.toJSON());
    }

    /**
     * Record step, called during a teleop loop.
     */
    void record() {
        if (this.recording) {
            this.script.add(robot.getState());
        }
    }

    /**
     * Set the current script, for playback.  NOTE: Will replace any recorded script.
     *
     * This will stopRecording() and stopPlayback() as well, if either are running.
     *
     * @param script
     */
    public void setScript(RobotScript script) {
        stopRecording();
        stopPlayback();
        this.script.clear();
        this.script.addAll(script);
    }

    /**
     * Return the current script
     *
     * @return
     */
    public RobotScript getScript() {
        return script;
    }


    /**
     * Starts playback.  Note that this will be "from where you left off" unless you resetPlayback() first.
     */
    public void startPlayback() {
        this.playingBack = true;
    }

    /**
     * Stop playback and reset the playback pointer to zero.
     */
    public void resetPlayback() {
        stopPlayback();
        this.playbackPosition = 0;
    }

    /**
     * Step the current playback.
     */
    public void stopPlayback() {
        this.playingBack = false;
    }

    /**
     * Load the robot state from the next item in the playback script.
     *
     * Note that nothing happens if we're past the end of the script.
     */
    void playback() {
        // if we're at the end of the script, stop playback
        if (this.playbackPosition >= this.script.size()) {
            this.stopPlayback();
        }
        // otherwise load robot state from the next recorded item
        if (this.playingBack) {
            robot.setState(this.script.get(this.playbackPosition++));
        }
    }

}
