package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.OI

suspend fun ControlPanel.soleniodTest() = use(this) {
    try {
        isExtending = false
        periodic {
            if (OI.driverController.a) {
                isExtending = false
            }
            if (OI.driverController.b) {
                isExtending = true
            }
            println(isExtending)
        }
    } finally {
        isExtending = false
    }

}

suspend fun ControlPanel.motorTest() = use(this) {
    try {
        setPower(0.2)
        delay(5.0)
        setPower(0.0)
    } finally {
        setPower(0.0)
    }

}