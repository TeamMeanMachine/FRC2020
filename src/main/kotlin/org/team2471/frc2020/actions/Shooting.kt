package org.team2471.frc2020.actions

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.*
import org.team2471.frc2020.FrontLimelight.aimError
import java.util.*
import javax.naming.ldap.Control
import kotlin.math.abs
import kotlin.math.absoluteValue

suspend fun shootMode() = use(Shooter, Feeder, Intake, FrontLimelight) {
    try {
        Intake.setPower(0.0)
        Intake.extend = false
        Shooter.prepShotOn = true
        Shooter.rpm = Shooter.rpmSetpoint
        Intake.extend = true
        Intake.setPower(0.0)
        FrontLimelight.ledEnabled = true
        val t = Timer()
        t.start()
        FrontLimelight.ledEnabled = true
        periodic {
            Shooter.rpm = Shooter.rpmSetpoint
            val currTime = t.get()
//            println("rpm: ${Shooter.rpm}; rpmSetpoint: ${Shooter.rpmSetpoint}; Close? ${abs(Shooter.rpm - Shooter.rpmSetpoint) < 100.0}. Hi.")
            if (abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.5) {
//                println("Close to rpmSetpoint? ${abs(Shooter.rpm - Shooter.rpmSetpoint) < 200.0}. Valid Target? ${FrontLimelight.hasValidTarget} Small Aim Error? ${abs(aimError) < 1.0}. Been a while? ${currTime > 0.1}. Hi.")
                if (currTime > 0.1) {
                    OI.driverController.rumble = 0.5
//                    ControlPanel.sendCommand(ArduinoCommand.LED_GREEN)
                }
            } else {
                if(FrontLimelight.hasValidTarget && Shooter.prepShotOn){
//                    ControlPanel.sendCommand(ArduinoCommand.LED_YELLOW)
                }
                OI.driverController.rumble = 0.0
                t.start()
            }

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
    } finally {
        OI.driverController.rumble = 0.0
        Shooter.rpm = 0.0
        Shooter.prepShotOn = false
        Feeder.setPower(0.0)
        Intake.setPower(0.0)
        Intake.extend = false
        FrontLimelight.ledEnabled = false
    }
}


suspend fun autoPrepShot(ballsIntaken: Int) = use(Shooter, Drive, Intake, Feeder) {
    try {
        Intake.setPower(0.0)
        Shooter.prepShotOn = true
        Intake.extend = true
        Intake.setPower(Intake.INTAKE_POWER)
        val totalT = Timer()
        totalT.start()
        val t = Timer()
        t.start()
        periodic {
            val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)
            Shooter.rpm = rpmSetpoint
            val currTime = t.get()
            if (abs(Shooter.rpm - Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)) < 200.0 && FrontLimelight.hasValidTarget && abs(aimError) < 1.0) {
                if (currTime > 0.1) {
                    this.stop()
                }
            } else {
                t.start()
            }
            if (totalT.get() > 1.5) {
                this.stop()
            }
            var turn = 0.0
//            println("has valid target: ${FrontLimelight.hasValidTarget}, xtranslation ${FrontLimelight.xTranslation}, parallax ${FrontLimelight.parallax.asDegrees}")
            if (FrontLimelight.hasValidTarget && Shooter.prepShotOn) {
                turn = Drive.aimPDController.update(FrontLimelight.xTranslation-FrontLimelight.parallax.asDegrees)
//                println("turn = $turn. Hi.")
            }
            Drive.drive(
                Vector2(0.0, 0.0),
                turn,
                if (Drive.gyro != null) SmartDashboard.getBoolean(
                    "Use Gyro",
                    true
                ) && !DriverStation.getInstance().isAutonomous else false
            )
        }
        Feeder.setPower(Feeder.FEED_POWER)
        Intake.extend = false
        t.start()
        var ballsShot = 0
        var shootingBall = false
        periodic(0.015) {
            var rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches) + Shooter.rpmOffset
            Shooter.rpm = rpmSetpoint
            var currTime = t.get()
            if(currTime > 2.0 && !shootingBall && Shooter.rpm < 0.93 * rpmSetpoint) {
                ballsShot++
                shootingBall = true
            }
            if(shootingBall && Math.abs(rpmSetpoint - Shooter.rpm) < 0.05 * rpmSetpoint) {
                shootingBall = false
            }
            if(ballsShot > ballsIntaken - 1 || t.get() > 3.5) {
                this.stop()
            }
            Drive.drive(
                Vector2(0.0,0.0),
                0.0
            )
        }
    } finally {
        OI.driverController.rumble = 0.0
        Shooter.prepShotOn = false
        Feeder.setPower(0.0)
        Intake.extend = false
    }
}
