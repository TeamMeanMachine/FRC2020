package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Length
import org.team2471.frc.lib.units.asFeet

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(FalconID(Falcons.SHOOTER),FalconID(Falcons.SHOOTER2))
    private val hoodMotor = MotorController(SparkMaxID(Sparks.HOOD))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    val rpmEntry = table.getEntry("RPM")
    val rpmSetpointEntry = table.getEntry("RPM Setpoint")

    val rpmErrorEntry = table.getEntry("RPM Error")
    val rpmOffsetEntry = table.getEntry("RPM Offset")

    lateinit var rpmCurve: MotionCurve

    var prepShotOn = false

    val shooterPower = 0.7


    init {
        println("shooter init")
        hoodMotor.config {
            brakeMode()
        }

        rpmCurve = MotionCurve()

        rpmCurve.setMarkBeginOrEndKeysToZeroSlope(false)

        rpmCurve.storeValue(11.0, 7680.0) //tuned 3/5
        rpmCurve.storeValue(13.0, 7300.0) //tuned 3/5
        rpmCurve.storeValue(18.0, 6590.0) //tuned 3/5
        rpmCurve.storeValue(26.0, 6540.0) //tuned 3/5
        rpmCurve.storeValue(34.0, 7530.0) //tuned(-ish) 3/5

        var dist = 11.0
        while (dist <= 34.0) {
            //println("$dist ${rpmCurve.getValue(dist)}")
            dist += 0.2
        }

        shootingMotor.config {
            feedbackCoefficient = 60.0 / (2048 * 0.49825)
            inverted(true)
            followersInverted(false)
            brakeMode()
            pid {
                p(0.8e-5) //1.5e-8)
                i(0.0)//i(0.0)
                d(0.0)//d(1.5e-3) //1.5e-3  -- we tried 1.5e9 and 1.5e-9, no notable difference  // we printed values at the MotorController and the wrapper
                f(0.03696) //0.000045
            }
//            burnSettings()
        }
        rpmSetpointEntry.setDouble(6000.0)
        println("right before globalscope")
        GlobalScope.launch(MeanlibDispatcher) {
            println("in global scope")
            var upPressed = false
            var downPressed = false
            rpmOffset = rpmOffsetEntry.getDouble(1600.0)

            periodic {
                //                print(".")
//                println(hoodMotor.analogAngle)
//                hoodMotor.setPercentOutput( 0.25 * (OI.operatorController.leftTrigger - OI.operatorController.rightTrigger))
                rpmEntry.setDouble(rpm)
                rpmErrorEntry.setDouble(rpmSetpoint - rpm)

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
//            if (FrontLimelight.hasValidTarget) {
//                val rpm2 = rpmFromDistance(FrontLimelight.distance) + rpmOffset
//                rpmSetpointEntry.setDouble(rpm2)
//                return rpm2
//            } else {
//                field = rpmCurve.getValue(20.0) + rpmOffset
//                rpmSetpointEntry.setDouble(field)
//                return field
//            }

            return rpmSetpointEntry.getDouble(2000.0)
        }

    var rpmOffset: Double = 0.0 //400.0
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