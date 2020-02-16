package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.Intake
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter

suspend fun EndGame.climbSolenoidTest() = use(EndGame, Shooter, Intake) {
    EndGame.brakeIsExtending = false
    Intake.extend = true
    delay(2.0)
    Shooter.setPower(0.25)
    delay(1.0)
    climbIsExtending = true
    delay(10.0)
    Shooter.setPower(-0.25)
}

suspend fun EndGame.brakeSolenoidTest() = use(EndGame) {
    EndGame.brakeIsExtending = true
    periodic {
        println(brakeIsExtending)
    }
}

suspend fun EndGame.climbTest() = use(EndGame) {
    EndGame.brakeIsExtending = false
    Intake.extend = true
    climbIsExtending = true
    periodic {
        Shooter.setPower(OI.operatorLeftY * 0.5)
        brakeIsExtending = Math.abs(OI.operatorLeftY) < 0.1
    }
}