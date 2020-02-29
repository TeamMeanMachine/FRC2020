package org.team2471.frc2020

import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use

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

    suspend fun reverseFeeder() = use(Feeder){
        try {
//            println("Got into reverseFeeder. Hi.")
            periodic {
                setPower(-OI.operatorController.rightTrigger)
            }
        } finally {
            setPower(0.0)
        }
    }

    override suspend fun default() {
        periodic {
            setPower(OI.operatorRightTrigger * -0.7)
        }
    }
}