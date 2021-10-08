package org.team2471.frc2020.actions

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.units.asFeet
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.*
import org.team2471.frc2020.FrontLimelight.aimError
import kotlin.math.abs
import kotlin.math.absoluteValue

suspend fun shootingMode(ballsIntaken: Int = 5) = use(Drive, Shooter, FrontLimelight, Intake, Feeder) {
    try {
        println("Got in shootingMode function. Hi.")
        val isAuto = DriverStation.getInstance().isAutonomous
        Intake.setPower(0.0)
        Shooter.prepShotOn = true
        Intake.extend = isCompBotIHateEverything
        Intake.setPower(Intake.INTAKE_POWER)
        val totalT = Timer()
        totalT.start()
        var t = totalT.get()
        FrontLimelight.ledEnabled = true
        var currTime = totalT.get() - t
        var turn = 0.0
        periodic {
            Shooter.rpm = Shooter.rpmSetpoint
            Shooter.hoodSetpoint = if (FrontLimelight.hasValidTarget) Shooter.hoodCurve.getValue(FrontLimelight.distance.asFeet) else Drive.position.length
//            Shooter.hoodSetpoint = Shooter.hoodSetpointEntry.getDouble(50.0)
            if (abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.5) {
                currTime = totalT.get() - t
                if (!isAuto && currTime > 0.1) {
                    OI.operatorController.rumble = 0.5
                }
                if(isAuto && currTime > 2.0) { //currTime > 0.1 is how it would be if we didn't increase it for the hood to go up
                    this.stop()
                }
            } else {
                t = totalT.get()
                if(!isAuto) {
                    OI.operatorController.rumble = 0.0
                }
            }
            if(isAuto) {
                if(totalT.get() > 1.5) this.stop()
            } else {
                if (OI.driverController.rightTrigger > 0.1) {
                    Feeder.setPower(OI.driveRightTrigger * -0.70)
                    Intake.setPower(OI.driveRightTrigger * 0.70)
                    Intake.extend = false
                } else if (OI.operatorController.rightTrigger > 0.1) {
                    Feeder.setPower(OI.operatorRightTrigger * 0.60)
                    Intake.setPower(OI.operatorRightTrigger * 0.80)
                    Intake.extend = false
                } else {
                    Feeder.setPower(0.0)
                    Intake.setPower(0.0)
                    Intake.extend = isCompBotIHateEverything
                }
                if (!OI.operatorController.leftBumper) {
                    this.stop()
                }
            }
            turn = 0.0
            if (OI.driveRotation.absoluteValue > 0.001 && !isAuto) {
                turn = OI.driveRotation
            } else if (FrontLimelight.hasValidTarget) {
                turn = Drive.aimPDController.update(FrontLimelight.aimError)
            }
//            printEncoderValues()
            if(!isAuto) {
                val direction = OI.driverController.povDirection
                if (direction != -1.0.degrees) Drive.headingSetpoint = direction
            }
            Drive.drive(
                if(isAuto) Vector2(0.0,0.0) else OI.driveTranslation,
                turn,
                !isAuto
            )
        }
        if(isAuto) {
            Feeder.setPower(Feeder.FEED_POWER)
            Intake.extend = false
            var ballsShot = 0
            var shootingBall = false
            periodic(0.015) {
                Shooter.rpm = Shooter.rpmSetpoint

                //trying to count balls shot by comparing rpm to offset
//                if(!shootingBall && Shooter.rpm < 0.93 * Shooter.rpmSetpoint) {
//                    ballsShot++
//                    shootingBall = true
//                }
//                if(shootingBall && abs(Shooter.rpmSetpoint - Shooter.rpm) < 0.05 * Shooter.rpmSetpoint) {
//                    shootingBall = false
//                }
//                if(ballsShot > ballsIntaken - 1 || totalT.get() > 3.5) {
//                    this.stop()
//                }

                if (totalT.get() > 7.0) {
                    this.stop()
                }

                Drive.drive(
                Vector2(0.0,0.0),
                0.0
            )
            }
        }
    } finally {
        val isAuto = DriverStation.getInstance().isAutonomous
        OI.operatorController.rumble = 0.0
        Shooter.prepShotOn = false
        Feeder.setPower(0.0)
        Intake.extend = false
        if (FrontLimelight.hasValidTarget) {
            val alpha = 0.5
            Drive.position = Drive.position * alpha + FrontLimelight.position * (1.0-alpha)
            println("Reset odometry to include limelight. Hi.")
        }
        FrontLimelight.ledEnabled = false
        if(!isAuto) {
            Shooter.setPower(0.0)
            Intake.setPower(0.0)
        }
    }
}
