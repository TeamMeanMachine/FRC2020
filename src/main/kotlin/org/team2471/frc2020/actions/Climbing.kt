package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.Intake
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import kotlin.math.absoluteValue

suspend fun climb() = use(Intake, EndGame, Shooter) {
    try {
        Intake.extend = true
        delay(0.3)
        EndGame.climbIsExtending = true
        periodic {
            EndGame.brakeIsExtending = OI.operatorLeftY.absoluteValue < 0.1
            Shooter.setPower(OI.operatorLeftY * -0.5)
            if(OI.operatorController.rightBumper) {
                this.stop()
            }
        }
    } finally {
        EndGame.brakeIsExtending = true
        Intake.extend = false
    }
}