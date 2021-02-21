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

    fun up() {
        pivot.targetPosition = 0
        pivot.power = 1.0
    }

    fun down() {
        pivot.targetPosition = 180
        pivot.power = 0.6
    }

    fun resetEncoder() {
        pivot.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        pivot.targetPosition = 0
        pivot.mode = DcMotor.RunMode.RUN_TO_POSITION
    }
}
