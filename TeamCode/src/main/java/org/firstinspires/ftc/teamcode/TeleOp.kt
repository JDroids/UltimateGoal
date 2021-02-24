package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.button.GamepadButton
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@TeleOp(group = "1")
class TeleOp : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var wobbleClaw: WobbleClaw

    override fun initialize() {
        wobbleClaw = WobbleClaw(hardwareMap, telemetry, false)
        mecanumDrive = SampleMecanumDrive(hardwareMap)

        val driverGamepad = GamepadEx(gamepad1)

        val gamepadButtonA = GamepadButton(driverGamepad, GamepadKeys.Button.A)
        val gamepadButtonB = GamepadButton(driverGamepad, GamepadKeys.Button.B)

        gamepadButtonA.whenPressed( wobbleClaw::open )
        gamepadButtonB.whenPressed( wobbleClaw::close )
}

    override fun run() {
        super.run()

        mecanumDrive.setDrivePower(
                Pose2d(
                        Vector2d(-gamepad1.left_stick_y.toDouble(),
                                -gamepad1.left_stick_x.toDouble()),
                        -gamepad1.right_stick_x.toDouble()))
        wobbleClaw.pivotPower(
                if (gamepad1.left_trigger > 0.01) {
                    -gamepad1.left_trigger.toDouble()
                }
                else {
                    gamepad1.right_trigger.toDouble() * 0.4
                }
        )


        telemetry.update()
    }
}