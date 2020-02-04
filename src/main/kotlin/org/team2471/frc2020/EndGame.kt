package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.framework.Subsystem

object EndGame: Subsystem("EndGame") {
    private val balanceMotor = MotorController(TalonID(Talons.BALANCE))

    /* private val climbSolenoid = Solenoid()
       private val brakeSolenoid = Solenoid()*/

    init {}

    /* var climbIsExtending: Boolean
        get() = climbSolenoid.get()
        set(value) {
           climbSolenoid.set(value)
        }

      var brakeIsExtending: Boolean
        get() = brakeSolenoid.get()
        set(value) {
            brakeSolenoid.set(value)
        }

      fun setPower(power: Double) {
        balanceMotor.setPercentOutput(power)
    } */

    override suspend fun default() {
    }
}