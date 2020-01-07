package org.team2471.FRC2020.testing

import org.team2471.FRC2020.OI
import org.team2471.FRC2020.Slurpy
import org.team2471.frc.lib.coroutines.periodic

suspend fun Slurpy.shoulderTest() {
    periodic {
       //Slurpy.shoulderMotor.setPositionSetpoint(OI.driverController.rightThumbstickX * 60.0)
        println("Error: ${Slurpy.shoulderAngle} ")

    }
}
suspend fun Slurpy.wristTest() {
    periodic {
        //Slurpy.wristMotor.setPositionSetpoint(OI.driverController.rightThumbstickY * 30)
        println("Error: ${Slurpy.wristAngle } ")

    }
}
