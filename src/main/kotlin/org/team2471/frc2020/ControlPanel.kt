package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use

object ControlPanel : Subsystem("Control Panel") {
    val controlMotor = MotorController(SparkMaxID(Sparks.CONTROL_PANEL))

    suspend fun test() = use(ControlPanel) {
        periodic {
            controlMotor.setPercentOutput(OI.driverController.leftTrigger)
        }
    }
    fun spin(power: Double) {
        controlMotor.setPercentOutput(power)
    }

    override suspend fun default() {
        periodic {
            spin(OI.driveLeftTrigger)
            //setRPM(1000.0)
            // setRPM(rpmTable.getDouble(0.0))
        }
    }
}