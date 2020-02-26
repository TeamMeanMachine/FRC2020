package org.team2471.frc2020

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.SerialPort
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.actuators.VictorID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.CONTROL_PANEL
import java.awt.image.renderable.ContextualRenderedImageFactory

//import org.team2471.frc.lib.actuators.MotorController
//import org.team2471.frc.lib.actuators.SparkMaxID
//import org.team2471.frc.lib.coroutines.periodic
//import org.team2471.frc.lib.framework.Subsystem
//import org.team2471.frc.lib.framework.use
//
object ControlPanel : Subsystem("Control Panel") {
    val controlMotor =
        if (isCompBotIHateEverything) MotorController(VictorID(Victors.CONTROL_PANEL)) else MotorController(
            TalonID(Talons.CONTROL_PANEL)
        )
    private val extensionSolenoid = Solenoid(CONTROL_PANEL)
    val serialPort = SerialPort(9600, SerialPort.Port.kUSB)

    fun setPower(power: Double) {
        controlMotor.setPercentOutput(power)
    }

    private enum class Color {
        RED,
        YELLOW,
        GREEN,
        BLUE,
        NONE
    }

    var fmsColor: String
        get() = {
            var gameData = DriverStation.getInstance().gameSpecificMessage;
            if (gameData.isNotEmpty()){
                when(gameData[0]) {
                    'R' -> "Red"
                    'G' -> "Green"
                    'B' -> "Blue"
                    'Y' -> "Yellow"
                    else -> "Error"
                }
            } else {
                "None"
            }
        }.toString()
        set(value) {}

    var readSerial: String
        get() = serialPort.readString()
        set(value) {}

    var isExtending: Boolean
        get() = extensionSolenoid.get()
        set(value) {
            extensionSolenoid.set(value)
        }

    override suspend fun default() {
        periodic {
            if(OI.operatorController.y)
                sendCommand(ArduinoCommand.SAMPLE)

            SmartDashboard.putString("Serial Output", readSerial)
            SmartDashboard.putString("FMS Target Color", fmsColor)
        }
    }

    fun sendCommand(command: ArduinoCommand) {
        when (command) {
            ArduinoCommand.CALIBRATE_R -> serialPort.writeString("r")
            ArduinoCommand.CALIBRATE_G -> serialPort.writeString("g")
            ArduinoCommand.CALIBRATE_B -> serialPort.writeString("b")
            ArduinoCommand.CALIBRATE_Y -> serialPort.writeString("y")
            ArduinoCommand.LED_RED -> serialPort.writeString("R")
            ArduinoCommand.LED_GREEN -> serialPort.writeString("G")
            ArduinoCommand.LED_BLUE -> serialPort.writeString("B")
            ArduinoCommand.LED_YELLOW -> serialPort.writeString("Y")
            ArduinoCommand.SAMPLE -> serialPort.writeString("?")
        }
    }
}