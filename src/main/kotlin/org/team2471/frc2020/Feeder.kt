package org.team2471.frc2020

import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.framework.Subsystem

object Feeder: Subsystem("Feeder") {
 private val feederMotor = MotorController(FalconID(Falcons.FEEDER))

 val FEED_POWER = 1.0

    init {
        feederMotor.config {
            inverted(true)
        }
    }

    fun setPower(power: Double) {
        feederMotor.setPercentOutput(power)
    }

//    override suspend fun default() {
//    }
}