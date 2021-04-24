package org.firstinspires.ftc.teamcode.subsystems

import com.arcrobotics.ftclib.command.SubsystemBase
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime

class Shooter(hardwareMap: HardwareMap) : SubsystemBase() {
    private val shooterMotor
            by lazy { hardwareMap.get(DcMotorEx::class.java, "shooterMotor") }
    private val indexerServo
            by lazy { hardwareMap.get(Servo::class.java, "indexerServo") }

    private var firstTime = true

    private var velocity = 0.0

    private enum class IndexerState {
        RETRACT,
        PUSH
    }

    private var indexerState = IndexerState.RETRACT

    private var timer = ElapsedTime()

    override fun periodic() {
        shooterMotor.velocity = velocity

        when (indexerState) {
            IndexerState.RETRACT -> {
                indexerServo.position = 0.7
            }
            IndexerState.PUSH -> {
                indexerServo.position = 0.9

                if (timer.milliseconds() > 300) {
                    indexerState = IndexerState.RETRACT
                }
            }
        }
    }

    fun shoot() {
        velocity = (5500 / 60.0) * 28
    }

    fun stop() {
        velocity = 0.0
    }

    fun pushRing() {
        indexerState = IndexerState.PUSH
        timer.reset()
    }
}