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

@Autonomous(group = "2")
class OldSingleWobbleAuto : CommandOpMode() {
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

        detector.setTopRectangle(0.48, 0.14)
        detector.setBottomRectangle(0.53, 0.14)
        detector.setRectangleSize(20, 10)
        detector.setThreshold(25)

        while (!isStarted && !isStopRequested) {
            stackHeight = detector.stack

            telemetry.addData("Stack Height", stackHeight)
            telemetry.addData("Top Average", detector.topAverage)
            telemetry.addData("Bottom Average", detector.bottomAverage)
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

                // Park

                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .back(8.0)
                },

                SetWobblePivotPosition(wobbleClaw, WobbleClaw.PivotPosition.UP),

                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder(true)
                            .splineTo(Vector2d(6.0, -36.0), Math.toRadians(180.0))
                }
        ))
    }
}