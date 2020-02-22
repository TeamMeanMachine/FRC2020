package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.PowerDistributionPanel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Length
import org.team2471.frc.lib.units.asFeet
import org.team2471.frc.lib.units.feet

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(SparkMaxID(Sparks.SHOOTER), SparkMaxID(Sparks.SHOOTER2))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    val rpmEntry = table.getEntry("RPM")
    val rpmSetpointEntry = table.getEntry("RPM Setpoint")

    val rpmErrorEntry = table.getEntry("RPM Error")

    lateinit var rpmCurve: MotionCurve

    var prepShotOn = false


    init {
        rpmCurve = MotionCurve()

        rpmCurve.setMarkBeginOrEndKeysToZeroSlope(false)
        rpmCurve.storeValue(11.0, 5000.0)
        rpmCurve.storeValue(13.0, 4200.0)
        rpmCurve.storeValue(19.0, 3950.0)
        rpmCurve.storeValue(26.0, 4100.0)
        rpmCurve.storeValue(34.5, 4450.0)
        rpmCurve.storeValue(35.5, 4500.0)
        var dist = 11.0
        while (dist<=34.0) {
            //println("$dist ${rpmCurve.getValue(dist)}")
            dist += 0.2
        }

        shootingMotor.config {
            feedbackCoefficient = 1.0 / (42.0 * 1.01471)
            inverted(true)
            followersInverted(true)
            brakeMode()
            pid {
                p(1.5e-8) //1.5e-8)
                i(0.0)
                d(1.5e-3) //1.5e-3  -- we tried 1.5e9 and 1.5e-9, no notable difference  // we printed values at the MotorController and the wrapper
                f(0.000045)
            }
//            burnSettings()
        }


        rpmSetpointEntry.setDouble(0.0)
//        GlobalScope.launch(MeanlibDispatcher) {
//            periodic {
//                rpmEntry.setDouble(rpm)
//                rpmErrorEntry.setDouble(rpmSetpoint - rpm)
//            }
//        }
    }

    fun setPower(power: Double) {
        shootingMotor.setPercentOutput(power)
    }

    fun stop() {
        shootingMotor.stop()
    }

    fun rpmFromDistance(distance: Length): Double {
        return rpmCurve.getValue(distance.asFeet)
    }

    var rpm: Double
        get() = shootingMotor.velocity
        set(value) = shootingMotor.setVelocitySetpoint(value)

    var rpmSetpoint: Double = 0.0
        get() {
            if (Limelight.hasValidTarget) {
                val rpm2 = rpmFromDistance(Limelight.distance)
                rpmSetpointEntry.setDouble(rpm2)
                return rpm2
            /*} else if(rpmSetpointEntry.value.double > 0.0) {
                return rpmSetpointEntry.value.double */
            } else {
                return 3950.0
            }
        }


    var current = shootingMotor.current

    override suspend fun default() {
        periodic {
            shootingMotor.stop()
        }
    }
}