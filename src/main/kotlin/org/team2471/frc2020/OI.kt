package org.team2471.frc2020

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign
import org.team2471.frc2020.actions.climb
import org.team2471.frc2020.actions.controlPanel1
import org.team2471.frc2020.actions.intake
import org.team2471.frc2020.actions.shootMode

//import org.team2471.frc2020.actions.intake
//import org.team2471.frc2020.actions.teleopPrepShot
//import org.team2471.frc2020.actions.shoot

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val deadBandDriver = 0.1
    private val deadBandOperator = 0.1


    private val driveTranslationX: Double
        get() = driverController.leftThumbstickX.deadband(deadBandDriver).squareWithSign()

    private val driveTranslationY: Double
        get() = -driverController.leftThumbstickY.deadband(deadBandDriver).squareWithSign()

    val driveTranslation: Vector2
        get() = Vector2(driveTranslationX, driveTranslationY) //does owen want this cubed?

    val driveRotation: Double
        get() = (driverController.rightThumbstickX.deadband(deadBandDriver)).cube() * 0.5 //changed from 0.6

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
        get() = operatorController.rightThumbstickX.deadband(0.2)

    val operatorRightY: Double
        get() = operatorController.rightThumbstickY.deadband(0.2)

    init {
        driverController::back.whenTrue { Drive.zeroGyro() }
        driverController::leftBumper.whenTrue { shootMode() }
//        ({driverController.leftTrigger > 0.1}).whileTrue { shootMode() }
        driverController::rightBumper.toggleWhenTrue { intake() }
        driverController::a.whenTrue { Limelight.pipeline = 1.0 }
        driverController::b.whenTrue { Limelight.pipeline = 0.0 }
        operatorController::rightBumper.toggleWhenTrue { climb() }
        operatorController::leftBumper.toggleWhenTrue { controlPanel1() }
//        driverController::x.whenTrue { triggerTest() }
    }

//    suspend fun triggerTest() {
//        println("Got into triggerTest. Hi.")
//        suspendUntil{driverController.leftTrigger > 0.1}
//        println("Driver left trigger pressed. Hi.")
//        shootMode()
//    }
}

