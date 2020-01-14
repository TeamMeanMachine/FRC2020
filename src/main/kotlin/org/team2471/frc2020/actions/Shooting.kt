package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.Limelight
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import org.team2471.frc2020.Shooter.rpm

suspend fun shoot() = use (Shooter) {
    try {
        val setpoint = 3500.0
        rpm = setpoint

        periodic {
            if(Math.abs(rpm - setpoint) < 500.0 && Limelight.hasValidTarget) {
                OI.driverController.rumble = 0.5
            }
            if(OI.driverController.rightBumper) {
                this.stop()
            }
        }
    }
    finally {
        OI.driverController.rumble = 0.0
    }
}