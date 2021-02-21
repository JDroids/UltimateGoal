package org.firstinspires.ftc.teamcode.command

import com.arcrobotics.ftclib.command.CommandBase
import org.firstinspires.ftc.teamcode.subsystems.WobbleClaw

class SetWobblePivotPosition(private val wobbleClaw: WobbleClaw,
                             private val pivotPosition: WobbleClaw.PivotPosition) : CommandBase() {
    override fun initialize() {
        wobbleClaw.setPivot(pivotPosition)
    }

    override fun isFinished() = !wobbleClaw.isBusy()
}