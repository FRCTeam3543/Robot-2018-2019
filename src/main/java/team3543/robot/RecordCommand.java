package team3543.robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Command that allows recording
 */
class RecordCommand extends Command {
    Robot robot;
    Robot.RecordedRobotState recording;
    boolean stopped = false;

    public RecordCommand(Robot robot) {
        this.robot = robot;
        recording = new Robot.RecordedRobotState();
    }

    @Override
    protected void initialize() {
        recording.clear();
        stopped = false;
    }

    @Override
    protected void execute() {
        recording.add(robot.getState());
    }

    @Override
    protected boolean isFinished() {
        return stopped;
    }

    public void stopRecording() {
        this.stopped = true;
    }

    public Robot.RecordedRobotState getRecording() {
        return this.recording;
    }
}