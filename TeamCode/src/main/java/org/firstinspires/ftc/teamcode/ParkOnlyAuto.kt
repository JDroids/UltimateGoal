package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.FunctionalCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitCommand
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.command.FollowTrajectory
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@Autonomous(group = "1")
class ParkOnlyAuto : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive

    override fun initialize() {
        mecanumDrive = SampleMecanumDrive(hardwareMap)
        mecanumDrive.poseEstimate = Pose2d(-63.0, -39.0, Math.PI)

        var secondsToWait = 0

        var previousDown = false
        var previousUp = false


        while (!isStarted && !isStopRequested) {
            if (!previousDown && gamepad1.dpad_down && secondsToWait > 0) {
                secondsToWait--
            }
            else if(!previousUp && gamepad1.dpad_up && secondsToWait < 30) {
                secondsToWait++
            }

            previousDown = gamepad1.dpad_down
            previousUp = gamepad1.dpad_up

            telemetry.addData("Seconds to Wait Before Parking", secondsToWait)
            telemetry.update()
        }

        val timer = ElapsedTime()

        schedule(SequentialCommandGroup(
                FunctionalCommand({timer.reset()}, {}, {}, {timer.seconds() > secondsToWait}),
                // Park
                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder(true)
                            .lineToConstantHeading(Vector2d(9.0, -39.0))
                },
        ))
    }
}