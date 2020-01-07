package org.team2471.FRC2020

import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion.following.driveAlongPath
import org.team2471.frc.lib.motion.following.stop
import org.team2471.frc.lib.motion_profiling.Path2D
import org.team2471.frc.lib.units.Length
import org.team2471.frc.lib.units.asFeet

suspend fun pathThenVision(path: Path2D, stopTime: Double, resetOdometry: Boolean = false) = use(Drive, Limelight, name = "Path then Vision") {

    val pathJob = launch { Drive.driveAlongPath(path, resetOdometry = resetOdometry) }

    suspendUntil { Limelight.hasValidTarget || Bintake.current > 45.0 }
    pathJob.cancelAndJoin()

    //limelight takes over
    Limelight.isCamEnabled = true

    periodic {
        if (Limelight.hasValidTarget) {
            // send it
            Drive.drive(
                Vector2(-Limelight.xTranslation/320.0, -0.5), 0.0, false)
            println(Limelight.xTranslation/320.0)
        } else {
            Drive.drive(Vector2(0.0, -0.25), 0.0, false)
        }

        if (Drive.position.y > 30.0 || Bintake.current > 45.0 ) {
            this.stop()
        }
    }
    Drive.stop()
}