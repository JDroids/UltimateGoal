package org.firstinspires.ftc.teamcode.teaching;

import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Disabled
@TeleOp
public class NicerTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotorEx frontLeftMotor = hardwareMap.get(DcMotorEx.class, "frontLeft");
        DcMotorEx frontRightMotor = hardwareMap.get(DcMotorEx.class, "frontRight");
        DcMotorEx backLeftMotor = hardwareMap.get(DcMotorEx.class, "backLeft");
        DcMotorEx backRightMotor = hardwareMap.get(DcMotorEx.class, "backRight");

        DcMotorEx shooterMotor = hardwareMap.get(DcMotorEx.class, "shooterMotor");
        DcMotorEx intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");

        Rev2mDistanceSensor intakeSensor =
                hardwareMap.get(Rev2mDistanceSensor.class, "intakeSensor");

        // This tells the shooter motor to use a PIDF loop on velocity to control
        // the motor. Ideally, the PIDF coefficients would be tuned by the user.
        // This has the advantage of producing more consistent movement.
        shooterMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        waitForStart();

        int ringsIntaked = 0;

        boolean wasGamepadBumperPressed = false;

        while (opModeIsActive()) {
            // basic mecanum drive code
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_y;

            frontLeftMotor.setPower(y + x + rx);
            backLeftMotor.setPower(y - x + rx);
            frontRightMotor.setPower(y - x - rx);
            backRightMotor.setPower(y + x - rx);

            // shooter control
            if (gamepad1.x) {
                // While using .setPower with RUN_USING_ENCODER works, it provides a rather low
                // speed cap; using .setVelocity on a DcMotorEx prevents that
                shooterMotor.setVelocity(shooterTPSfromRPM(4000));
            } else {
                shooterMotor.setVelocity(shooterTPSfromRPM(0));
            }

            // The following code allows the driver to turn on and off the intake using a toggle
            // on a single button, but also allow them to make the intake reverse just by holding
            // the right bumper. (This is not sensor-based, however some relatively simple driver
            // optimizations even without special sensors can make it easier for the driver to
            // do the right thing.)
            if (gamepad1.right_bumper) {
                // .setPower is still used for the intake, as it does not really matter
                // how fast the intake spins as long as it is fast enough
                intakeMotor.setPower(-1.0);
            }
            // This logic makes sure that this if statement is only true for the first cycle after
            // the button is hit. This means that the driver has leeway in how long they hold
            // the button; no matter how long it is held, the desired event will only happen
            // once.
            else if (gamepad1.left_bumper != wasGamepadBumperPressed) {
                wasGamepadBumperPressed = gamepad1.left_bumper;

                if (gamepad1.left_bumper) {
                    intakeMotor.setPower(1.0);
                }
                else {
                    intakeMotor.setPower(0.0);
                }
            }
        }

    }

    // 3:2 reduction between motor and shooter
    private final double GEAR_RATIO = 1.5;

    private final int TICKS_PER_REVOLUTION_OF_BARE_MOTOR = 28;

    // this method converts between output rpm and encoder ticks per second for the shooter motor
    private int shooterTPSfromRPM(double rpm) {
        return (int)((rpm * GEAR_RATIO) * TICKS_PER_REVOLUTION_OF_BARE_MOTOR) / 60;
    }

    // rpm * ticks per second
}
