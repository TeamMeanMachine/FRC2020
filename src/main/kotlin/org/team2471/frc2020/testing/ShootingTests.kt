package org.team2471.frc2020.testing

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.Feeder
import org.team2471.frc2020.FrontLimelight
import org.team2471.frc2020.OI
import org.team2471.frc2020.Shooter
import java.lang.Math.abs

suspend fun Shooter.distance2RpmTest() = use(this, Feeder, FrontLimelight){
    FrontLimelight.ledEnabled = true
    periodic {
        rpm = rpmSetpointEntry.getDouble(0.0)
        Feeder.setPower(OI.driveRightTrigger)
    }

}

suspend fun Shooter.motorTest() = use(Shooter, Feeder) {
    println("In Shooter.motorTest(). Hi.")
    periodic {
        setPower(OI.operatorController.leftThumbstickY)
        Feeder.setPower(OI.operatorController.rightThumbstickY * 0.75)
    }
}

suspend fun Shooter.countBallsShotTest() = use(this, Feeder) {
    var rpmSetpoint = 4100.0
    rpm = rpmSetpoint
    val t = Timer()
    t.start()
    parallel ({
        periodic {
            if(abs(rpmSetpoint - rpm) < 100) {
                Feeder.setPower(Feeder.FEED_POWER)
            }
        }
    },{
        var ballsShot = 0
        var shootingBall = false
        periodic(0.015) {
              var currTime = t.get()
              if(currTime > 2.0 && !shootingBall && rpm < 0.93 * rpmSetpoint) {
                  ballsShot++
                  shootingBall = true
              }
              if(shootingBall && abs(rpmSetpoint - rpm) < 0.05 * rpmSetpoint) {
                  shootingBall = false
              }
//            println("Balls shot: $ballsShot. Hi.")
        }

    })
}