package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

class SetWobblePivotPosition(private val wobbleClaw: WobbleClaw,
                             private val pivotPosition: WobbleClaw.PivotPosition) : CommandBase() {
    private val timer = ElapsedTime()

    override fun initialize() {
        wobbleClaw.setPivot(pivotPosition)
        timer.reset()
    }

    override fun isFinished() = !wobbleClaw.isBusy() || timer.seconds() > 1
}