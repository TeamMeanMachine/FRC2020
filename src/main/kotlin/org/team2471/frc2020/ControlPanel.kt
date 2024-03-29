package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.SerialPort
import edu.wpi.first.wpilibj.Solenoid
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.VictorID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.CONTROL_PANEL

//import org.team2471.frc.lib.actuators.MotorController
//import org.team2471.frc.lib.actuators.SparkMaxID
//import org.team2471.frc.lib.coroutines.periodic
//import org.team2471.frc.lib.framework.Subsystem
//import org.team2471.frc.lib.framework.use
//
object ControlPanel : Subsystem("Control Panel") {
    private val table = NetworkTableInstance.getDefault().getTable(name)
    val gameColorEntry = table.getEntry("Game Color")
    val gameData: String
        get() {
            return if (DriverStation.getInstance().gameSpecificMessage != null && DriverStation.getInstance().gameSpecificMessage.isNotEmpty()) {
                when (DriverStation.getInstance().gameSpecificMessage[0]) {
                    'R' -> "b"
                    'G' -> "y"
                    'B' -> "r"
                    'Y' -> "g"
                    else -> "Error"
                }
            } else {
                "Error"
            }
        }


    val controlMotor = MotorController(VictorID(Victors.CONTROL_PANEL))

    private val extensionSolenoid = Solenoid(CONTROL_PANEL)

//    val serialPort = SerialPort(9600, SerialPort.Port.kUSB1)

    fun setPower(power: Double) {
        controlMotor.setPercentOutput(power)
    }


    var fmsColor: String
        get() = {
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

/*
    var readSerial: String
        get() {
            return if (serialPort.readString().isNotEmpty()) serialPort.readString() else "Error"
        }
        set(value) {}
*/
    var isExtending: Boolean
        get() = extensionSolenoid.get()
        set(value) {
            extensionSolenoid.set(value)
        }
/*
    var lastColor = ""

//
    override suspend fun default() {
        periodic {
       //     println("Color read: ${getColor()}. Hi.")
            gameColorEntry.setString(gameData)

//           println("Serial Output: $lastColor")
//           println("FMS Target Color: $fmsColor")
        }
    }

    init {
        controlMotor.config {
            brakeMode()
        }
    }
*/
    fun getColor() : String {
//        sendCommand(ArduinoCommand.SAMPLE)
//        lastColor = readSerial
//        return lastColor
        return "Red"
    }
/*
    fun sendCommand(command: ArduinoCommand) {
        if (serialPort.readString().isNotEmpty()) {
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
*/
}