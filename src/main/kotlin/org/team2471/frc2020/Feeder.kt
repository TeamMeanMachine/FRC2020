package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.Talon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use

object Feeder: Subsystem("Feeder") {
 private val feederMotor = MotorController(FalconID(Falcons.FEEDER))

    private val indexMotor = MotorController(TalonID(Talons.INDEXER))
    private val table = NetworkTableInstance.getDefault().getTable(Feeder.name)
    val currentEntry = table.getEntry("Feeder Current")

    val FEED_POWER = 1.0

    init {
        feederMotor.config {
            inverted(true)
        }

        GlobalScope.launch(MeanlibDispatcher) {
            periodic {
                currentEntry.setDouble(feederMotor.current)
            }
        }
    }

    val current: Double
        get() = feederMotor.current

    fun setPower(power: Double) {
        feederMotor.setPercentOutput(power)
    }

    fun indexSetPower(power: Double) {
        indexMotor.setPercentOutput(power)
    }



    suspend fun reverseFeeder() = use(Feeder){
        try {
//            println("Got into reverseFeeder. Hi.")
            periodic {
                setPower(-OI.driverController.leftTrigger * 0.7)
            }
        } finally {
            setPower(0.0)
        }
    }

    override suspend fun default() {
        try {
            var buttonWasPressed = false

            periodic {
                if (!Intake.ballIsStaged && !buttonWasPressed) {
                    Feeder.setPower(0.5)
                    indexSetPower(1.0)


                } else {
                    setPower(0.0)
                    buttonWasPressed = true
                }
            }
        } finally {
            setPower(0.0)
        }

    }
}