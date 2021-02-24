package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime

class SleepCommand(private val seconds: Double) : CommandBase() {
    private val timer = ElapsedTime()

    override fun initialize() {
        timer.reset()
    }

    override fun isFinished() = timer.seconds() > seconds
}