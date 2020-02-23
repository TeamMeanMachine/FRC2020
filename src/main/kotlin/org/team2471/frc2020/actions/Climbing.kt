package org.team2471.frc2020.actions

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.*
import javax.naming.ldap.Control
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
            EndGame.brakeIsExtending = OI.operatorLeftY.absoluteValue < 0.1
            Shooter.setPower(OI.operatorLeftY * -0.5)
            EndGame.setPower(OI.operatorLeftX * 0.2)
        }
    } finally {
        EndGame.brakeIsExtending = true
        EndGame.climbIsExtending = false
        EndGame.setPower(0.0)
        withContext(NonCancellable) {
            delay(0.5)
        }
        Intake.extend = false
    }
}