package org.team2471.frc2020.actions

import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.ControlPanel

//suspend fun controlPanel1() = use(ControlPanel){
//    try{
//          ControlPanel.isExtending = true
//          /*var startingColor = sensor stuff*/
//          ControlPanel.setPower(0.2)
//          delay(5.0)
//          /*suspendUntil(sensor == startingColor)
//          suspendUntil(sensor == startingColor)
//          suspendUntil(sensor == startingColor)*/
//    } finally {
//          ControlPanel.isExtending = false
//          ControlPanel.setPower(0.0)
//    }
//}
//
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