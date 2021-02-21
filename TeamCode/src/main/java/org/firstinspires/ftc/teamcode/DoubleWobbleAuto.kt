package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.FunctionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.FollowTrajectory
import org.firstinspires.ftc.teamcode.command.SetWobblePivotPosition
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw
import java.util.function.BooleanSupplier
import java.util.function.Consumer

@Autonomous(group = "1")
class DoubleWobbleAuto : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var detector: UGRectDetector
    lateinit var stackHeight: UGRectDetector.Stack

    override fun initialize() {
        val wobbleClaw = WobbleClaw(hardwareMap, telemetry)
        wobbleClaw.close()

        mecanumDrive = SampleMecanumDrive(hardwareMap)
        mecanumDrive.poseEstimate = Pose2d(-63.0, -39.0, 0.0)

        detector = UGRectDetector(hardwareMap)
        detector.init()

        detector.setTopRectangle(0.43, 0.14)
        detector.setBottomRectangle(0.48, 0.14)
        detector.setRectangleSize(20, 10)
        detector.setThreshold(25)

        while (!isStarted && !isStopRequested) {
            stackHeight = detector.stack

            telemetry.addData("Stack Height", stackHeight)
            telemetry.update()
        }

        detector.camera.stopStreaming()
        detector.camera.setFlashlightEnabled(false)

        schedule(SequentialCommandGroup(
                // Drop Wobble Goal
                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .lineToConstantHeading(Vector2d(-45.0, -57.0 ))
                },

                when (stackHeight) {
                    // position A
                    UGRectDetector.Stack.ZERO -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder()
                                .splineTo(Vector2d(0.0, -60.0), 0.0)
                    }

                    // position B
                    UGRectDetector.Stack.ONE -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder()
                                .splineTo(Vector2d(24.0, -36.0), 0.0)
                    }

                    // position C
                    UGRectDetector.Stack.FOUR -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder()
                                .splineTo(Vector2d(48.0, -60.0), 0.0)
                    }
                },
                SetWobblePivotPosition(wobbleClaw, WobbleClaw.PivotPosition.DOWN),
                wobbleClaw.instant { wobbleClaw.open() },

                // Grab Second Wobble

                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .back(22.0)
                },

                FunctionalCommand(
                        { mecanumDrive.turnToAsync(Math.toRadians(180.0)) }.runnable,
                        {}.runnable,
                        Consumer<Boolean> {  },
                        BooleanSupplier { mecanumDrive.isBusy },
                        mecanumDrive),

                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .splineTo(Vector2d(-30.0, -22.0), Math.toRadians(180.0))
                },

                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .forward(3.0)
                },

                wobbleClaw.instant { wobbleClaw.close() },
                SetWobblePivotPosition(wobbleClaw, WobbleClaw.PivotPosition.UP)
            ))
    }
}