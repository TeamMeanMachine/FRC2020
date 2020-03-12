package org.team2471.frc2020.actions

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.*
import org.team2471.frc2020.FrontLimelight.aimError
import kotlin.math.abs
import kotlin.math.absoluteValue

//TODO: Delete commented out areas if the combined function works


//suspend fun shootMode() = use(Shooter, Feeder, Intake, FrontLimelight) {
//    try {
//        Intake.setPower(0.0)
//        Shooter.prepShotOn = true
//        Shooter.rpm = Shooter.rpmSetpoint
//        Intake.setPower(0.0)
//        Intake.extend = true
//        FrontLimelight.ledEnabled = true
//        val t = Timer()
//        t.start()
//        var isUnjamming = false
//        var startTime = 0.0
//        periodic {
//            Shooter.rpm = Shooter.rpmSetpoint
//            val currTime = t.get()
////            println("rpm: ${Shooter.rpm}; rpmSetpoint: ${Shooter.rpmSetpoint}; rpmOffset: ${Shooter.rpmOffset}; Distance from Target: ${FrontLimelight.distance} Close? ${abs(Shooter.rpm - Shooter.rpmSetpoint) < 100.0}. Hi.")
////            println("Close to rpmSetpoint? ${abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0}. Valid Target? ${FrontLimelight.hasValidTarget} Small Aim Error? ${abs(aimError) < 1.0}. Been a while? ${currTime > 0.1}. Hi.")
//            if (abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.5) {
//                if (currTime > 0.1) {
//                    OI.driverController.rumble = 0.5
////                    ControlPanel.sendCommand(ArduinoCommand.LED_GREEN)
//                }
//            } else {
////                if(FrontLimelight.hasValidTarget && Shooter.prepShotOn){
//////                    ControlPanel.sendCommand(ArduinoCommand.LED_YELLOW)
////                }
//                OI.driverController.rumble = 0.0
//                t.start()
//            }
//            //auto unjam does not work. Amperage is too variable and can surpass jamming amperage even during normal use.
////            if(Feeder.current > 45.0 && !isUnjamming) {
////                isUnjamming = true
////                startTime = t.get()
////            } else if(isUnjamming) {
////                Feeder.setPower(-0.70)
////                Intake.setPower(0.70)
////                if(abs(startTime-t.get()) > 0.4) {
////                    isUnjamming = false
////                }
////            }else
//            if (OI.operatorController.rightTrigger > 0.1) {
//                Feeder.setPower(OI.operatorRightTrigger * -0.70)
//                Intake.setPower(OI.operatorRightTrigger * 0.70)
//                Intake.extend = false
//            } else if (OI.driverController.rightTrigger > 0.1) {
//                Feeder.setPower(OI.driveRightTrigger * 0.80)
//                Intake.setPower(OI.driveRightTrigger * 0.80)
//                Intake.extend = false
//            } else {
//                Feeder.setPower(0.0)
//                Intake.setPower(0.0)
//                Intake.extend = true
////                Intake.extend = false
//            }
//            if (!OI.driverController.leftBumper) {
//                this.stop()
//            }
//        }
//    } finally {
//        OI.driverController.rumble = 0.0
//        Shooter.rpm = 0.0
//        Shooter.prepShotOn = false
//        Feeder.setPower(0.0)
//        Intake.setPower(0.0)
//        Intake.extend = false
//        FrontLimelight.ledEnabled = false
//    }
//}
//
//
//suspend fun autoPrepShot(ballsIntaken: Int) = use(Shooter, Drive, Intake, Feeder) {
//    try {
//        Intake.setPower(0.0)
//        Shooter.prepShotOn = true
//        Intake.extend = true
//        Intake.setPower(Intake.INTAKE_POWER)
//        val totalT = Timer()
//        totalT.start()
//        val t = Timer()
//        t.start()
//        periodic {
//            Shooter.rpm = Shooter.rpmSetpoint
//            val currTime = t.get()
//            if (abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.0) {
//                if (currTime > 0.1) {
//                    this.stop()
//                }
//            } else {
//                t.start()
//            }
//            if (totalT.get() > 1.5) {
//                this.stop()
//            }
//            var turn = 0.0
////            println("has valid target: ${FrontLimelight.hasValidTarget}, xtranslation ${FrontLimelight.xTranslation}, parallax ${FrontLimelight.parallax.asDegrees}")
//            if (FrontLimelight.hasValidTarget && Shooter.prepShotOn) {
//                turn = Drive.aimPDController.update(FrontLimelight.xTranslation-FrontLimelight.parallax.asDegrees)
////                println("turn = $turn. Hi.")
//            }
//            Drive.drive(
//                Vector2(0.0, 0.0),
//                turn,
//                if (Drive.gyro != null) SmartDashboard.getBoolean(
//                    "Use Gyro",
//                    true
//                ) && !DriverStation.getInstance().isAutonomous else false
//            )
//        }
//        Feeder.setPower(Feeder.FEED_POWER)
//        Intake.extend = false
//        t.start()
//        var ballsShot = 0
//        var shootingBall = false
//        periodic(0.015) {
//            var rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches) + Shooter.rpmOffset
//            Shooter.rpm = rpmSetpoint
//            var currTime = t.get()
//            if(currTime > 2.0 && !shootingBall && Shooter.rpm < 0.93 * rpmSetpoint) {
//                ballsShot++
//                shootingBall = true
//            }
//            if(shootingBall && Math.abs(rpmSetpoint - Shooter.rpm) < 0.05 * rpmSetpoint) {
//                shootingBall = false
//            }
//            if(ballsShot > ballsIntaken - 1 || t.get() > 3.5) {
//                this.stop()
//            }
//            Drive.drive(
//                Vector2(0.0,0.0),
//                0.0
//            )
//        }
//    } finally {
//        OI.driverController.rumble = 0.0
//        Shooter.prepShotOn = false
//        Feeder.setPower(0.0)
//        Intake.extend = false
//    }
//}

