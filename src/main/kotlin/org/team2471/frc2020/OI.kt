package org.team2471.frc2020

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign
import org.team2471.frc.lib.units.degrees
import org.team2471.frc2020.AutoChooser
import org.team2471.frc2020.Feeder.reverseFeeder
import org.team2471.frc2020.actions.climb
import org.team2471.frc2020.actions.controlPanel1
import org.team2471.frc2020.actions.intake
import org.team2471.frc2020.actions.shootingMode
import kotlin.math.roundToInt

//import org.team2471.frc2020.actions.intake
//import org.team2471.frc2020.actions.teleopPrepShot
//import org.team2471.frc2020.actions.shoot

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val deadBandDriver = 0.1
    private val deadBandOperator = 0.1


    private val driveTranslationX: Double
        get() = driverController.leftThumbstickX.deadband(deadBandDriver)//.squareWithSign()

    private val driveTranslationY: Double
        get() = -driverController.leftThumbstickY.deadband(deadBandDriver)//.squareWithSign()

    val driveTranslation: Vector2
        get() = Vector2(driveTranslationX, driveTranslationY)

    val driveRotation: Double
        get() = (driverController.rightThumbstickX.deadband(deadBandDriver)).cube() // * 0.6

    val driveLeftTrigger: Double
        get() = driverController.leftTrigger

    val driveRightTrigger: Double
        get() = driverController.rightTrigger

    val operatorLeftTrigger: Double
        get() = operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(0.2)

    val operatorLeftX: Double
        get() = operatorController.leftThumbstickX.deadband(0.2)

    val operatorRightTrigger: Double
        get() = operatorController.rightTrigger

    val operatorRightX: Double
        get() = operatorController.rightThumbstickX.deadband(0.25)

    val operatorRightY: Double
        get() = operatorController.rightThumbstickY.deadband(0.25)

    init {
        //Driver: Owen
        driverController::back.whenTrue { Drive.zeroGyro() }
        driverController::leftBumper.whenTrue {
            println("Driver left bumper pressed. Hi.")
            shootingMode() }
//        ({driverController.leftTrigger > 0.1}).whileTrue { shootMode() }
        driverController::rightBumper.toggleWhenTrue { intake() }
        ({driverController.dPad==Controller.Direction.UP}).whenTrue {
            println("dPad pressed. Heading before: ${Drive.heading.asDegrees.roundToInt()} Heading Setpoint before: ${Drive.
                headingSetpoint.asDegrees.roundToInt()}")
            Drive.headingSetpoint = 0.0.degrees
        }
        driverController::a.whenTrue { AutoChooser.yeeterToFeeder() } //no path yet
        driverController::b.whenTrue { AutoChooser.feederToYeeter() }
//        driverController::start.whenTrue {
//            Drive.disable()
//            Drive.resetDriveMotors()
//            Drive.resetSteeringMotors()
//            Drive.modules = Drive.origModules
//            Drive.enable()
//            Drive.initializeSteeringMotors()
//        }
//        driverController::a.whenTrue { FrontLimelight.pipeline = 1.0 }
//        driverController::b.whenTrue { FrontLimelight.pipeline = 0.0 }

        //Operator: Justine
        operatorController::rightBumper.toggleWhenTrue { climb() }
//        operatorController::leftBumper.toggleWhenTrue { controlPanel1() }
        ({ driverController.leftTrigger > 0.1 }).whileTrue { feederStationVision() }
        //        ({ driverController.rightTrigger > 0.1 }).whileTrue { reverseFeeder() }
//        operatorController::back.toggleWhenTrue { Drive.initializeSteeringMotors() }
    }
}
