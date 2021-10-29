@file:JvmName("Main")

package org.team2471.frc2020




//import FRC____.BuildConfig
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.AnalogInput
import edu.wpi.first.wpilibj.RobotBase
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.MeanlibRobot
import org.team2471.frc.lib.units.degrees
import org.team2471.frc2020.testing.*
import java.net.NetworkInterface
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//import org.team2471.frc2020.testing.intakeFeedAndShootTest

//import org.team2471.frc2020.testing.solenoidTest

//val PDP = PowerDistributionPanel()

var isCompBotIHateEverything = true

const val date = 5/20/2021
object Robot : MeanlibRobot() {

    init {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        for (iFace in networkInterfaces) {
            if (iFace.name == "eth0") {
                println("NETWORK NAME--->${iFace.name}<----")
                var macString = ""
                for (byteVal in iFace.hardwareAddress) {
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

//        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//        var instant = Instant.ofEpochMilli(BuildConfig.BUILD_TIME)
//        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
//        println(formatter.format(date))
//        println((System.currentTimeMillis() - BuildConfig.BUILD_TIME) / 1000)
//        val deltaSeconds = (System.currentTimeMillis() - BuildConfig.BUILD_TIME) / 1000
//        val isBuildFresh = deltaSeconds < 100
        val table = NetworkTableInstance.getDefault().getTable("SmartDashboard")
        val freshBuildEntry = table.getEntry("Fresh Build")
//        freshBuildEntry.setBoolean(isBuildFresh)
//
        Drive.zeroGyro()
        Drive.heading = 0.0.degrees
        AutoChooser
        FrontLimelight.startUp()
        BackLimelight.startUp()
        BackLimelight.ledEnabled = false
        FrontLimelight.ledEnabled = false
    }

    override suspend fun enable() {
        println("starting enable")
        Drive.enable()
//        ControlPanel.enable()
        BackLimelight.enable()
        FrontLimelight.enable()
//        Drive.initializeSteeringMotors()
        Shooter.enable()
        Feeder.enable()
        Intake.enable()
//        EndGame.enable()
//        Tester.enable()
        println("Comp Bot = $isCompBotIHateEverything")
        Shooter.resetHoodEncoder()
        //bean
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
//
//        val table = NetworkTableInstance.getDefault().getTable(Drive.name)
//
//        val angle1Entry = table.getEntry("Angle 1")
//        val angle2Entry = table.getEntry("Angle 2")
//        val angle3Entry = table.getEntry("Angle 3")
//        val angle4Entry = table.getEntry("Angle 4")
//
//        val module0 = (Drive.modules[0] as Drive.Module)
//        val module1 = (Drive.modules[1] as Drive.Module)
//        val module2 = (Drive.modules[2] as Drive.Module)
//        val module3 = (Drive.modules[3] as Drive.Module)
//
//
//        periodic {
////            Drive.recordOdometry()
//
//            //println(module0.analogAngle)
////            println("kjaflds;jfklda;sjflk;adsjkl")
////            println(module0.analogAngle.asDegrees)
//            angle1Entry.setValue(module0.analogAngle.asDegrees)
//            angle2Entry.setValue(module1.analogAngle.asDegrees)
//            angle3Entry.setValue(module2.analogAngle.asDegrees)
//            angle4Entry.setValue(module3.analogAngle.asDegrees)
//        }
//        }
    }

    override suspend fun test() {
//        Drive.steeringTests()
//        periodic {
//            Drive.printEncoderValues()
//        }
    }


    override suspend fun disable() {
//        Intake.setPower(0.0)
//        Intake.extend = false
        Drive.disable()
        BackLimelight.disable()
        FrontLimelight.disable()
//        ControlPanel.disable()
        Feeder.disable()
        Intake.disable()
//        EndGame.disable()
//        Tester.disable()
        Shooter.disable()
        //bean

        BackLimelight.ledEnabled = false
        FrontLimelight.ledEnabled = false
// bean
//        Shooter.rpmOffsetEntry.setPersistent()
        FrontLimelight.parallaxThresholdEntry.setPersistent()
        OI.driverController.rumble = 0.0

        var analogInput0 = Drive.modules[0].angle
        var analogInput1 = Drive.modules[1].angle
        var analogInput2 = Drive.modules[2].angle
        var analogInput3 = Drive.modules[3].angle
//        println("Analog 0: $analogInput0;     1: $analogInput1;     2: $analogInput2     3: $analogInput3")

        periodic {
            analogInput0 = Drive.modules[0].angle
            analogInput1 = Drive.modules[1].angle
            analogInput2 = Drive.modules[2].angle
            analogInput3 = Drive.modules[3].angle
            //println("Analog 0: $analogInput0;     1: $analogInput1;     2: $analogInput2     3: $analogInput3")
        }
        println("Disable Done")

        }
//    }

}

fun main() {
    println("start robot")
    RobotBase.startRobot { Robot }
}