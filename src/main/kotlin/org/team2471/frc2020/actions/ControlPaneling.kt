package org.team2471.frc2020.actions

import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.util.Timer
import org.team2471.frc2020.ControlPanel
import org.team2471.frc2020.EndGame
import org.team2471.frc2020.OI

suspend fun controlPanel1() = use(ControlPanel, EndGame) {
    println("got into controlpanel1")
    if (!ControlPanel.isExtending) {
        if (EndGame.climbIsExtending) {
            EndGame.climbIsExtending = false
            delay(0.5)
        }
        ControlPanel.isExtending = true
        delay(1.0)
    } else {
        println("bumper")
        val t = Timer()
        try {
            t.start()
            var startingColor = ""
            var currentColor = startingColor
            var previousColor = startingColor
            var colorCount = 0
            periodic {
                ControlPanel.setPower(0.5)
                currentColor = ControlPanel.getColor()
                if (currentColor != "") {
                    if (startingColor == "" && currentColor.length == 1) {
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
                if (t.get() > 5.0) {
                    this.stop()
                }
            }
        } finally {
            ControlPanel.setPower(0.0)
            delay(0.5)
            ControlPanel.isExtending = false

        }
    }
}

suspend fun controlPanel2() = use(ControlPanel, EndGame) {
    println("got into controlpanel2")
    if (!ControlPanel.isExtending) {
        if (EndGame.climbIsExtending) {
            EndGame.climbIsExtending = false
            delay(0.5)
        }
        ControlPanel.isExtending = true
        delay(1.0)
    } else {
        println("bumper")
        val t = Timer()
        try {
            t.start()
            ControlPanel.setPower(0.2)
            periodic {
                if (ControlPanel.getColor() == ControlPanel.gameData || t.get() > 5.0) {
                    this.stop()
                }
            }
        } finally {
            ControlPanel.setPower(0.0)
            delay(1.0)
            ControlPanel.isExtending = false
        }
    }
}

// github guthib gitgit hubhub gigbub
