package org.team2471.frc2020.actions

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.Intake
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import kotlin.math.absoluteValue

suspend fun climb() = use(Intake, EndGame, Shooter) {
    if (ControlPanel.isExtending) {
        ControlPanel.isExtending = false
        delay(0.5)
    }
    try {
        Intake.extend = true
        delay(0.3)
        EndGame.climbIsExtending = true
        periodic {
            EndGame.brakeIsExtending = OI.operatorRightY.absoluteValue < 0.1
            Shooter.setPower(OI.operatorRightY * -0.5)
            EndGame.setPower(OI.operatorRightX)
        }
    } finally {
        EndGame.brakeIsExtending = true
        EndGame.climbIsExtending = false
        Shooter.setPower(0.0)
        EndGame.setPower(0.0)
        withContext(NonCancellable) {
            delay(0.5)
        }
        Intake.extend = false
    }
}