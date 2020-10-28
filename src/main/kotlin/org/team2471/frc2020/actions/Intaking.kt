package org.team2471.frc2020.actions

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.input.whenTrue
import org.team2471.frc.lib.math.squareWithSign
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.Feeder
import org.team2471.frc2020.Intake
import org.team2471.frc2020.Intake.INTAKE_POWER
import org.team2471.frc2020.Intake.intakeMotor
import org.team2471.frc2020.OI
import java.lang.Double.max
import kotlin.math.pow

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

suspend fun intake() = use(Intake, Feeder) {
    try {
        var buttonWasPressed = false
        Intake.extend = true
        Intake.setPower(INTAKE_POWER)
        var t = Timer()
        t.start()
        var goalT = Double.MAX_VALUE
        var maxCurr = 0.0
        OI.driverController.rumble = 0.0
        delay(0.5)
        periodic {
            if (intakeMotor.current > 12.0) {
                goalT = t.get() + 0.3
                maxCurr = max(intakeMotor.current, maxCurr)
            }
            if (t.get() > goalT) {
                maxCurr = 0.0
                goalT = Double.MAX_VALUE
            }
            if (!Intake.ballIsStaged && !buttonWasPressed) {
                Feeder.setPower(0.7)
//                print(!Intake.ballIsStaged)
            } else {
                Feeder.setPower(0.0)
//                print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH")
                buttonWasPressed = true
            }
//            println("maxCurr: $maxCurr")
            OI.driverController.rumble = (maxCurr / 40.0).pow(2.0)
        }
    } finally {
        Feeder.setPower(0.0)
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

