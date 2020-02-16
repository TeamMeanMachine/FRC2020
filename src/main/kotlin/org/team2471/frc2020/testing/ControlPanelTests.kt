package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.OI

suspend fun ControlPanel.soleniodTest() = use(this) {
    isExtending = false
    periodic {
        if(OI.driverController.a) {
            isExtending = false
        }
        if(OI.driverController.b) {
            isExtending = true
        }
        println(isExtending)
    }
}

suspend fun ControlPanel.motorTest() = use(this) {
    setPower(0.2)
    delay(5.0)
    setPower(0.0)
}