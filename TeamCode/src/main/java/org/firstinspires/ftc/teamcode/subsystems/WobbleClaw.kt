package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.robotcore.external.Telemetry

class WobbleClaw(hardwareMap: HardwareMap, private val telemetry: Telemetry, private val reset: Boolean=true) : SubsystemBase() {
    private val claw by lazy {hardwareMap.get(Servo::class.java, "wobbleClaw")}
    private val pivot by lazy {hardwareMap.get(DcMotorEx::class.java, "wobblePivot")}
    private var firstTime = true


    override fun periodic() {
        if (firstTime) {
            pivot.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
            pivot.direction = DcMotorSimple.Direction.REVERSE

            pivot.targetPositionTolerance = 20

            if (reset) {
                resetEncoder()
            }

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
        HALF_UP((90*16)/20, 1.0),
        DOWN((180*16)/20, 0.6)
    }

    fun setPivot(position: PivotPosition) {
        pivot.targetPosition = position.targetPosition
        pivot.power = position.power
    }

    fun pivotPower(power: Double) {
        pivot.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        pivot.power = power
    }

    fun resetEncoder() {
        pivot.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        pivot.targetPosition = 0
        pivot.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    fun isBusy() = pivot.isBusy
}

