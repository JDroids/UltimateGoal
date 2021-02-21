package org.firstinspires.ftc.teamcode.command

import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive

class FollowTrajectory(private val mecanumDrive: SampleMecanumDrive,
                       private val trajectory: () -> TrajectoryBuilder) : CommandBase() {
    override fun initialize() {
        mecanumDrive.followTrajectoryAsync(trajectory().build())
    }

    override fun execute() {
        mecanumDrive.update()
    }

    override fun isFinished() = !mecanumDrive.isBusy

}