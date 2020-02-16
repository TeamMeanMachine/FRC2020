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
    extend = false
    periodic {
        if(OI.driverController.a) {
            extend = false
        }
        if(OI.driverController.b) {
            extend = true
        }
        println(extend)
    }
}

suspend fun Intake.intakeFeedAndShootTest() = use(this, Feeder, Shooter) {
    parallel({
        Feeder.test()
    },{
        Intake.extend = true
        periodic {
            println(brakeIsExtending)
            setPower(OI.driveLeftTrigger)
        }
    })
}