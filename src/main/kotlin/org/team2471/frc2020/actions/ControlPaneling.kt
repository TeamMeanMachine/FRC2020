package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
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
        delay(1.0)
        //todo: give time for lining up
        var startingColor = ControlPanel.getColor()
        var currentColor = startingColor
        var previousColor = startingColor
        var colorCount = 0
        periodic {
            ControlPanel.setPower(0.5)
            currentColor = ControlPanel.getColor()
            if (currentColor != "") {
                if (startingColor == "") {
                    startingColor = currentColor
                    previousColor = currentColor
                }
                if (currentColor != previousColor) {
                    println("Color changed $previousColor to $currentColor")
                    if (currentColor == startingColor) {
                        colorCount++
                        if (colorCount == 6) {
                            this.stop()
                        }
                    }
                    previousColor = currentColor
                }
            }
        }
    } finally {
        ControlPanel.isExtending = false
        ControlPanel.setPower(0.0)
    }
}

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