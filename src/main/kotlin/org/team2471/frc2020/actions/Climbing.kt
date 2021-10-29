//package org.team2471.frc2020.actions
//
//import kotlinx.coroutines.NonCancellable
//import kotlinx.coroutines.withContext
//import org.team2471.frc.lib.coroutines.delay
//import org.team2471.frc.lib.coroutines.parallel
//import org.team2471.frc.lib.coroutines.periodic
//import org.team2471.frc.lib.framework.use
//import org.team2471.frc2020.Intake
//import org.team2471.frc2020.ControlPanel
//import org.team2471.frc2020.OI
//import org.team2471.frc2020.Shooter
//import kotlin.math.absoluteValue
//
//suspend fun climb2() = use(EndGame) {
//    println("Entered climbing")
//
//    try {
//        periodic {
//            if (OI.operatorLeftY.absoluteValue > 0.2) {
//                println("joystick move pls")
//                EndGame.brakeIsOn = false
//                EndGame.setPower(OI.operatorLeftY * 1.0)
//            } else if (OI.operatorLeftTrigger > 0.1) {
//                EndGame.brakeIsOn = false
//                EndGame.setLeftPower(OI.operatorLeftTrigger * 0.3)
//            } else if (OI.operatorRightTrigger > 0.1) {
//                EndGame.brakeIsOn = false
//                EndGame.setRightPower(OI.operatorRightTrigger * 0.3)
//            } else {
//                EndGame.setPower(0.0)
//                EndGame.brakeIsOn = true
//            }
//        }
//
//    } finally {
//        EndGame.brakeIsOn = true
//        EndGame.setPower(0.0)
//    }
//}