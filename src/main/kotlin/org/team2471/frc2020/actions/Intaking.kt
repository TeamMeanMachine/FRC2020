package org.team2471.frc2020.actions

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.input.whenTrue
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.Intake
import org.team2471.frc2020.Intake.INTAKE_POWER
import org.team2471.frc2020.Intake.intakeMotor
import org.team2471.frc2020.OI

//suspend fun intake() = use(Intake){
//    try {
//        Intake.extend = true
//        Intake.setPower(INTAKE_POWER)
//        val t = Timer()
//        t.start()
//        periodic {
//            val currT = t.get()
//            if (intakeMotor.current > 1.0 /*TODO: tune numbers */ && currT > 1.0) {
//                this.stop() //cell safety because it may get caught
//            } else {
//                t.start()
//            }
//
//            if (OI.driverController.leftTrigger < 0.1 ) {
//                this.stop()
//            }
//        }
//    } finally {
//        Intake.setPower(0.0)
//        Intake.extend = false
//    }
//}

suspend fun intake() = use(Intake) {
    Intake.extend = true
    Intake.setPower(INTAKE_POWER)
    periodic {
        if(intakeMotor.current > 8.0)
            OI.driverController.rumble
    }

}

suspend fun autoIntakeStart() = use(Intake) {
    Intake.extend = true
    Intake.setPower(INTAKE_POWER)
}

suspend fun autoIntakeStop() = use(Intake) {
    Intake.extend = false
    Intake.setPower(0.0)
}

