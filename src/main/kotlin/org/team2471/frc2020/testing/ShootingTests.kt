package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.Feeder
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import org.team2471.frc2020.Shooter.rpm

suspend fun Shooter.distance2RpmTest() = use(this, Feeder){
    periodic {
        rpm = rpmSetpointEntry.getDouble(0.0)
        Feeder.setPower(OI.driveRightTrigger )
    }

}

suspend fun Shooter.seeRpmDipsTest() = use(this, Feeder) {
    var rpmSetpoint = 4100.0
    rpm = rpmSetpoint
    periodic {
        if(rpmSetpoint - rpm < 100 && OI.driveRightTrigger > 0.1) {
            Feeder.setPower(Feeder.FEED_POWER)
        } else {
            Feeder.setPower(0.0)
        }
    }
}