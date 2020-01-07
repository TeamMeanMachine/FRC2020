@file:JvmName("Main")

package org.team2471.BunnyBots2019

import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.*
import org.team2471.BunnyBots2019.testing.*
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.RobotProgram
import org.team2471.frc.lib.framework.initializeWpilib
import org.team2471.frc.lib.framework.runRobotProgram
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.units.degrees

val PDP = PowerDistributionPanel()

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
        Slurpy.shoulderMotor.stop()
        Slurpy.wristMotor.stop()
        Drive.enable()
        Drive.zeroGyro()
        Limelight.enable()
        Drive.initializeSteeringMotors()
        Slurpy.enable()
        Bintake.enable()
        //periodic {
            //println("Arm= ${round(Slurpy.shoulderAngle.asDegrees,2)}  Arm= ${round(Slurpy.wristAngle.asDegrees,2)}")
        //}
    }

    override suspend fun autonomous() {
        //Drive.zeroGyro()
        Slurpy.resetShoulderAngle()

        AutoChooser.autonomous()

    }

    override suspend fun teleop() {
        Bintake.animateToPose(BintakePose.SPITTING_POSE)
        Bintake.intakeMotor.setPercentOutput(1.0)
        delay(0.25)
        Bintake.intakeMotor.setPercentOutput(0.0)
        Bintake.animateToPose(BintakePose.SAFETY_POSE)
        Slurpy.prepareSlurpy()
    }

    override suspend fun test()  {
        Drive.disable()

//        var testShooterMotor = CANSparkMax( 9, CANSparkMaxLowLevel.MotorType.kBrushless)
//        testShooterMotor.restoreFactoryDefaults()
//        var pidController = testShooterMotor.pidController
//        var testEncoder = testShooterMotor.encoder
//        with(pidController){
//            setP(0.00005)
//            setI(0.0)
//            setD(0.0)
//            setIZone(0.0)
//            setFF(0.0002)
//            setOutputRange(-1.0, 1.0)
//        }


        val shooterVelocity = 1000.0 //NetworkTableInstance.getDefault().getTable("ShooterTestTable").getEntry("TestVelocity").getDouble(0.0)
        Slurpy.cubeIntakeMotor.setVelocitySetpoint(shooterVelocity)
//        pidController.setReference(shooterVelocity, ControlType.kVelocity)

//        Drive.initializeSteeringMotors()
//        Drive.steeringTests()
//        Drive.driveTests()
//        periodic {
//            println("Angle= ${round(Drive.modules[2].angle.asDegrees,2)}, Analog= ${round((Drive.modules[2] as Drive.Module).analogAngle.asDegrees, 2)}")
//        }
//        Slurpy.shoulderTest()
//        Slurpy.wristTest()
//        Bintake.pivotTest()
    }

    override suspend fun disable() {
        Drive.disable()
        periodic {
//            println("Angle= ${round(Drive.modules[2].angle.asDegrees,2)}, Analog= ${round((Drive.modules[2] as Drive.Module).analogAngle.asDegrees, 2)}")
//            println("Arm= ${round(Slurpy.shoulderAngle.asDegrees,2)}  Wrist= ${round(Slurpy.wristAngle.asDegrees,2)}")
//            println("Bintake Angle = ${round(Bintake.angle.asDegrees,2)}")

        }
        Slurpy.disable()
        Bintake.disable()
    }
}

fun main() {
    initializeWpilib()
    Drive
    OI
    Slurpy
    AutoChooser
    runRobotProgram(Robot)
}