package org.team2471.BunnyBots2019

import edu.wpi.first.wpilibj.Timer
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees
import kotlin.math.absoluteValue

object Slurpy : Subsystem("Slurpy") {
    var shoulderStealAngle: Angle = SlurpyPose.HIGH_STEAL_POSE.shoulderAngle
    const val SHOULDER_OFFSET = -53.0
    const val WRIST_OFFSET = 48.0
    var slurpyCurrentPose = SlurpyPose.START_POSE
    val cubeIntakeMotor = MotorController(SparkMaxID(Sparks.CUBE_INTAKE))
    val shoulderMotor = MotorController(SparkMaxID(Sparks.ARM_MOVEMENT)).config(20) {
        feedbackCoefficient = 90.0 / 1014.0
        //setRawOffsetConfig(analogAngle)
        inverted(false)
        setSensorPhase(false)
        pid {
            p(0.00000375) //0.000075
            d(0.0) //0.00025
        }
        coastMode()
    }

    val wristMotor = MotorController(SparkMaxID(Sparks.WRIST_MOVEMENT)).config(20) {
        feedbackCoefficient = 90.0 / 1014.0
        //setRawOffsetConfig(analogAngle)
        inverted(true)
        setSensorPhase(false)
        pid {
            p(0.00000375) //0.000075
            d(0.0) //0.00025
        }
        coastMode()
    }

    var shoulderAngle: Angle
        get() = shoulderMotor.position.degrees + SHOULDER_OFFSET.degrees
        set(value) = shoulderMotor.setPositionSetpoint(value.asDegrees - SHOULDER_OFFSET)

    var wristAngle: Angle
        get() = wristMotor.position.degrees + WRIST_OFFSET.degrees
        set(value) = wristMotor.setPositionSetpoint(value.asDegrees - WRIST_OFFSET)

    val shoulderAngleError: Angle
        get() = shoulderMotor.closedLoopError.degrees

    val wristAngleError: Angle
        get() = wristMotor.closedLoopError.degrees


    fun resetShoulderAngle() {
        shoulderMotor.position = 0.0
    }

    fun resetWristAngle() {
        wristMotor.position = 0.0
    }

    override fun reset() {

    }

    suspend fun prepareSlurpy() = use(Slurpy){
        animateToPose(SlurpyPose.SCORING_POSE, 0.5)
        delay(0.5)
        animateToPose(SlurpyPose.SAFETY_POSE)
    }


    suspend fun goToPose(targetSlurpyPose: SlurpyPose) = use(Slurpy) {
        val timer = Timer().apply { start() }

        periodic {
            Slurpy.shoulderMotor.setPositionSetpoint(targetSlurpyPose.shoulderAngle.asDegrees)
            Slurpy.wristMotor.setPositionSetpoint(targetSlurpyPose.wristAngle.asDegrees)
            val shoulderError = Slurpy.shoulderAngleError
            val wristError = Slurpy.wristAngleError
            if (shoulderError.asDegrees.absoluteValue < 10.0 &&
                wristError.asDegrees.absoluteValue < 10.0 &&
                timer.get() > 0.3
            ) {
                stop()
            }
        }
    }

    suspend fun animateToPose(slurpyPose: SlurpyPose, time: Double = 1.0) = use(Slurpy) {
        slurpyCurrentPose = slurpyPose
        val shoulderCurve = MotionCurve()
        val wristCurve = MotionCurve()

        shoulderCurve.storeValue(0.0, Slurpy.shoulderAngle.asDegrees)
        wristCurve.storeValue(0.0, Slurpy.wristAngle.asDegrees)

        shoulderCurve.storeValue(time, slurpyPose.shoulderAngle.asDegrees)
        wristCurve.storeValue(time, slurpyPose.wristAngle.asDegrees)

        val timer = Timer()
        timer.start()

        periodic {
            val t = timer.get()

            /**println(
                "Shoulder Error: ${round(
                    Slurpy.shoulderAngle.asDegrees - shoulderCurve.getValue(t),
                    2
                )} and Wrist Error: ${round(Slurpy.wristAngle.asDegrees - wristCurve.getValue(t), 2)} at time ${round(
                    t,
                    2
                )}"
            )**/

            Slurpy.shoulderAngle = shoulderCurve.getValue(t).degrees
            Slurpy.wristAngle = wristCurve.getValue(t).degrees

            if (t > time) {
                this.stop()
            }
        }
        println("Animation took ${round(timer.get(), 2)} seconds")
    }

