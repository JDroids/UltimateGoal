package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.command.*
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@Autonomous(group="1")
class SingleWobbleShootingAuto : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive

    lateinit var detector: UGRectDetector
    lateinit var stackHeight: UGRectDetector.Stack

    override fun initialize() {
        val wobbleClaw = WobbleClaw(hardwareMap, telemetry)
        val shooter = Shooter(hardwareMap)

        wobbleClaw.close()

        mecanumDrive = SampleMecanumDrive(hardwareMap)
        mecanumDrive.poseEstimate = Pose2d(-63.0, -39.0, Math.PI)

        detector = UGRectDetector(hardwareMap)
        detector.init()

        detector.setTopRectangle(0.52, 0.52)
        detector.setBottomRectangle(0.57, 0.52)
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
                                .splineTo(Vector2d(24.0, -36.0), 0.0)
                    }

                    // position C
                    UGRectDetector.Stack.FOUR -> FollowTrajectory(mecanumDrive) {
                        mecanumDrive.trajectoryBuilder(true)
                                .splineTo(Vector2d(48.0, -46.0), 0.0)
                    }
                },
                SetWobblePivotPosition(wobbleClaw, WobbleClaw.PivotPosition.DOWN),

                wobbleClaw.instant { wobbleClaw.open() },

                // Drive to shooting position
                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .splineTo(Vector2d(-4.0, -48.0), 0.0) // x was -1 for 3 midshots
                },
                TurnToCommand(mecanumDrive, Math.toRadians(-5.0)),

                // Shoot
                ShootCommand(shooter),

                // Park
                FollowTrajectory(mecanumDrive) {
                    mecanumDrive.trajectoryBuilder()
                            .forward(12.0)
                }
        ))
    }
}