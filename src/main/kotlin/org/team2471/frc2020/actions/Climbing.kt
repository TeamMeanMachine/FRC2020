package org.team2471.frc2020.actions

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.Intake
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import kotlin.math.absoluteValue

suspend fun climb() = use(Intake, EndGame, Shooter) {
    try {
        parallel ({
            if (ControlPanel.isExtending) {
                ControlPanel.isExtending = false
            }
        }, {
            Intake.extend = true
            delay(0.5)
        })
        EndGame.climbIsExtending = true
        periodic {
            EndGame.brakeIsOn = OI.operatorLeftY.absoluteValue < 0.1
            Shooter.setPower(OI.operatorLeftY * -0.5)
            EndGame.setPower(OI.operatorRightX)
        }
    } finally {
        EndGame.brakeIsOn = true
        EndGame.climbIsExtending = false
        Shooter.setPower(0.0)
        EndGame.setPower(0.0)
        withContext(NonCancellable) {
            delay(0.5)
        }
        Intake.extend = false
    }
}