package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc2020.Drive.gyro
import org.team2471.frc2020.Drive.heading
import org.team2471.frc2020.BackLimelight.rotationD
import org.team2471.frc2020.BackLimelight.rotationP
import org.team2471.frc.lib.control.PDController
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.halt
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.*
import org.team2471.frc2020.BackLimelight.area
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object BackLimelight : Subsystem("Back Limelight") {
    private val table = NetworkTableInstance.getDefault().getTable("limelight-back")
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
    private var positionXEntry = table.getEntry("PositionX")
    private var positionYEntry = table.getEntry("PositionY")
    private var parallaxEntry = table.getEntry("Parallax")


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
        get() = rotationPEntry.getDouble(0.0)//0.0001)

    val rotationD
        get() = rotationDEntry.getDouble(0.0)

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

    override suspend fun default() {
        ledEnabled = false
        halt()
    }

    override fun reset() {
    }

}

suspend fun feederStationVision() = use(Drive, BackLimelight, Intake, name = "Vision Drive") {
    println("Got into feederStationVision(). Hi.")
    BackLimelight.isCamEnabled = true
    val timer = Timer()
    var prevTime = 0.0
    timer.start()
    BackLimelight.ledEnabled = true
    val rotationPDController = PDController(0.005, 0.0)
    try {
        Intake.extend = true
        Intake.setPower (Intake.INTAKE_POWER)

        periodic {
            println("In feederStationVision() periodic. Hi.")
            val t = timer.get()


            val robotHeading = heading
            val targetHeading = 0.0.degrees
            val headingError = (targetHeading - robotHeading).wrap()

            val translationControl = if (BackLimelight.hasValidTarget)
                Vector2(
                    BackLimelight.xTranslation * 0.01 * OI.driverController.leftTrigger,
                    OI.driverController.leftTrigger * 0.4 * (if (area < 7) (1 / area) else 0.2)
                ) //im sorry mom
            else
                Vector2(0.0, OI.driverController.leftTrigger * 0.4)

            println("tx: ${BackLimelight.xTranslation} x: ${BackLimelight.xTranslation * 0.01 * OI.driverController.leftTrigger}. Hi.")
            val turnControl = rotationPDController.update(headingError.asDegrees)
            println(headingError)
            // send it


            Drive.drive(
                OI.driveTranslation - translationControl,
                OI.driveRotation + turnControl,
                SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous
            )

            if(OI.operatorController.rightBumper) this.stop()
        }
    } finally {
        Intake.extend = false
        Intake.setPower(0.0)
        BackLimelight.ledEnabled = false
    }
}