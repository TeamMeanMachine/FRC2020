package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.OI

suspend fun controlPanel1() = use(ControlPanel) {
    try {
        if (EndGame.climbIsExtending) {
            EndGame.climbIsExtending = false
            delay(0.5)
        }
        ControlPanel.isExtending = true
        /*var startingColor = sensor stuff*/
        periodic {
            ControlPanel.setPower(OI.operatorLeftX)
        }
        /*suspendUntil(sensor == startingColor)
           suspendUntil(sensor == startingColor)
           suspendUntil(sensor == startingColor)*/
    } finally {
        ControlPanel.isExtending = false
        ControlPanel.setPower(0.0)
    }
}

//Testing GitHub. Hi.

//suspend fun controlPanel2() = use(ControlPanel) {
//    try{
//          ControlPanel.isExtending = true
//          ControlPanel.setPower(0.2)
//          periodic {
//              if(/*sensor stuff*/) {
//                  this.stop()
//              }
//          }
//    } finally {
//          ControlPanel.isExtending = false
//          ControlPanel.setPower(0.0)
//    }
//}