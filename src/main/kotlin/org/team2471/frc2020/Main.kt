@file:JvmName("Main")

package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.AnalogInput
import edu.wpi.first.wpilibj.DigitalInput
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
        BackLimelight.ledEnabled = false
        FrontLimelight.ledEnabled = true
    }

    override suspend fun enable() {
        println("starting enable")
        Drive.enable()
//        ControlPanel.enable()
//        BackLimelight.enable()
//        FrontLimelight.enable()
//        Drive.initializeSteeringMotors()
        Shooter.enable()
//        Feeder.enable()
        Intake.enable()
//        EndGame.enable()
//        Tester.enable()
        println("Comp Bot = $isCompBotIHateEverything")
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
    }

    override suspend fun test()  {
//        Drive.disable()
//        ControlPanel.test()
//        Drive.steeringTests()
//        Drive.driveTests()
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
        Drive.tuneDrivePositionController()
//        Drive.encoderValueTest()
    }


    override suspend fun disable() {
        Intake.setPower(0.0)
        Intake.extend = false
        BackLimelight.disable()
        FrontLimelight.disable()
        ControlPanel.disable()
        Shooter.disable()
        Feeder.disable()
        Intake.disable()
        EndGame.disable()
        Tester.disable()
//        Drive.encoderValueTest()
        Drive.disable()

        BackLimelight.ledEnabled = false
        FrontLimelight.ledEnabled = false

        Shooter.rpmOffsetEntry.setPersistent()
        FrontLimelight.parallaxThresholdEntry.setPersistent()


//        val table = NetworkTableInstance.getDefault().getTable(Drive.name)
//        val angle1Entry = table.getEntry("Angle 1")
//        val angle2Entry = table.getEntry("Angle 2")
//        val angle3Entry = table.getEntry("Angle 3")
//        val angle4Entry = table.getEntry("Angle 4")
//
//        val analogInput0 = AnalogInput(0)
//        val analogInput1 = AnalogInput(1)
//        val analogInput2 = AnalogInput(2)
//        val analogInput3 = AnalogInput(3)


//        val module0 = (Drive.modules[0] as Drive.Module)
//        val module1 = (Drive.modules[1] as Drive.Module)
//        val module2 = (Drive.modules[2] as Drive.Module)
//        val module3 = (Drive.modules[3] as Drive.Module)

//        periodic {
//            Drive.recordOdometry()

            //println(module0.analogAngle)
//            angle1Entry.setValue(module0.analogAngle.asDegrees)
//            angle2Entry.setValue(module1.analogAngle.asDegrees)
//            angle3Entry.setValue(module2.analogAngle.asDegrees)
//            angle4Entry.setValue(module3.analogAngle.asDegrees)
//            println("hi")
//            angle1Entry.setValue(analogInput0.voltage)
//            angle2Entry.setValue(analogInput1.voltage)
//            angle3Entry.setValue(analogInput2.voltage)
//            angle4Entry.setValue(analogInput3.voltage)
//        }
    }
}

fun main() {
    println("start robot")
    RobotBase.startRobot { Robot }
}