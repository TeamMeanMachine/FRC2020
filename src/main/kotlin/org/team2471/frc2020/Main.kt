@file:JvmName("Main")

package org.team2471.frc2020

import edu.wpi.first.wpilibj.RobotBase
import org.team2471.frc2020.testing.driveTests
import org.team2471.frc2020.testing.steeringTests
import org.team2471.frc.lib.framework.MeanlibRobot
import org.team2471.frc.lib.units.degrees

//val PDP = PowerDistributionPanel()

object Robot : MeanlibRobot() {

    init {
        Drive.zeroGyro()
        Drive.heading = 0.0.degrees
    }

    override suspend fun enable() {
        Drive.enable()
        ControlPanel.enable()
        Drive.zeroGyro()
        Limelight.enable()
        Drive.initializeSteeringMotors()
        Shooter.enable()
    }

    override suspend fun autonomous() {
        //Drive.zeroGyro()
        AutoChooser.autonomous()
    }

    override suspend fun teleop() {

    }

    override suspend fun test()  {
        //Drive.disable()
        ControlPanel.test()

        Drive.steeringTests()
        Drive.driveTests()
    }

    override suspend fun disable() {
        Drive.disable()
        ControlPanel.disable()
        Shooter.disable()
    }
}

fun main() {
    RobotBase.startRobot { Robot }
}