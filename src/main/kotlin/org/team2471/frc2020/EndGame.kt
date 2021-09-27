package org.team2471.frc2020

import edu.wpi.first.wpilibj.Solenoid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.actuators.VictorID
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc2020.Solenoids.BRAKE
import org.team2471.frc2020.Solenoids.CLIMB

object EndGame: Subsystem("EndGame") {
    val leftClimbMotor = MotorController(FalconID(Falcons.LEFT_CLIMB))
    val rightClimbMotor = MotorController(FalconID(Falcons.RIGHT_CLIMB))

    // motors aren't ided yet!


    private val brakeSolenoid = Solenoid(BRAKE)

    init {
        rightClimbMotor.config {
            inverted(true)
        }
//        GlobalScope.launch(MeanlibDispatcher) {
//            periodic {
//                Intake.currentEntry.setDouble(Intake.intakeMotor.current)
//            }
//        }
    }


    var brakeIsOn: Boolean
        get() = !brakeSolenoid.get()
        set(value) {
            brakeSolenoid.set(!value)
        }

    fun setPower(power: Double) {
        leftClimbMotor.setPercentOutput(power)
        rightClimbMotor.setPercentOutput(power)
    }

    override suspend fun default() {
        periodic {
            brakeIsOn = true
        }
    }
}