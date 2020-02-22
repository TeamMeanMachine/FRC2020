package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc2020.Drive.gyro
import org.team2471.frc2020.Drive.heading
import org.team2471.frc2020.Limelight.rotationD
import org.team2471.frc2020.Limelight.rotationP
import org.team2471.frc.lib.control.PDController
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object Limelight : Subsystem("Limelight") {
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

    val distance : Length
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

    val position: Vector2
        get() = Vector2(0.0, 0.0) - Vector2((distance.asFeet * (heading + xTranslation.degrees).sin()), (distance.asFeet * (heading + xTranslation.degrees).cos()))

    val targetAngle: Angle
            get() {
                return -gyro!!.angle.degrees + xTranslation.degrees
            } //verify that this changes? or is reasonablej

    val targetPoint
        get() = Vector2(distance.asFeet * sin(targetAngle.asRadians), distance.asFeet * cos(targetAngle.asRadians)) + Drive.position

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
        get() = targetValidEntry.getDouble(0.0) == 1.0


    var pipeline = 0.0
        get() = currentPipelineEntry.getDouble(0.0)
        set(value) {
            setPipelineEntry.setDouble(value)
            field = value
        }

    val aimError: Double
        get() = xTranslation + parallax.asDegrees

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
    }
        fun startUp() {
            distanceEntry = table.getEntry("Distance")
            positionXEntry = table.getEntry("PositionX")
            positionYEntry = table.getEntry("PositionY")
            parallaxEntry = table.getEntry("Parallax")
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


    override fun reset() {
    }

    val parallax: Angle
        get() {
            val frontGoalPos = Vector2(0.0, 0.0)
            val backGoalPos = Vector2(0.0, 2.0)
            val frontAngle = (frontGoalPos-position).angle.radians
            val backAngle = (backGoalPos-position).angle.radians
            var internalParallax = backAngle-frontAngle
            if (abs(internalParallax.asDegrees) > 4.0) {
                internalParallax = 0.0.degrees
            }
            return internalParallax
    }
}


suspend fun visionDrive() = use(Drive, Limelight, name = "Vision Drive") {
    Limelight.isCamEnabled = true
    val timer = Timer()
    var prevTargetHeading = Limelight.targetAngle
    var prevTargetPoint = Limelight.targetPoint
    var prevTime = 0.0
    timer.start()
    val rotationPDController = PDController(rotationP, rotationD)
    periodic {
        val t = timer.get()
        val dt = t - prevTime

        // position error
        val targetPoint = Limelight.targetPoint * 0.5 + prevTargetPoint * 0.5
        val positionError = targetPoint - Drive.position
        prevTargetPoint = targetPoint

        val robotHeading = heading
        val targetHeading = if (Limelight.hasValidTarget) positionError.angle.radians else prevTargetHeading
        val headingError = (targetHeading - robotHeading).wrap()
        prevTargetHeading = targetHeading

        val turnControl = rotationPDController.update(headingError.asDegrees )

        // send it


        Drive.drive(
            OI.driveTranslation,
            OI.driveRotation + turnControl,
            SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous)
    }
}



suspend fun feederVision() = use(Drive, Limelight, name = "Vision Drive") {
    Limelight.isCamEnabled = true
    val timer = Timer()
    var prevTargetHeading = Limelight.targetAngle
    var prevTargetPoint = Limelight.targetPoint
    var prevTime = 0.0
    timer.start()
    val rotationPDController = PDController(rotationP, rotationD)

    periodic {
        val t = timer.get()
        val dt = t - prevTime

        // position error
        val targetPoint = Limelight.targetPoint * 0.5 + prevTargetPoint * 0.5
        val positionError = targetPoint - Drive.position
        prevTargetPoint = targetPoint

        val robotHeading = heading
        val targetHeading = 0.0.degrees
        val headingError = (targetHeading - robotHeading).wrap()
        prevTargetHeading = targetHeading

        val translationControl = positionError * OI.driverController.leftTrigger * 0.6
        val turnControl = rotationPDController.update(headingError.asDegrees )

        // send it


        Drive.drive(
            OI.driveTranslation + translationControl,
            OI.driveRotation + turnControl,
            SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous)
    }
}