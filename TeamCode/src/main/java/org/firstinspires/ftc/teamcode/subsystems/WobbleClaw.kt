package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.Telemetry

class WobbleClaw(hardwareMap: HardwareMap, private val telemetry: Telemetry) : SubsystemBase() {
    private val claw by lazy {hardwareMap.get(Servo::class.java, "wobbleClaw")}
    private val pivot by lazy {hardwareMap.get(DcMotorEx::class.java, "wobblePivot")}
    private var firstTime = true


    override fun periodic() {
        if (firstTime) {
            pivot.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

            pivot.targetPositionTolerance = 10
            resetEncoder()
            claw.position = 0.0

            firstTime = false
        }

        telemetry.addData("Wobble Pivot Pos", pivot.currentPosition)
    }

    fun close() {
        claw.position = 0.0
    }

    fun open() {
        claw.position = 0.5
    }

    enum class PivotPosition(val targetPosition: Int, val power: Double) {
        UP(0, 1.0),
        DOWN(180, 0.6)
    }

    fun setPivot(position: PivotPosition) {
        pivot.targetPosition = position.targetPosition
        pivot.power = position.power
    }

    fun resetEncoder() {
        pivot.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        pivot.targetPosition = 0
        pivot.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    fun isBusy() = pivot.isBusy
}

