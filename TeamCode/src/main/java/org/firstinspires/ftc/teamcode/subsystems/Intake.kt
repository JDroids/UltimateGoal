package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Intake(hardwareMap: HardwareMap) : SubsystemBase() {
    private val intakeMotor
            by lazy { hardwareMap.get(DcMotorEx::class.java, "intakeMotor") }

    private var firstTime = true

    private var power = 0.0

    override fun periodic() {
        if (firstTime) {
            intakeMotor.direction = DcMotorSimple.Direction.REVERSE

            firstTime = false
        }

        intakeMotor.power = power
    }

    fun intake() {
        power = 1.0
    }

    fun outtake() {
        power = -1.0
    }

    fun stop() {
        power = 0.0
    }
}