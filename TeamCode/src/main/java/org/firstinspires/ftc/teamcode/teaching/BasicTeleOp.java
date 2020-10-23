package org.firstinspires.ftc.teamcode.teaching;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BasicTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() {
        DcMotor frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        DcMotor frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        DcMotor backLeftMotor = hardwareMap.get(DcMotor.class, "backLeft");
        DcMotor backRightMotor = hardwareMap.get(DcMotor.class, "backRight");

        DcMotor shooterMotor = hardwareMap.get(DcMotor.class, "shooterMotor");
        DcMotor intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        waitForStart();

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
                shooterMotor.setPower(1.0);
            } else {
                shooterMotor.setPower(0.0);
            }

            // intake control
            if (gamepad1.left_bumper) {
                intakeMotor.setPower(1.0);
            } else if (gamepad1.right_bumper) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }
        }

    }
}
