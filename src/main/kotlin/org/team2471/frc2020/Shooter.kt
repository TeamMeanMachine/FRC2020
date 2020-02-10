package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.motion_profiling.MotionCurve

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(SparkMaxID(Sparks.SHOOTER), SparkMaxID(Sparks.SHOOTER2))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    val rpmEntry = table.getEntry("RPM")
    val rpmSetpointEntry = table.getEntry("RPM Setpoint")
    val rpmCurve = MotionCurve()

    public var prepShotOn = false


    init {
//        rpmCurve.StoreValue(125, 4600)
//        rpmCurve.StoreValue(130, 5000)
//        rpmCurve.StoreValue(135, 5400)
//        rpmCurve.StoreValue(143, 4800)
//        rpmCurve.StoreValue(175, 4100)
//        rpmCurve.StoreValue(219, 4000)
//        rpmCurve.StoreValue(265, 4900)


        shootingMotor.config {
            feedbackCoefficient = 1.0/(42.0 * 1.01471)
            inverted(true)
            followersInverted(true)
            pid {
                p(1.5e-8)
                i(0.0)
                d(0.0)
                f(0.000045)
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

    fun stop() {
        shootingMotor.stop()
    }

    var rpm: Double
        get() = shootingMotor.velocity
        set(value) = shootingMotor.setVelocitySetpoint(value)

    var current = shootingMotor.current


    override suspend fun default() {
        periodic {
            if (Limelight.hasValidTarget) {
                //rpm = rpmCurve.GetValue(LimeLight.area)
            }

            if (rpmSetpointEntry.getDouble(0.0) < 0.1) {
                shootingMotor.stop()
            } else {
                rpm = rpmSetpointEntry.getDouble(0.0)
            }
        }
    }
}