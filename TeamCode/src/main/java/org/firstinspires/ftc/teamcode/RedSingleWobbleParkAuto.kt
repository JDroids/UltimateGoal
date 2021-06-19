package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import org.firstinspires.ftc.teamcode.command.*
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@Autonomous(group="1")
class RedSingleWobbleParkAuto : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive

    lateinit var detector: UGRectDetector
    lateinit var stackHeight: UGRectDetector.Stack

    override fun initialize() {
        val wobbleClaw = WobbleClaw(hardwareMap, telemetry)
        val shooter = Shooter(hardwareMap)

        wobbleClaw.close()

        mecanumDrive = SampleMecanumDrive(hardwareMap)
        mecanumDrive.poseEstimate = Pose2d(-63.0, -53.0, Math.PI)

        detector = UGRectDetector(hardwareMap)
        detector.init()

        detector.setTopRectangle(0.63, 0.1)
        detector.setBottomRectangle(0.7, 0.1)
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
                    mecanumDrive.trajectoryBuilder(true)
                            .lineToConstantHeading(Vector2d(-45.0, -57.0 ))
                },

                when (stackHeight) {
                    // position A
                    UGRectDetector.Stack.ZERO -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder(true)
                                .splineTo(Vector2d(0.0, -55.0), 0.0)
                    }

                    // position B
                    UGRectDetector.Stack.ONE -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder(true)
                                .splineTo(Vector2d(24.0, -33.0), 0.0)
                    }

                    // position C
                    UGRectDetector.Stack.FOUR -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder(true)
                                .splineTo(Vector2d(48.0, -57.0), 0.0)
                    }
                },

                SetWobblePivotPosition(wobbleClaw, WobbleClaw.PivotPosition.DOWN),

                wobbleClaw.instant { wobbleClaw.open() },

                // Park
                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .splineTo(Vector2d(8.0, -57.0), 0.0)
                }
        ))
    }
}