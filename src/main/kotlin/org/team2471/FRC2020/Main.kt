@file:JvmName("Main")

package org.team2471.FRC2020

import edu.wpi.first.wpilibj.*
import org.team2471.FRC2020.testing.driveTests
import org.team2471.FRC2020.testing.steeringTests
import org.team2471.frc.lib.framework.RobotProgram
import org.team2471.frc.lib.framework.initializeWpilib
import org.team2471.frc.lib.framework.runRobotProgram
import org.team2471.frc.lib.units.degrees

//val PDP = PowerDistributionPanel()

object Robot : RobotProgram {

    init {
        Drive.zeroGyro()
        Drive.heading = 0.0.degrees

        // i heard the first string + double concatenations were expensive...
        repeat(25) {
            println("RANDOM NUMBER: ${Math.random()}")
        }
        println("TAKE ME HOOOOOME COUNTRY ROOOOOOOOADS TOOO THE PLAAAAAAACE WHERE I BELOOOOOOOOONG")
        //I swear there was a good reason for this but i honestly have no idea what that was
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
//        ControlPanel.test()

//        Drive.steeringTests()
//        Drive.driveTests()
    }

    override suspend fun disable() {
        Drive.disable()
        ControlPanel.disable()
        Shooter.disable()
    }
}

fun main() {
    initializeWpilib()
    Drive
    OI
    AutoChooser
    ControlPanel
    Shooter
    runRobotProgram(Robot)
}