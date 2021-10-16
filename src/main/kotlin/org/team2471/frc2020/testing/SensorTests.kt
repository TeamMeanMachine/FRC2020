package org.team2471.frc2020.testing

import org.team2471.frc2020.Sensors
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.OI

suspend fun Sensors.test() = use(this, Sensors) {

    periodic {
        if (OI.operatorController.b)
            rs232.write(byteArrayOf('r'.code.toByte()), byteArrayOf('r'.code.toByte()).size)
        if (OI.operatorController.a)
            rs232.write(byteArrayOf('g'.code.toByte()), byteArrayOf('g'.code.toByte()).size)
        if (OI.operatorController.x)
            rs232.write(byteArrayOf('b'.code.toByte()), byteArrayOf('b'.code.toByte()).size)
        if (OI.operatorController.y)
            rs232.write(byteArrayOf('y'.code.toByte()), byteArrayOf('y'.code.toByte()).size)
    }
}