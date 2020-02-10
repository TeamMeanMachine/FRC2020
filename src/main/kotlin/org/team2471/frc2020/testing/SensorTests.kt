package org.team2471.frc2020.testing

import org.team2471.frc2020.Sensors
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc2020.OI

suspend fun Sensors.test() = use(this, Sensors) {

    periodic {
        if (OI.operatorController.b)
            rs232.write(byteArrayOf('r'.toByte()), byteArrayOf('r'.toByte()).size)
        if (OI.operatorController.a)
            rs232.write(byteArrayOf('g'.toByte()), byteArrayOf('g'.toByte()).size)
        if (OI.operatorController.x)
            rs232.write(byteArrayOf('b'.toByte()), byteArrayOf('b'.toByte()).size)
        if (OI.operatorController.y)
            rs232.write(byteArrayOf('y'.toByte()), byteArrayOf('y'.toByte()).size)
    }
}