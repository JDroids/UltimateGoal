package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap

class Intake(hardwareMap: HardwareMap) : SubsystemBase() {
    private val intakeMotor
            by lazy { hardwareMap.get(DcMotorEx::class.java, "intakeMotor") }

    private var firstTime = true

    enum class State(val power: Double) {
        INTAKE(1.0),
        OUTTAKE(-1.0),
        STOP(0.0)
    }

    var state = State.STOP

    override fun periodic() {
        if (firstTime) {
            intakeMotor.direction = DcMotorSimple.Direction.REVERSE

            firstTime = false
        }

        intakeMotor.power = state.power
    }
}