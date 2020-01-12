package org.team2471.frc2020

import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign

private val deadBandDriver = 0.1
private val deadBandOperator = 0.1

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val driveTranslationX: Double
        get() = driverController.leftThumbstickX.deadband(deadBandDriver).squareWithSign()

    private val driveTranslationY: Double
        get() = -driverController.leftThumbstickY.deadband(deadBandDriver).squareWithSign()

    val driveTranslation: Vector2
        get() = Vector2(driveTranslationX, driveTranslationY) //does owen want this cubed?

    val driveRotation: Double
        get() = (driverController.rightThumbstickX.deadband(deadBandDriver)).cube() * 0.5 //changed from 0.6

    val driveLeftTrigger: Double
        get() = 0.0 //driverController.leftTrigger

    val driveRightTrigger: Double
        get() = driverController.rightTrigger

    val operatorLeftTrigger: Double
        get() = 0.0 //operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(0.2)

    val operatorRightTrigger: Double
        get() = 0.0 //operatorController.rightTrigger

    init {
        driverController::back.whenTrue { Drive.zeroGyro() }
    }
}

