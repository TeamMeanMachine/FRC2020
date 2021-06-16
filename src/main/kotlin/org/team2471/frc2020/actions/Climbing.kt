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

suspend fun climb() = use(EndGame) {
    try {
        periodic {
            if (OI.operatorLeftTrigger > 0.1) {
                EndGame.brakeIsOn = false

                EndGame.setLeftPower(OI.operatorLeftTrigger * 0.6)
                EndGame.setRightPower(OI.operatorLeftTrigger * 0.6)
            } else if (OI.operatorRightTrigger > 0.1) {
                EndGame.brakeIsOn = false

                EndGame.setLeftPower(OI.operatorRightTrigger * -0.6)
                EndGame.setRightPower(OI.operatorRightTrigger * -0.6)
            } else {
                EndGame.setLeftPower(0.0)
                EndGame.setRightPower(0.0)

                EndGame.brakeIsOn = true
            }
        }

    } finally {
        EndGame.brakeIsOn = true
        EndGame.setLeftPower(0.0)
        EndGame.setRightPower(0.0)
    }
}