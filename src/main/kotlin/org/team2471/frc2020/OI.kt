package org.team2471.frc2020

import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.units.degrees
import org.team2471.frc2020.actions.*

//import org.team2471.frc2020.actions.controlPanel1
import kotlin.math.roundToInt

//import org.team2471.frc2020.actions.intake
//import org.team2471.frc2020.actions.teleopPrepShot
//import org.team2471.frc2020.actions.shoot

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val deadBandDriver = 0.1
    private val deadBandOperator = 0.2


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

    val operatorLeftBumper: Boolean
        get() = operatorController.leftBumper

    val operatorLeftTrigger: Double
        get() = operatorController.leftTrigger.deadband(0.1)

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(deadBandOperator)

    val operatorLeftX: Double
        get() = operatorController.leftThumbstickX.deadband(deadBandOperator)

    val operatorRightTrigger: Double
        get() = operatorController.rightTrigger.deadband(0.1)

    val operatorRightX: Double
        get() = operatorController.rightThumbstickX.deadband(0.25)

    val operatorRightY: Double
        get() = operatorController.rightThumbstickY.deadband(0.25)

    init {
        //Driver: Owen
        driverController::back.whenTrue { Drive.zeroGyro() }
//        driverController::leftBumper.whenTrue { shootingMode() }
        operatorController::leftBumper.whenTrue { shootingMode() }

//        ({driverController.leftTrigger > 0.1}).whileTrue { shootMode() }
        driverController::rightBumper.toggleWhenTrue {
            intakeAction()
        }
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
//        operatorController::leftBumper.toggleWhenTrue { climb2() }
//        operatorController::a.whenTrue { controlPanel1() }
//        operatorController::b.whenTrue { controlPanel2() }
        ({ driverController.leftTrigger > 0.1 }).whileTrue { feederStationVision() }
        //        ({ driverController.rightTrigger > 0.1 }).whileTrue { reverseFeeder() }
//        operatorController::back.toggleWhenTrue { Drive.initializeSteeringMotors() }
    }
}
