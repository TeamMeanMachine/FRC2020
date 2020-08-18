package org.team2471.frc2020

import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem

object Tester: Subsystem("Tester") {
    private val testMotor = MotorController(FalconID(Falcons.TESTER))

    override suspend fun default() {
        periodic {
            testMotor.setPercentOutput(OI.operatorLeftY)
        }
    }
}