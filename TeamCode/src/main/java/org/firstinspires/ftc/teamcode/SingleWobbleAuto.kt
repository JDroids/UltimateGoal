package org.firstinspires.ftc.teamcode

import com.arcrobotics.ftclib.command.CommandOpMode
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@Autonomous(group = "1")
class SingleWobbleAuto : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var detector: UGRectDetector
    lateinit var stackHeight: UGRectDetector.Stack

    override fun initialize() {
        val wobbleClaw = WobbleClaw(hardwareMap, telemetry)
        wobbleClaw.close()

        mecanumDrive = SampleMecanumDrive(hardwareMap)

        detector = UGRectDetector(hardwareMap)
        detector.init()

        detector.setTopRectangle(0.48, 0.1)
        detector.setBottomRectangle(0.52, 0.1)

        while (!isStarted && !isStopRequested) {
            stackHeight = detector.stack

            telemetry.addData("Stack Height", stackHeight)
            telemetry.update()
        }

        detector.camera.setFlashlightEnabled(false)
    }
}