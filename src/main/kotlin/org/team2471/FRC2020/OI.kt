package org.team2471.FRC2020

import org.team2471.FRC2020.Bintake.animateToPose
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign
import org.team2471.frc.lib.units.degrees

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
        get() = 0.0 //driverController.rightTrigger

    val operatorLeftTrigger: Double
        get() = 0.0 //operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(0.2)

    val operatorRightTrigger: Double
        get() = 0.0 //operatorController.rightTrigger

    init {
        driverController::back.whenTrue { Drive.zeroGyro() }
//        driverController::b.whenTrue {
//            println("YEET")
//            for (module in 0..3) {
//                val module = (Drive.modules[module] as Drive.Module)
//                module.turnMotor.setRawOffset(0.0.degrees)
//                module.driveMotor.setRawOffset(0.0.degrees)

//            }
//        }

        driverController::rightBumper.whenTrue {
            if (Slurpy.slurpyCurrentPose == SlurpyPose.SAFETY_POSE) {
                Slurpy.intakeCube()
            }
        }
        operatorController::rightBumper.whenTrue {
            Bintake.intakeBinCubes()
        }
        operatorController::leftBumper.whenTrue {
            Slurpy.stealCube()
        }
//        driverController::a.whenTrue {
//            animateToPose(SlurpyPose.START_POSE)
//        }
//
//        driverController::b.whenTrue {
//            animateToPose(SlurpyPose.SCORING_POSE)
//        }
        driverController::x.whenTrue {
            Slurpy.animateToPose(SlurpyPose.SAFETY_POSE)
        }
//        driverController::y.whenTrue {
//            animateToPose(SlurpyPose.GROUND_POSE)
//        }
//        operatorController::a.whenTrue {
//            animateToPose(BintakePose.SAFETY_POSE)
//        }
//        operatorController::b.whenTrue {
//            animateToPose(BintakePose.INTAKE_POSE)
//        }
//        operatorController::x.whenTrue {
//            animateToPose(BintakePose.SCORING_POSE)
//        }
//        operatorController::y.whenTrue {
//            animateToPose(BintakePose.SPITTING_POSE)
//        }
    }
}

