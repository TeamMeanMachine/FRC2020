package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Talons


object Intake: Subsystem("Shooter") {
    val intakeMotor = MotorController(TalonID(Talons.INTAKE))
/*    private val extensionSolenoid = Solenoid(INTAKE_EXTENDER)
    private val extensionSolenoid2 = S

    private val INTAKE_POWER =
*/


    init {}

    fun temporaryTest() {
        print("Remove this if found.")
    }
    /*var isExtending: Boolean
        get() = extensionSolenoid.get()
        set(value) {
            extensionSolenoid.set(value)
        }

        fun setPower(power: Double) {
            intakeMotor.setPercentOutput(power)
        }
*/
    override suspend fun default() {
    }
}