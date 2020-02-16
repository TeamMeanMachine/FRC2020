package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.Intake
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter

suspend fun climb() = use(Intake, EndGame, Shooter) {
    try {
        Intake.extend = true
        delay(0.1)
        EndGame.climbIsExtending = true
        periodic {
            Shooter.setPower(OI.operatorLeftY * 0.5)
            if(OI.driverController.rightBumper) {
                this.stop()
            }
        }
    } finally {
        EndGame.brakeIsExtending = true
        Intake.extend = false
    }
}