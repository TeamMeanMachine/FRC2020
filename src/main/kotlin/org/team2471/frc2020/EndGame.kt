package org.team2471.frc2020

import edu.wpi.first.wpilibj.Solenoid
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.actuators.VictorID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.BRAKE
import org.team2471.frc2020.Solenoids.CLIMB

object EndGame: Subsystem("EndGame") {
    val balanceMotor = MotorController(VictorID(Victors.BALANCE))

    //private val climbSolenoid = Solenoid(CLIMB)
   // private val brakeSolenoid = Solenoid(BRAKE)

    init {

    }

    var climbIsExtending: Boolean
        get() = true// climbSolenoid.get()
        set(value) {
           //climbSolenoid.set(value)
        }

    var brakeIsExtending: Boolean
        get() = true //!brakeSolenoid.get()
        set(value) {
           // brakeSolenoid.set(!value)
        }

    fun setPower(power: Double) {
        balanceMotor.setPercentOutput(power)
    }

    override suspend fun default() {
        periodic {
            climbIsExtending = false
            brakeIsExtending = true
        }
    }
}