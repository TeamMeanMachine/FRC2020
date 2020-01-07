package org.team2471.BunnyBots2019.testing

import org.team2471.BunnyBots2019.OI
import org.team2471.BunnyBots2019.Slurpy
import org.team2471.BunnyBots2019.Slurpy.cubeIntakeMotor
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic

suspend fun Slurpy.cubeIntakeTest() {
    periodic {
        cubeIntakeMotor.setPercentOutput(OI.driverController.leftTrigger - OI.driverController.rightTrigger)
        println("Error: ${Slurpy.shoulderAngleError}")
    }
}