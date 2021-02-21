package org.firstinspires.ftc.teamcode

import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.Subsystem

val (() -> Unit).runnable
        get() = Runnable { this() }


fun Subsystem.instant(thingToRun: () -> Unit) =
        InstantCommand(thingToRun.runnable, this)