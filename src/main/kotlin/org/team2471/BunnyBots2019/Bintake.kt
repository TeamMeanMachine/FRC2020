package org.team2471.BunnyBots2019

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import org.team2471.BunnyBots2019.Slurpy.animateToPose
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.util.Timer
import kotlin.concurrent.timer

object Bintake : Subsystem("Bintake") {

    var bintakeCurrentPose = BintakePose.SAFETY_POSE

    val pivotMotor = MotorController(TalonID(Talons.BINTAKE_ROTATION)).config {
        encoderType(FeedbackDevice.CTRE_MagEncoder_Absolute)
        encoderContinuous(false)
        // rawOffset(-224)
        sensorPhase(true)
        inverted(true)
        feedbackCoefficient = 90.0 / 1014.0 // needs modification
        pid {
            p(0.0001) //0.000075
            d(0.0) //0.00025
        }
    }

    val intakeMotor = MotorController(TalonID(Talons.BINTAKE_INTAKE)).config {
    }

    val current : Double
        get() = intakeMotor.current

    var angle: Angle
        get() = pivotMotor.position.degrees
        set(value) = pivotMotor.setPositionSetpoint(value.asDegrees)

    fun intake(power: Double) {
        intakeMotor.setPercentOutput(power)
        println(power)
    }

    fun pivot(power: Double) {
        pivotMotor.setPercentOutput(power)
    }

    suspend fun prepareBintake() {
        animateToPose(BintakePose.SAFETY_POSE)
    }

    suspend fun animateToPose(bintakePose: BintakePose, time: Double = 0.5) = use(Bintake) {
        bintakeCurrentPose = bintakePose
        val motionCurve = MotionCurve()

        motionCurve.storeValue(0.0, Bintake.angle.asDegrees)
        motionCurve.storeValue(time, bintakePose.angle.asDegrees)

        val timer = Timer()
        timer.start()

        periodic {
            val t = timer.get()

            println(
                "Bintake Error: ${round(
                    Bintake.angle.asDegrees - motionCurve.getValue(t),
                    2
                )}"
            )

            angle = motionCurve.getValue(t).degrees

            if (t > time) {
                this.stop()
            }
        }
        println("Animation took ${round(timer.get(), 2)} seconds")
    }

    override fun reset() {

    }

    suspend fun intakeBinCubes() = use(Bintake) {
        try {
            val timer = Timer()
            timer.start()

            animateToPose(BintakePose.INTAKE_POSE)
            intakeMotor.setPercentOutput(-1.0)
            delay(0.5)
            periodic {
                if (OI.operatorController.rightBumper && intakeMotor.current < 40.0) {
                    intakeMotor.setPercentOutput(-1.0)
                } else {
                    this.stop()
                }
            }

            if (current > 45.0 ) {
                delay(0.5)
                intakeMotor.setPercentOutput(-0.5)
                animateToPose(BintakePose.SCORING_POSE)
                delay(0.5)
                animateToPose(BintakePose.SPITTING_POSE)
                intakeMotor.setPercentOutput(0.5)
                delay(1.0)
            }
        } finally {
            intakeMotor.setPercentOutput(0.0)
            animateToPose(BintakePose.SAFETY_POSE)
        }

    }

}
