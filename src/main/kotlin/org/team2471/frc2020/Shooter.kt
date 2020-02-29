package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Length
import org.team2471.frc.lib.units.asFeet

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(SparkMaxID(Sparks.SHOOTER), SparkMaxID(Sparks.SHOOTER2))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    val rpmEntry = table.getEntry("RPM")
    val rpmSetpointEntry = table.getEntry("RPM Setpoint")

    val rpmErrorEntry = table.getEntry("RPM Error")
    val rpmOffsetEntry = table.getEntry("RPM Offset")

    lateinit var rpmCurve: MotionCurve

    var prepShotOn = false


    init {
        println("shooter init")
        rpmCurve = MotionCurve()

        rpmCurve.setMarkBeginOrEndKeysToZeroSlope(false)
/*
        rpmCurve.storeValue(11.0, 5000.0)
        rpmCurve.storeValue(13.0, 4500.0)
        rpmCurve.storeValue(19.0, 4120.0)
        rpmCurve.storeValue(26.0, 4200.0)
        rpmCurve.storeValue(34.5, 4850.0)
        rpmCurve.storeValue(35.5, 4900.0)
*/
        rpmCurve.storeValue(13.5, 5050.0)
        rpmCurve.storeValue(15.0, 4660.0)
        rpmCurve.storeValue(19.0, 4270.0)
        rpmCurve.storeValue(30.0, 4150.0)
        rpmCurve.storeValue(38.0, 4320.0)
        rpmCurve.storeValue(53.0, 5040.0)

        var dist = 11.0
        while (dist <= 34.0) {
            //println("$dist ${rpmCurve.getValue(dist)}")
            dist += 0.2
        }

        shootingMotor.config {
            feedbackCoefficient = 1.0 / (42.0 * 1.01471)
            inverted(true)
            followersInverted(true)
            brakeMode()
            pid {
                p(0.4e-8) //1.5e-8)
                i(0.0)//i(0.0)
                d(0.0)//d(1.5e-3) //1.5e-3  -- we tried 1.5e9 and 1.5e-9, no notable difference  // we printed values at the MotorController and the wrapper
                f(0.000042) //0.000045
            }
//            burnSettings()
        }


        rpmSetpointEntry.setDouble(0.0)
        println("right before globalscope")
        GlobalScope.launch(MeanlibDispatcher) {
            println("in global scope")
            var upPressed = false
            var downPressed = false
            rpmOffsetEntry.setPersistent()
            periodic {
                //                print(".")
                rpmEntry.setDouble(rpm)
                rpmErrorEntry.setDouble(rpmSetpoint - rpm)

                rpmOffsetEntry.setDouble(rpmOffset)

                if (OI.operatorController.dPad == Controller.Direction.UP) {
                    upPressed = true
                } else if (OI.operatorController.dPad == Controller.Direction.DOWN) {
                    downPressed = true
                }

                if(OI.operatorController.dPad != Controller.Direction.UP && upPressed) {
                    upPressed = false
                    incrementRpmOffset()
                }

                if(OI.operatorController.dPad != Controller.Direction.DOWN && downPressed) {
                    downPressed = false
                    decrementRpmOffset()
                }

            }
        }
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
            if (FrontLimelight.hasValidTarget) {
                val rpm2 = rpmFromDistance(FrontLimelight.distance) + rpmOffset
                rpmSetpointEntry.setDouble(rpm2)
                return rpm2
                /*} else if(rpmSetpointEntry.value.double > 0.0) {
                    return rpmSetpointEntry.value.double */
            } else {
                return 4050.0 + rpmOffset
            }
        }

    var rpmOffset: Double = 400.0
        get() = rpmOffsetEntry.getDouble(400.0)
        set(value) {
            field = value
            rpmOffsetEntry.setDouble(value)
        }

    fun incrementRpmOffset() {
        rpmOffset += 20.0
    }

    fun decrementRpmOffset() {
        rpmOffset -= 20.0
    }

    var current = shootingMotor.current

    override suspend fun default() {
        periodic {
            shootingMotor.stop()
        }
    }
}