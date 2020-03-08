package org.team2471.frc2020

import edu.wpi.first.wpilibj.SerialPort
import edu.wpi.first.wpilibj.interfaces.Gyro
import org.team2471.frc.lib.units.degrees

class TheBestGyroEver : Gyro {
    private val batteryboard = SerialPort(9600, SerialPort.Port.kUSB)
    private var offset = -180.0

    override fun getAngle(): Double {
        return (rawAngle - offset).degrees.wrap().asDegrees
    }

    val rawAngle : Double
        get() = currentVals(2).toDouble()

    val rawYaw : Double
        get() = currentVals(0).toDouble()

    val rawPitch : Double
        get() = currentVals(1).toDouble()

    private fun  currentVals(index: Int) : String
    {
        return batteryboard.readString(50).split("\n")[1].split(",")[index]
    }

    override fun getRate(): Double = 0.0

        override fun calibrate() = Unit

        override fun reset()  {
            offset = -180.0 + rawAngle
        }

    override fun close() {
        batteryboard.close()
    }

}