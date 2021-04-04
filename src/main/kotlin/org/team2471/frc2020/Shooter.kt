package org.team2471.frc2020

import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.FalconID
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Length
import org.team2471.frc.lib.units.asFeet
import org.team2471.frc.lib.util.Timer

object Shooter : Subsystem("Shooter") {
    private val shootingMotor = MotorController(FalconID(Falcons.SHOOTER),FalconID(Falcons.SHOOTER2))
    private val hoodMotor = MotorController(SparkMaxID(Sparks.HOOD))

    private val table = NetworkTableInstance.getDefault().getTable(name)
    val rpmEntry = table.getEntry("RPM")
    val rpmSetpointEntry = table.getEntry("RPM Setpoint")
    val hoodEntry = table.getEntry("Hood")
    val hoodSetpointEntry = table.getEntry("Hood Setpoint")

    val rpmErrorEntry = table.getEntry("RPM Error")
    val rpmOffsetEntry = table.getEntry("RPM Offset")

    var hoodCurve: MotionCurve

    var prepShotOn = false

    val shooterPower = 0.7



    init {
        println("shooter init")
        hoodMotor.config {
            brakeMode()
            feedbackCoefficient = 63.0 / 475.0 // deg / tick
            pid {
                p(.00005)
            }
        }

        hoodCurve = MotionCurve()

        hoodCurve.setMarkBeginOrEndKeysToZeroSlope(false)

        hoodCurve.storeValue(7.6, 28.0) //tuned 4/3
        hoodCurve.storeValue(10.2, 36.0) //tuned 4/3
        hoodCurve.storeValue(14.5, 42.0) //tuned 4/3
        hoodCurve.storeValue(19.6, 45.0) //tuned 4/3
        hoodCurve.storeValue(25.0, 45.0) //tuned 4/3


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
        hoodSetpointEntry.setDouble(0.0)
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
                hoodEntry.setDouble(hoodEncoderPosition)

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

    fun hoodSetPower(power: Double) {
        hoodMotor.setPercentOutput(power)
    }

    fun stop() {
        shootingMotor.stop()
    }

    fun hoodStop() {
        hoodMotor.stop()
    }

    var rpm: Double
        get() = shootingMotor.velocity
        set(value) = shootingMotor.setVelocitySetpoint(value)

    var hoodEncoderPosition: Double
        get() = hoodMotor.position
        set(value) {
            hoodMotor.setPositionSetpoint(value.coerceIn(3.0, 60.0))
        }

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

            return rpmSetpointEntry.getDouble(6000.0)
        }

    var rpmOffset: Double = 0.0 //400.0
        set(value) {
            field = value
            rpmOffsetEntry.setDouble(value)
        }

    var hoodSetpoint: Double = 0.0
        get() = hoodSetpointEntry.getDouble(0.0)

    fun incrementRpmOffset() {
        rpmOffset += 20.0
    }

    fun decrementRpmOffset() {
        rpmOffset -= 20.0
    }

    suspend fun resetHoodEncoder() = use(this) {
        //wip
        var t = Timer()
        hoodSetPower(-0.1)
        t.start()
        periodic {
            if (t.get() > 1.0) {
                println("Second passed. Hi.")
                this.stop()
            }
            if (hoodMotor.current > 20.0 && t.get() > 0.05) {
                println("Current limit hit. Hi.")
                this.stop()
            }
        }
        hoodSetPower(0.0)
        hoodMotor.position = 0.0
    }

    var current = shootingMotor.current

    override suspend fun default() {
        periodic {
            shootingMotor.stop()
            hoodMotor.stop()
        }
    }
}