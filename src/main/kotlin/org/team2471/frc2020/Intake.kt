package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.Solenoid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.INTAKE
import org.team2471.frc2020.Talons


object Intake: Subsystem("Intake") {
    val intakeMotor = MotorController(TalonID(Talons.INTAKE))
    private val extensionSolenoid = Solenoid(INTAKE)

    private val table = NetworkTableInstance.getDefault().getTable(Intake.name)
    val currentEntry = table.getEntry("Current")

    val INTAKE_POWER = 0.75
 var defaultReason = ""
    val button = DigitalInput(9) //you can check to see if this is the same (where the button is plugged in on the roboRIO the grey thing at the bottom) or you can test it and cross your fingers
//the hopper/feeder should run until a ball presses the button
    //is this enough info for a few min?

var defaultExtend = false

    init {
        intakeMotor.config {
            inverted(true)
            feedbackCoefficient = 0.01242
        }
        GlobalScope.launch(MeanlibDispatcher) {
            periodic {
                currentEntry.setDouble(intakeMotor.current)
            }
        }
    }

    var extend: Boolean
        get() =  extensionSolenoid.get()
        set(value) =  extensionSolenoid.set(value)

    val ballIsStaged: Boolean
        get() = !button.get()


    fun setPower(power: Double) {
        intakeMotor.setPercentOutput(power)
    }


    override suspend fun default() {
        try {
            println("defaultExtend=$defaultExtend $defaultReason")
            if(OI.operatorController.rightBumper) {
                defaultExtend = false
                defaultReason = "default set to false"
                println("defaultExtend=False From default")
            }
            extend = defaultExtend
            delay(1.7)
            //setPower(OI.operatorRightTrigger * 0.7 ) beans put this in a periodic
//            println("Motorencoder: ${intakeMotor.position}")

        } finally {
            extend = false
            setPower(0.0)
        }
    }
}