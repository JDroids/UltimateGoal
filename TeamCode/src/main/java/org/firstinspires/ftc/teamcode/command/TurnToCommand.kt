package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive

class TurnToCommand(private val drive: SampleMecanumDrive, private val angle: Double) : CommandBase() {
    override fun initialize() {
        drive.turnToAsync(angle)
    }

    override fun isFinished() = !drive.isBusy
}