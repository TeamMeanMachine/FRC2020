package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc2020.Drive.gyro
import org.team2471.frc2020.Drive.heading
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.input.Controller
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object FrontLimelight : Subsystem("Front Limelight") {
    private val table = NetworkTableInstance.getDefault().getTable("limelight-front")
    private val thresholdTable = table.getSubTable("thresholds")
    private val xEntry = table.getEntry("tx")
    private val yEntry = table.getEntry("ty")
    private val areaEntry = table.getEntry("ta")
    private val camModeEntry = table.getEntry("camMode")
    private val ledModeEntry = table.getEntry("ledMode")
    private val targetValidEntry = table.getEntry("tv")
    private val currentPipelineEntry = table.getEntry("getpipe")
    private val setPipelineEntry = table.getEntry("pipeline")
    private val heightToDistance = MotionCurve()
    private var distanceEntry = table.getEntry("Distance")
    private var positionXEntry = table.getEntry("PositionX")
    private var positionYEntry = table.getEntry("PositionY")
    private var parallaxEntry = table.getEntry("Parallax")
    private var useParallaxEntry = table.getEntry("Toggle Parallax")

    private var angleOffsetEntry = FrontLimelight.table.getEntry("Angle Offset Entry")

    val distance: Length
        get() = 6.17.feet / (14.3 + yTranslation).degrees.tan() //heightToDistance.getValue(yTranslation).feet


    private val tempPIDTable = NetworkTableInstance.getDefault().getTable("fklsdajklfjsadlk;")

    private val rotationPEntry = tempPIDTable.getEntry("Rotation P").apply {
        setPersistent()
        setDefaultDouble(0.012)
    }

    private val rotationDEntry = tempPIDTable.getEntry("Rotation D").apply {
        setPersistent()
        setDefaultDouble(0.1)
    }

    private val useAutoPlaceEntry = table.getEntry("Use Auto Place").apply {
        setPersistent()
        setDefaultBoolean(true)
    }

    var angleOffset: Double = 0.0
        get() = FrontLimelight.angleOffsetEntry.getDouble(0.0)
        set(value) {
            field = value
            FrontLimelight.angleOffsetEntry.setDouble(value)
        }

    val position: Vector2
        get() = Vector2(0.0, 0.0) - Vector2(
            (distance.asFeet * (heading + xTranslation.degrees).sin()),
            (distance.asFeet * (heading + xTranslation.degrees).cos())
        )

    val targetAngle: Angle
        get() {
            return -gyro!!.angle.degrees + xTranslation.degrees
        } //verify that this changes? or is reasonablej

    val targetPoint
        get() = Vector2(
            distance.asFeet * sin(targetAngle.asRadians),
            distance.asFeet * cos(targetAngle.asRadians)
        ) + Drive.position

    var isCamEnabled = false
        set(value) {
            field = value
            camModeEntry.setDouble(0.0)
        }

    var ledEnabled = false
        set(value) {
            field = value
            ledModeEntry.setDouble(if (value) 0.0 else 1.0)
        }

    val xTranslation
        get() = xEntry.getDouble(0.0)

    val yTranslation
        get() = yEntry.getDouble(0.0)

    val area
        get() = areaEntry.getDouble(0.0)

    val rotationP
        get() = rotationPEntry.getDouble(0.012)

    val rotationD
        get() = rotationDEntry.getDouble(0.1)

    var hasValidTarget = false
        get() {
            return targetValidEntry.getDouble(0.0) == 1.0
        }


    var pipeline = 0.0
        get() = currentPipelineEntry.getDouble(0.0)
        set(value) {
            setPipelineEntry.setDouble(value)
            field = value
        }

    val aimError: Double
        get() = xTranslation + FrontLimelight.angleOffset + if(useParallaxEntry.getBoolean(true)) parallax.asDegrees else 0.0


    fun leftAngleOffset() {
            FrontLimelight.angleOffset -= 0.1
    }

    fun rightAngleOffset() {
        FrontLimelight.angleOffset += 0.1
    }


    init {
        isCamEnabled = false
        heightToDistance.storeValue(33.0, 3.0)
        heightToDistance.storeValue(22.0, 7.2)
        heightToDistance.storeValue(9.6, 11.5)
        heightToDistance.storeValue(-4.1, 22.2)
        heightToDistance.storeValue(-20.0, 35.0)
        var i = -4.1
        while (i < 22.5) {
            val tmpDistance = heightToDistance.getValue(i).feet
            //println("$i, ${tmpDistance.asFeet}")
            i += 0.5
        }
        GlobalScope.launch(MeanlibDispatcher) {
            periodic {
                var leftPressed = false
                var rightPressed = false

                if(OI.operatorController.dPad == Controller.Direction.LEFT) {
                    leftPressed = true
                }

                if(OI.operatorController.dPad == Controller.Direction.RIGHT) {
                    rightPressed = true
                }

                if(OI.operatorController.dPad != Controller.Direction.LEFT && leftPressed) {
                    leftPressed = false
                    leftAngleOffset()
                }

                if(OI.operatorController.dPad != Controller.Direction.RIGHT && rightPressed) {
                    rightPressed = false
                    rightAngleOffset()
                }
            }
        }
    }

    fun startUp() {
        distanceEntry = table.getEntry("Distance")
        positionXEntry = table.getEntry("PositionX")
        positionYEntry = table.getEntry("PositionY")
        parallaxEntry = table.getEntry("Parallax")
        useParallaxEntry = table.getEntry("Use Parallax")

        SmartDashboard.setPersistent("Use Parallax")
        useParallaxEntry.setBoolean(true)

        GlobalScope.launch(MeanlibDispatcher) {

            periodic {
                distanceEntry.setDouble(distance.asFeet)
                val savePosition = position
                positionXEntry.setDouble(savePosition.x)
                positionYEntry.setDouble(savePosition.y)
                parallaxEntry.setDouble(parallax.asDegrees)
            }
        }
    }

    val parallax: Angle
        get() {
            val frontGoalPos = Vector2(0.0, 0.0)
            val backGoalPos = Vector2(0.0, 2.0)
            val frontAngle = (frontGoalPos - position).angle.radians
            val backAngle = (backGoalPos - position).angle.radians
            var internalParallax = backAngle - frontAngle
            if (abs(internalParallax.asDegrees) > 4.0) {
                internalParallax = 0.0.degrees
            }
            return internalParallax
        }


    override suspend fun default() {
        ledEnabled = false
        halt()
    }

    override fun reset() {
    }

}


//suspend fun visionDrive() = use(Drive, FrontLimelight, name = "Vision Drive") {
//    FrontLimelight.isCamEnabled = true
//    val timer = Timer()
//    var prevTargetHeading = FrontLimelight.targetAngle
//    var prevTargetPoint = FrontLimelight.targetPoint
//    var prevTime = 0.0
//    timer.start()
//    val rotationPDController = PDController(rotationP, rotationD)
//    periodic {
//        val t = timer.get()
//        val dt = t - prevTime
//
//        // position error
//        val targetPoint = FrontLimelight.targetPoint * 0.5 + prevTargetPoint * 0.5
//        val positionError = targetPoint - Drive.position
//        prevTargetPoint = targetPoint
//
//        val robotHeading = heading
//        val targetHeading = if (BackLimelight.hasValidTarget) positionError.angle.radians else prevTargetHeading
//        val headingError = (targetHeading - robotHeading).wrap()
//        prevTargetHeading = targetHeading
//
//        val turnControl = rotationPDController.update(headingError.asDegrees )
//
//        // send it
//
//
//        Drive.drive(
//            OI.driveTranslation,
//            OI.driveRotation + turnControl,
//            SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous)
//    }
//}