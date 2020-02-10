package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.EndGame.brakeIsExtending
import org.team2471.frc2020.Feeder
import org.team2471.frc2020.Intake
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter

suspend fun Intake.motorTest() = use(this) {
    setPower(0.85)
    delay(10.0)
    setPower(0.0)
}

suspend fun Intake.solenoidTest() = use(this) {
    intakeIsExtending = false
    periodic {
        if(OI.driverController.a) {
            intakeIsExtending = false
        }
        if(OI.driverController.b) {
            intakeIsExtending = true
        }
        println(intakeIsExtending)
    }
}

suspend fun Intake.intakeFeedAndShootTest() = use(this, Feeder, Shooter) {
    parallel({
        Feeder.test()
    },{
        Intake.intakeIsExtending = true
        periodic {
            println(brakeIsExtending)
            setPower(OI.driveLeftTrigger)
        }
    })
}