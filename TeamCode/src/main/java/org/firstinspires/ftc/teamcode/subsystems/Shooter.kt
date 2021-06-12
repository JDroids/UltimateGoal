package org.firstinspires.ftc.teamcode.subsystems

import com.acmerobotics.dashboard.FtcDashboard
import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystems.ShooterConstants.*
import kotlin.math.abs

class Shooter(hardwareMap: HardwareMap) : SubsystemBase() {
    private val shooterMotor
            by lazy { hardwareMap.get(DcMotorEx::class.java, "shooterMotor") }
    private val indexerServo
            by lazy { hardwareMap.get(Servo::class.java, "indexerServo") }

    private var firstTime = true

    private var targetVelocity = 0.0

    private enum class IndexerState {
        RETRACT,
        PUSH
    }

    private var indexerState = IndexerState.RETRACT

    private var timer = ElapsedTime()

    override fun periodic() {
        shooterMotor.velocity = targetVelocity
        shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, PIDFCoefficients(WHEEL_P, WHEEL_I, WHEEL_D, WHEEL_F))

        when (indexerState) {
            IndexerState.RETRACT -> {
                indexerServo.position = 0.46
            }
            IndexerState.PUSH -> {
                indexerServo.position = 0.76

                if (timer.milliseconds() > 400) {
                    indexerState = IndexerState.RETRACT
                }
            }
        }

        FtcDashboard.getInstance().telemetry.addData("Target TPS", targetRPM / 60 * 28)
        FtcDashboard.getInstance().telemetry.addData("Current TPS", shooterMotor.velocity)
        FtcDashboard.getInstance().telemetry.update()
    }

    fun shoot() {
        targetVelocity = rpmToTPS(targetRPM)
    }

    fun stop() {
        targetVelocity = 0.0
    }

    fun pushRing() {
        indexerState = IndexerState.PUSH
        timer.reset()
    }

    private fun rpmToTPS(rpm: Double) = (rpm / 60) * 28
    private fun tpsToRPM(tps: Double) = (tps * 60.0) / 28

    var targetRPM = 5100.0

    fun isReady() = abs(tpsToRPM(shooterMotor.velocity) - targetRPM) <= 50.0   // is within 50 rpm
            && indexerState == IndexerState.RETRACT
}