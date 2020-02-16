package org.team2471.frc2020

import edu.wpi.first.wpilibj.Solenoid
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.actuators.TalonID
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
     val controlMotor = MotorController(TalonID(Talons.CONTROL_PANEL))
     private val extensionSolenoid = Solenoid(CONTROL_PANEL)
//    suspend fun test() = use(ControlPanel) {
//        periodic {
//            controlMotor.setPercentOutput(OI.driverController.leftTrigger)
//        }
//    }
    fun setPower(power: Double) {
        controlMotor.setPercentOutput(power)
    }

var isExtending: Boolean
    get() = extensionSolenoid.get()
    set(value) {
        extensionSolenoid.set(value)
    }

    override suspend fun default() {
        periodic {
            isExtending = false
        }
//        periodic {
//            spin(OI.driveLeftTrigger)
//            //setRPM(1000.0)
//            // setRPM(rpmTable.getDouble(0.0))
//        }
        }
    }
