package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.Intake
import org.team2471.frc2020.Intake.intakeMotor
import org.team2471.frc2020.OI

//suspend fun intake() = use(Intake){
//    try {
//        Intake.isExtending = true
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
//        Intake.isExtending = false
//    }
//}