suspend fun shootingMode(ballsIntaken: Int = 5) = use(Drive, Shooter, FrontLimelight, Intake, Feeder) {
    try {
        val isAuto = DriverStation.getInstance().isAutonomous
        Intake.setPower(0.0)
        Shooter.prepShotOn = true
        Intake.extend = true
        Intake.setPower(Intake.INTAKE_POWER)
        val totalT = Timer()
        totalT.start()
        var t = totalT.get()
        FrontLimelight.ledEnabled = true
        var currTime = totalT.get() - t
        var turn = 0.0
        periodic {
            Shooter.rpm = Shooter.rpmSetpoint
            if (abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.5) {
                currTime = totalT.get() - t
                if (!isAuto && currTime > 0.1) {
                    OI.driverController.rumble = 0.5
                }
                if(isAuto && currTime > 0.1) {
                    this.stop()
                }
            } else {
                t = totalT.get()
                if(!isAuto) {
                    OI.driverController.rumble = 0.0
                }
            }
            if(isAuto) {
                if(totalT.get() > 1.5) this.stop()
            } else {
                if (OI.operatorController.rightTrigger > 0.1) {
                    Feeder.setPower(OI.operatorRightTrigger * -0.70)
                    Intake.setPower(OI.operatorRightTrigger * 0.70)
                    Intake.extend = false
                } else if (OI.driverController.rightTrigger > 0.1) {
                    Feeder.setPower(OI.driveRightTrigger * 0.80)
                    Intake.setPower(OI.driveRightTrigger * 0.80)
                    Intake.extend = false
                } else {
                    Feeder.setPower(0.0)
                    Intake.setPower(0.0)
                    Intake.extend = true
                }
                if (!OI.driverController.leftBumper) {
                    this.stop()
                }
            }
            turn = 0.0
            if (OI.driveRotation.absoluteValue > 0.001 && !isAuto) {
                turn = OI.driveRotation
            } else if (FrontLimelight.hasValidTarget) {
                turn = Drive.aimPDController.update(FrontLimelight.aimError)
                println("FrontLimeLightAimError=${FrontLimelight.aimError} Hi.")
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
                if(!shootingBall && Shooter.rpm < 0.93 * Shooter.rpmSetpoint) {
                    ballsShot++
                    shootingBall = true
                }
                if(shootingBall && abs(Shooter.rpmSetpoint - Shooter.rpm) < 0.05 * Shooter.rpmSetpoint) {
                    shootingBall = false
                }
                if(ballsShot > ballsIntaken - 1 || totalT.get() > 3.5) {
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
        OI.driverController.rumble = 0.0
        Shooter.prepShotOn = false
        Feeder.setPower(0.0)
        Intake.extend = false
        FrontLimelight.ledEnabled = false
        if(!isAuto) {
            Shooter.rpm = 0.0
            Intake.setPower(0.0)
        }
    }
}
