package org.team2471.frc2020.actions

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.*
import org.team2471.frc2020.Limelight.aimError
import org.team2471.frc2020.Limelight.hasValidTarget
import kotlin.math.abs
import kotlin.math.absoluteValue

suspend fun teleopPrepShot() = use(Shooter) {
    try {
        Shooter.prepShotOn = true
        //val setpoint = Shooter.rpmCurve.getValue(Limelight.distance.asInches)
        val setpoint = Shooter.rpmSetpointEntry.getDouble(0.0)
        Shooter.rpm = setpoint
        val t = Timer()
        t.start()
        periodic {
            val currTime = t.get()
            if (abs(Shooter.rpm - setpoint) < 100.0 && Limelight.hasValidTarget && abs(aimError) < 0.5) {
                if (currTime > 0.1) {
                    OI.driverController.rumble = 0.5
                }
            } else {
                OI.driverController.rumble = 0.0
                t.start()
            }
            if (!OI.driverController.leftBumper) {
                this.stop()
            }

        }
    } finally {
        OI.driverController.rumble = 0.0
        Shooter.rpm = 0.0
        Shooter.prepShotOn = false
    }
}


suspend fun autoPrepShot() = use(Shooter, Drive) {
    try {
        Shooter.prepShotOn = true
        val totalT = Timer()
        totalT.start()
        val t = Timer()
        t.start()
        periodic {
            if (Shooter.rpmSetpointEntry.getDouble(0.0) < 0.1) {
                Shooter.stop()
            } else {
                Shooter.rpm = Shooter.rpmCurve.getValue(Limelight.distance.asInches)
            }
            val currTime = t.get()
            if (abs(Shooter.rpm - Shooter.rpmCurve.getValue(Limelight.distance.asInches)) < 100.0 && Limelight.hasValidTarget && abs(aimError) < 0.5) {
                if (currTime > 0.1) {
                    this.stop()
                }
            } else {
                t.start()
            }
            if (totalT.get() > 2.0) {
                this.stop()
            }
            var turn = 0.0
            if (OI.driveRotation.absoluteValue > 0.001) {
                turn = OI.driveRotation
            } else if (Limelight.hasValidTarget && Shooter.prepShotOn) {
                turn = Drive.aimPDController.update(Limelight.xTranslation-Limelight.parallax.asDegrees)
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
    } finally {
        OI.driverController.rumble = 0.0
        Shooter.prepShotOn = false
    }
}


suspend fun shoot() = use(Feeder)
 {
    try {
        if (Shooter.rpm > 1000.0) {
        Feeder.setPower(Feeder.FEED_POWER)
        }
        suspendUntil(20, { OI.driverController.rightTrigger == 0.0 })
    } finally {
        Feeder.setPower(0.0)
    }
}
