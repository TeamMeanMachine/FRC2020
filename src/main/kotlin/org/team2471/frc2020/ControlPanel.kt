package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem

object ControlPanel : Subsystem("Control Panel") {
    val controlMotor = MotorController(SparkMaxID(Sparks.CONTROL_PANEL))

    suspend fun test() {
        periodic {
            controlMotor.setPercentOutput(OI.driverController.leftThumbstickX)
        }
    }
}