package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.subsystems.Shooter

class ShootCommand(private val shooter: Shooter, private val telemetry: Telemetry?=null, private val rpm: Double=5100.0) : CommandBase() {
    override fun initialize() {
        shooter.targetRPM = rpm
        shooter.shoot()
    }

    private var shots = 0

    override fun execute() {
        if (shooter.isReady()) {
            shooter.pushRing()
            shots += 1
        }

        telemetry?.addData("Shots", shots)
        telemetry?.update()
    }

    override fun end(interrupted: Boolean) {
        shooter.stop()
    }

    override fun isFinished() = shots >= 4
}