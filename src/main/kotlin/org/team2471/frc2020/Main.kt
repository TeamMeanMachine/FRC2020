@file:JvmName("Main")

package org.team2471.frc2020

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotBase
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.MeanlibRobot
import org.team2471.frc.lib.units.degrees
import org.team2471.frc2020.testing.*
import java.net.NetworkInterface

//import org.team2471.frc2020.testing.intakeFeedAndShootTest

//import org.team2471.frc2020.testing.solenoidTest

//val PDP = PowerDistributionPanel()

var isCompBotIHateEverything = true

object Robot : MeanlibRobot() {

    init {
        val networkInterfaces =  NetworkInterface.getNetworkInterfaces()
        for (iFace in networkInterfaces) {
                   if (iFace.name == "eth0") {
                   println("NETWORK NAME--->${iFace.name}<----")
                   var macString = ""
                   for (byteVal in iFace.hardwareAddress){
                    macString += String.format("%s", byteVal)
                }
                println("FORMATTED---->$macString<-----")

            isCompBotIHateEverything = (macString != "0-128472587-69")
            println("Comp Bot = $isCompBotIHateEverything")
           }
        }


        // i heard the first string + double concatenations were expensive...
        repeat(25) {
            println("RANDOM NUMBER: ${Math.random()}")
    }
        println("TAKE ME HOOOOOME COUNTRY ROOOOOOOOADS TOOO THE PLAAAAAAACE WHERE I BELOOOOOOOOONG")

        Drive.zeroGyro()
        Drive.heading = 0.0.degrees
        AutoChooser
        FrontLimelight.startUp()
        BackLimelight.startUp()
    }

    override suspend fun enable() {
        println("starting enable")
        Drive.enable()
        ControlPanel.enable()
        BackLimelight.enable()
        FrontLimelight.enable()
        Drive.initializeSteeringMotors()
        Shooter.enable()
        Feeder.enable()
        Intake.enable()
        EndGame.enable()
        println("ending enable")
    }

    override suspend fun autonomous() {
//        Drive.zeroGyro()
        Drive.brakeMode()
        AutoChooser.autonomous()
    }

    override suspend fun teleop() {
        println("telop begin")
        Drive.headingSetpoint = Drive.heading
//        periodic{
//            println("parallax ${FrontLimelight.parallax}")
//        }
    }

    override suspend fun test()  {
//        Drive.disable()
//        ControlPanel.test()
//
        println("ajfslkdjaslk;fjkldsajflkd;sa")
        Drive.steeringTests()
        Drive.driveTests()
//        Feeder.test()
//        Intake.solenoidTest()
//        ControlPanel.motorTest()
//        ControlPanel.soleniodTest()
//        Intake.intakeFeedAndShootTest()
//        EndGame.climbSolenoidTest()
//        EndGame.brakeSolenoidTest()
//        EndGame.climbTest()
//        Shooter.distance2RpmTest()
//        Shooter.countBallsShotTest()
//        Shooter.motorTest()
//        Drive.tuneDrivePositionController()
    }


    override suspend fun disable() {
        Intake.setPower(0.0)
        Intake.extend = false
        Drive.disable()
        BackLimelight.disable()
        FrontLimelight.disable()
        ControlPanel.disable()
        Shooter.disable()
        Feeder.disable()
        Intake.disable()
        EndGame.disable()

        periodic {
            BackLimelight.ledEnabled = false
            FrontLimelight.ledEnabled = false
        }
//        val table = NetworkTableInstance.getDefault().getTable(Drive.name)
//        val xEntry = table.getEntry("X")
//        val yEntry = table.getEntry("Y")
//        periodic {
//            Drive.recordOdometry()
//
//            xEntry.setDouble(Drive.position.x)
//            yEntry.setDouble(Drive.position.y)
//            //println("analog 2 ${Drive.modules[2].angle}" )
//        }
    }
}

fun main() {
    println("start robot")
    RobotBase.startRobot { Robot }
}