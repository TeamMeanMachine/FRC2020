package org.team2471.frc2020

import edu.wpi.first.wpilibj.SerialPort
import org.team2471.frc.lib.framework.Subsystem

object Sensors: Subsystem("Sensors") {
    val rs232: SerialPort = SerialPort(9600, SerialPort.Port.kOnboard)

    init{ }

//    override suspend fun default(){ }
}