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
import kotlin.math.cos
import kotlin.math.sin

object Limelight : Subsystem("Limelight") {
    private val table = NetworkTableInstance.getDefault().getTable("limelight")
    private val thresholdTable = table.getSubTable("thresholds")
    private val xEntry = table.getEntry("tx")
    private val areaEntry = table.getEntry("ta")
    private val camModeEntry = table.getEntry("camMode")
    private val ledModeEntry = table.getEntry("ledMode")
    private val targetValidEntry = table.getEntry("tv")
    private val currentPipelineEntry = table.getEntry("getpipe")
    private val setPipelineEntry = table.getEntry("pipeline")
    private val areaToDistance = MotionCurve()
    private var distanceEntry = table.getEntry("Distance")

    val distance : Length
        get() = areaToDistance.getValue(area).feet



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

    init {
        isCamEnabled = false
        areaToDistance.storeValue(4.4, 0.0)
        areaToDistance.storeValue(3.2, 5.0)
        areaToDistance.storeValue(2.0, 10.0)
        areaToDistance.storeValue(1.18, 15.0)
        areaToDistance.storeValue(0.77, 20.0)
        areaToDistance.storeValue(0.52, 25.0)
        areaToDistance.storeValue(0.4, 30.0)
        areaToDistance.storeValue(0.2, 35.0)
        areaToDistance.storeValue(0.0, 40.0)
    }
        fun startUp (){
            distanceEntry = table.getEntry("Distance")
        GlobalScope.launch(MeanlibDispatcher) {

            periodic {
                distanceEntry.setDouble(distance.asFeet)
            }
        }
    }


    override fun reset() {
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

        val translationControlField = Vector2(0.0, 0.0) // positionError * 0.06 * OI.driverController.leftTrigger

        val robotHeading = heading
        val targetHeading = if (Limelight.hasValidTarget) positionError.angle.radians else prevTargetHeading
        val headingError = (targetHeading - robotHeading).wrap()
        prevTargetHeading = targetHeading

        val turnControl = rotationPDController.update(headingError.asDegrees )

        // send it


        Drive.drive(
            OI.driveTranslation + translationControlField,
            OI.driveRotation + turnControl,
            SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous)
    }
}
