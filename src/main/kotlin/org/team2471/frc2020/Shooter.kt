package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.MeanlibDispatcher

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(SparkMaxID(Sparks.SHOOTER), SparkMaxID(Sparks.SHOOTER2))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    private val rpmEntry = table.getEntry("RPM")
    private val rpmSetpointEntry = table.getEntry("RPM Setpoint")


    init {
        shootingMotor.config {
            feedbackCoefficient = 1.0/(42.0 * (24/18) * (5000.0/6570.0))
            followersInverted(true)
            pid {
                p(1e-8)
                i(0.0)
                d(0.0)
                f(0.0001)
            }
        }
        rpmSetpointEntry.setDouble(0.0)
        GlobalScope.launch(MeanlibDispatcher) {
            periodic {
                rpmEntry.setDouble(rpm)
            }
        }
    }

    fun setPower(power: Double) {
        shootingMotor.setPercentOutput(power)
    }

    var rpm: Double
        get() = shootingMotor.velocity
        set(value) = shootingMotor.setVelocitySetpoint(value)

    override suspend fun default() {
        periodic {
            //setPower(OI.driveRightTrigger)
            if (rpmSetpointEntry.getDouble(0.0) < 0.1) {
                shootingMotor.stop()
            } else {
                rpm = rpmSetpointEntry.getDouble(0.0)
            }
        }
    }
}