package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.Feeder
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter

suspend fun Feeder.test() = use(this, Shooter) {
    Shooter.rpm = 3000.0
    var feederPower = 0.0
    periodic {
        feederPower = OI.driveRightTrigger
        setPower(feederPower)
        println(feederPower)
    }
}