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

    override fun initialize() {
        val wobbleClaw = WobbleClaw(hardwareMap, telemetry)
        mecanumDrive = SampleMecanumDrive(hardwareMap)

        val driverGamepad = GamepadEx(gamepad1)

        val gamepadButtonA = GamepadButton(driverGamepad, GamepadKeys.Button.A)
        val gamepadButtonB = GamepadButton(driverGamepad, GamepadKeys.Button.B)
        val gamepadButtonY = GamepadButton(driverGamepad, GamepadKeys.Button.Y)

        val gamepadButtonLeftBumper = GamepadButton(driverGamepad, GamepadKeys.Button.LEFT_BUMPER)
        val gamepadButtonRightBumper = GamepadButton(driverGamepad, GamepadKeys.Button.RIGHT_BUMPER)

        gamepadButtonA.whenPressed( wobbleClaw::open )
        gamepadButtonB.whenPressed( wobbleClaw::close )
        gamepadButtonY.whenPressed( wobbleClaw::resetEncoder )

        gamepadButtonLeftBumper.whenPressed( wobbleClaw::up )
        gamepadButtonRightBumper.whenPressed( wobbleClaw::down )
    }

    override fun run() {
        super.run()

        mecanumDrive.setDrivePower(
                Pose2d(
                        Vector2d(-gamepad1.left_stick_y.toDouble(),
                                -gamepad1.left_stick_x.toDouble()),
                        -gamepad1.right_stick_x.toDouble()))

        telemetry.update()
    }
}