    suspend fun intakeCube() = use(Slurpy) {
        try {
            println("starting")
            animateToPose(SlurpyPose.GROUND_POSE)
            val timer = Timer()
            timer.start()

            periodic {
                val t = timer.get()
                if (OI.driverController.rightBumper && t < 7.0 && !OI.operatorController.a) {
                    cubeIntakeMotor.setPercentOutput(0.5)
                } else {
                    cubeIntakeMotor.setPercentOutput(0.0)
                    this.stop()
                }
            }
            animateToPose(SlurpyPose.SAFETY_POSE)
            suspendUntil(20, {OI.operatorController.a || OI.operatorController.b})
            if (OI.operatorController.a) {
                continueGroundIntake()
            }
            println("end")
        } finally {
            println("finally")
            cubeIntakeMotor.setPercentOutput(0.0)
            animateToPose(SlurpyPose.SAFETY_POSE, 0.75)

        }
    }

    suspend fun stealCube() = use(Slurpy) {
        try {
            val shoulderToWristCurve = MotionCurve()

            shoulderToWristCurve.storeValue(SlurpyPose.LOW_STEAL_POSE.shoulderAngle.asDegrees, SlurpyPose.LOW_STEAL_POSE.wristAngle.asDegrees)
            shoulderToWristCurve.storeValue(SlurpyPose.HIGH_STEAL_POSE.shoulderAngle.asDegrees, SlurpyPose.HIGH_STEAL_POSE.wristAngle.asDegrees)

            if (slurpyCurrentPose != SlurpyPose.SAFETY_POSE) {
                (SlurpyPose.SAFETY_POSE)
            }
            animateToPose(SlurpyPose(shoulderStealAngle, shoulderToWristCurve.getValue(shoulderStealAngle.asDegrees).degrees))
            val timer = Timer()

            periodic {
                if (!OI.driverController.rightBumper) {
                    shoulderStealAngle -= OI.operatorLeftY.degrees * 0.5
                    if (shoulderStealAngle < (45.0).degrees) {
                        shoulderStealAngle = (45.0).degrees
                    } else if (shoulderStealAngle > (120.0).degrees) {
                        shoulderStealAngle = (120.0).degrees
                    }

                    println("shoulder angle: $shoulderStealAngle wrist angle: ${shoulderToWristCurve.getValue(shoulderStealAngle.asDegrees).degrees}")

                    shoulderAngle = shoulderStealAngle
                    wristAngle = shoulderToWristCurve.getValue(shoulderStealAngle.asDegrees).degrees
                } else {
                    this.stop()
                }
//                println("Shoulder Setpoint is ${round(shoulderStealAngle.asDegrees, 2)}, Right Thumbstick y is ${round(OI.operatorController.rightThumbstickY, 2)}, and current position is ${round(
//                    shoulderAngle.asDegrees, 2)}")
            }

            timer.start()
            periodic {
                val t = timer.get()
                if (OI.driverController.rightBumper && t < 7.0 && shoulderAngle > 45.0.degrees) {
                    cubeIntakeMotor.setPercentOutput(0.5)
                    shoulderMotor.stop()
                } else {
                    this.stop()
                }
            }
            animateToPose(SlurpyPose.SAFETY_POSE)
            suspendUntil(20, {OI.operatorController.a || OI.operatorController.b})
            if (OI.operatorController.a) {
                continueGroundIntake()
            }
        } finally {
            cubeIntakeMotor.setPercentOutput(0.0)
            animateToPose(SlurpyPose.SAFETY_POSE)
        }
    }

    suspend fun continueGroundIntake() {
        animateToPose(SlurpyPose.SCORING_POSE)
        cubeIntakeMotor.setPercentOutput(-0.4)
        delay(3.0)
    }
}