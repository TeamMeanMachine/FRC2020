package org.team2471.frc2020

import edu.wpi.first.wpilibj.Solenoid
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.INTAKE
import org.team2471.frc2020.Talons


object Intake: Subsystem("Intake") {
    val intakeMotor = MotorController(TalonID(Talons.INTAKE))
    private val extensionSolenoid = Solenoid(INTAKE)

    val INTAKE_POWER = 0.75


    init {
        intakeMotor.config {
            inverted(true)
        }
    }

    var extend: Boolean
        get() = extensionSolenoid.get()
        set(value) = extensionSolenoid.set(value)


        /*fun setPower(power: Double) {
            intakeMotor.setPercentOutput(power)
        }
*/
    fun setPower(power: Double) {
        intakeMotor.setPercentOutput(power)
    }

    suspend fun stopMotorAfterIntaking() {
        delay(1.7)
        setPower(0.0)
    }

//    override suspend fun default() {
////        intakeIsExtending = true //climb testing
//    }
}