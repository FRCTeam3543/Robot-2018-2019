package team3543.robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

class PlaybackCommand extends Command {
    Robot.RecordedRobotState recording;
    Robot robot;

    public PlaybackCommand(Robot robot, Robot.RecordedRobotState recording) {
        // requires ALL subsystems
        for (Subsystem s : robot.getAllSubsystems()) {
            requires(s);
        }
        this.recording = new Robot.RecordedRobotState();
        this.recording.addAll(recording);
        this.robot = robot;
    }

    @Override
    protected void initialize() {
        // nothing to do here
    }

    @Override
    protected void execute() {
        if (!recording.isEmpty()) {
            // get the first item
            Robot.State robotState = recording.remove(0);
            robot.setState(robotState);
        }
    }

    @Override
    protected boolean isFinished() {
        return recording.isEmpty();
    }

}