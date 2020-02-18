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