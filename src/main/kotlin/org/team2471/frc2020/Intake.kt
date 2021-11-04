package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.PneumaticsModuleType
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
    private val extensionSolenoid = Solenoid(PneumaticsModuleType.CTREPCM, INTAKE)

    private val table = NetworkTableInstance.getDefault().getTable(Intake.name)
    val currentEntry = table.getEntry("Current")

    val INTAKE_POWER = 0.75

    val button = DigitalInput(0)




    init {
        intakeMotor.config {
            inverted(true)
        }
        GlobalScope.launch(MeanlibDispatcher) {
            periodic {
                currentEntry.setDouble(intakeMotor.current)
            }
        }
    }

    var extend: Boolean
        get() = extensionSolenoid.get()
        set(value) = extensionSolenoid.set(value)

    val ballIsStaged: Boolean
        get() = !button.get()


    fun setPower(power: Double) {
        intakeMotor.setPercentOutput(power)
    }


    override suspend fun default() {
        try {
            extend = false
            delay(1.7)
            periodic {
                setPower(OI.operatorRightTrigger * 0.7)
            }
        } finally {
            extend = false
            setPower(0.0)
        }
    }
}