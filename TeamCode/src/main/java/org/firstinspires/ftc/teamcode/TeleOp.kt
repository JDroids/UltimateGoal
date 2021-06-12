package org.firstinspires.ftc.teamcode

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.CommandScheduler
import com.arcrobotics.ftclib.command.button.Button
import com.arcrobotics.ftclib.command.button.GamepadButton
import com.arcrobotics.ftclib.command.button.Trigger
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.command.ShootCommand
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive
import org.firstinspires.ftc.teamcode.subsystems.Intake
import org.firstinspires.ftc.teamcode.subsystems.Shooter
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

@TeleOp(group = "1")
class TeleOp : CommandOpMode() {
    lateinit var mecanumDrive: SampleMecanumDrive
    lateinit var wobbleClaw: WobbleClaw
    lateinit var intake: Intake
    lateinit var shooter: Shooter


    override fun initialize() {
        mecanumDrive = SampleMecanumDrive(hardwareMap)
        wobbleClaw = WobbleClaw(hardwareMap, telemetry, false)
        intake = Intake(hardwareMap)
        shooter = Shooter(hardwareMap)

        val driverGamepad = GamepadEx(gamepad1)


        val gamepadButtonA = GamepadButton(driverGamepad, GamepadKeys.Button.A)
        val gamepadButtonB = GamepadButton(driverGamepad, GamepadKeys.Button.B)
        gamepadButtonA.whenPressed( wobbleClaw::open )
        gamepadButtonB.whenPressed( wobbleClaw::close )

        val gamepadButtonLeftBumper = GamepadButton(driverGamepad, GamepadKeys.Button.LEFT_BUMPER)
        val gamepadButtonRightBumper = GamepadButton(driverGamepad, GamepadKeys.Button.RIGHT_BUMPER)

        gamepadButtonLeftBumper
                .whenPressed(intake::outtake)
                .whenReleased(intake::stop)

        gamepadButtonRightBumper
                .whenPressed(intake::intake)
                .whenReleased(intake::stop)

        val gamepadTriggerLeft = Trigger { gamepad1.left_trigger > 0.5 }
                .whenActive(shooter::pushRing)
    }

    private val shootCommand by lazy { ShootCommand(shooter) }


    override fun run() {
        super.run()

        mecanumDrive.setDrivePower(
                Pose2d(
                        Vector2d(-gamepad1.left_stick_y.toDouble(),
                                -gamepad1.left_stick_x.toDouble()),
                        -gamepad1.right_stick_x.toDouble()))

        wobbleClaw.pivotPower(
                when {
                    gamepad1.dpad_up -> -1.0
                    gamepad1.dpad_down -> 0.4
                    else -> 0.0
                }
        )



        if (gamepad1.right_trigger > 0.5) {
            schedule(shootCommand)
        }
        else {
            CommandScheduler.getInstance().cancel(shootCommand)
        }

        telemetry.update()
    }
}