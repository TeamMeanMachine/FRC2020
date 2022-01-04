package org.team2471.frc2020

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.ADXRS450_Gyro
import edu.wpi.first.wpilibj.AnalogInput
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.control.PDConstantFController
import org.team2471.frc.lib.control.PDController
import org.team2471.frc.lib.coroutines.*
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion.following.SwerveDrive
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion_profiling.following.SwerveParameters
import org.team2471.frc.lib.units.*
import kotlin.math.absoluteValue

object Drive : Subsystem("Drive"), SwerveDrive {

    val navXGyroEntry = NetworkTableInstance.getDefault().getTable(name).getEntry("NavX Gyro")

    var demoMaxSpeed = 1.0


    /**
     * Coordinates of modules
     * **/
    val origModules: Array<SwerveDrive.Module> = arrayOf(
        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_FRONTLEFT)),
            MotorController(SparkMaxID(Sparks.STEER_FRONTLEFT)),
            Vector2(-11.5, 14.0),
            (-315.0).degrees,
            AnalogSensors.SWERVE_FRONT_LEFT
        ),
        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_FRONTRIGHT)),
            MotorController(SparkMaxID(Sparks.STEER_FRONTRIGHT)),
            Vector2(11.5, 14.0),
            (-225.0).degrees,
            AnalogSensors.SWERVE_FRONT_RIGHT
        ),
        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_BACKRIGHT)),
            MotorController(SparkMaxID(Sparks.STEER_BACKRIGHT)),
            Vector2(11.5, -14.0),
            (-135.0).degrees,
            AnalogSensors.SWERVE_BACK_RIGHT
        ),
        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_BACKLEFT)),
            MotorController(SparkMaxID(Sparks.STEER_BACKLEFT)),
            Vector2(-11.5, -14.0),
            (-45.0).degrees,
            AnalogSensors.SWERVE_BACK_LEFT
        )
    )

    override var modules = origModules

    //    val gyro: Gyro? = null
//        val gyro: ADIS16448_IMU? = ADIS16448_IMU()
//    private var navX: NavxWrapper? = NavxWrapper()
//    private var analogDevices: ADXRS450_Gyro? = ADXRS450_Gyro()
//    private var meanGyro : TheBestGyroEver? = TheBestGyroEver()
//    val gyro: NavxWrapper? = NavxWrapper()
    val gyro = if(isCompBotIHateEverything) NavxWrapper() else ADXRS450_Gyro()

//    val gyro = navX /*if (navXGyroEntry.getBoolean(true) && navX != null) navX else if (analogDevices != null) analogDevices else meanGyro*/

    private var gyroOffset = 0.0.degrees

    override var heading: Angle
        get() = gyroOffset - ((gyro?.angle ?: 0.0).degrees.wrap())
        set(value) {
            gyroOffset = value
            gyro?.reset()
        }

    override val headingRate: AngularVelocity
        get() = -(gyro?.rate ?: 0.0).degrees.perSecond

    override var velocity = Vector2(0.0, 0.0)

    override var position = Vector2(0.0, 0.0)

    override var robotPivot = Vector2(0.0, 0.0)

    override var headingSetpoint = 0.0.degrees

    override val parameters: SwerveParameters = SwerveParameters(
        gyroRateCorrection = 0.0,// 0.001,
        kpPosition = 0.32,
        kdPosition = 0.6,
        kPositionFeedForward = 0.0, //075,
        kpHeading = 0.008,
        kdHeading = 0.01,
        kHeadingFeedForward = 0.001
    )

    val aimPDController = PDConstantFController(0.006, 0.032, 0.011) //0.012, 0.03, 0.0
    var lastError = 0.0

    init {
        println("drive init")
        //SmartDashboard.putData("Gyro", gyro!!.getNavX())

/*
        try {
            navX = NavxWrapper()
        } catch(ex : Exception) {
            navX = null
            navXGyroEntry.setBoolean(false)
            println("NavX is having troubles. $ex")
        }

        try {
            analogDevices = ADXRS450_Gyro()
        } catch(ex: Exception) {
            analogDevices = null
            println("Analog Devices is having troubles. $ex")
        }

        try {
            meanGyro =  TheBestGyroEver()
        } catch(ex: Exception) {
            meanGyro = null
            println("Mean Gyro is having troubles. $ex")
        }
*/

        GlobalScope.launch(MeanlibDispatcher) {
            println("in drive global scope")
            val table = NetworkTableInstance.getDefault().getTable(name)

            val headingEntry = table.getEntry("Heading")

            val xEntry = table.getEntry("X")
            val yEntry = table.getEntry("Y")

            val aimPEntry = table.getEntry("p")
            val aimDEntry = table.getEntry("d")
            val aimErrorEntry = table.getEntry("Aim Error")
            val useGyroEntry = table.getEntry("Use Gyro")

            val zeroGyroEntry = table.getEntry("Zero Gyro")

            SmartDashboard.setPersistent("Use Gyro")
            SmartDashboard.setPersistent("Gyro Type")

            val demoMaxSpeedEntry = table.getEntry("Demo Max Speed")

            useGyroEntry.setBoolean(true)
            navXGyroEntry.setBoolean(isCompBotIHateEverything)


//            aimPEntry.setDouble(0.015)
//            aimDEntry.setDouble(0.005)
            periodic {
                val (x, y) = position
                xEntry.setDouble(x)
                yEntry.setDouble(y)
                headingEntry.setDouble(heading.asDegrees)
                aimErrorEntry.setDouble(FrontLimelight.aimError)
//                aimPDController.p = aimPEntry.getDouble(0.015)
//                aimPDController.d = aimDEntry.getDouble(0.005)

                demoMaxSpeed = demoMaxSpeedEntry.getDouble(1.0)
            }
        }
        println("drive init complete")
    }

    fun zeroGyro() = gyro?.reset()

    

    override suspend fun default() {
        val limelightTable = NetworkTableInstance.getDefault().getTable("limelight-front")
        val xEntry = limelightTable.getEntry("tx")
        val angleEntry = limelightTable.getEntry("ts")
        val table = NetworkTableInstance.getDefault().getTable(name)
        periodic {
            var turn = 0.0
            if (OI.driveRotation.absoluteValue > 0.001) {
                turn = OI.driveRotation
            } else if (FrontLimelight.hasValidTarget && Shooter.prepShotOn) {
                turn = aimPDController.update(FrontLimelight.aimError)
//                println("FrontLimeLightAimError=${FrontLimelight.aimError}")
            }
//            printEncoderValues()

            val direction = OI.driverController.povDirection
            if (direction==270.0.degrees)
                headingSetpoint = -112.0.degrees
            else if (direction == 0.0.degrees)
                headingSetpoint = direction

            drive(
                OI.driveTranslation/* * demoMaxSpeed*/,
                turn/* * demoMaxSpeed*/,
                //true,
                if (Drive.gyro != null) SmartDashboard.getBoolean("Use Gyro", true)
                        && !DriverStation.getInstance().isAutonomous else false,
                true
            )
        }
    }

    fun printEncoderValues() {
        for (moduleCount in 0..3) {
            print("$moduleCount=${round((modules[moduleCount] as Module).analogAngle.asDegrees, 2)}   ")
        }
    }

    fun initializeSteeringMotors() {
        for (moduleCount in 0..3) {
            val module = (modules[moduleCount] as Module)
            module.turnMotor.setRawOffset(module.analogAngle)
           // println("Module: $moduleCount analogAngle: ${module.analogAngle}")
        }
    }

    fun resetDriveMotors() {
        for (moduleCount in 0..3) {
            val module = (modules[moduleCount] as Module)
            module.driveMotor.restoreFactoryDefaults()
            println("For module $moduleCount, drive motor's factory defaults were restored.")
        }
    }

    fun resetSteeringMotors() {
        for (moduleCount in 0..3) {
            val module = (modules[moduleCount] as Module)
            module.turnMotor.restoreFactoryDefaults()
            println("For module $moduleCount, turn motor's factory defaults were restored.")
        }
    }

    fun brakeMode() {
        for (moduleCount in 0..3) {
            val module = (modules[moduleCount] as Module)
            module.driveMotor.brakeMode()
        }
    }

    fun coastMode() {
        for (moduleCount in 0..3) {
            val module = (modules[moduleCount] as Module)
            module.driveMotor.coastMode()
        }
    }

    class Module(
        val driveMotor: MotorController,
        val turnMotor: MotorController,
        override val modulePosition: Vector2,
        override val angleOffset: Angle,
        private val analogAnglePort: Int
    ) : SwerveDrive.Module {
        companion object {
            private const val ANGLE_MAX = 983
            private const val ANGLE_MIN = 47

            private val P = 0.0075 //0.010
            private val D = 0.00075
        }

        override val angle: Angle
            get() = turnMotor.position.degrees

        private val analogAngleInput = AnalogInput(analogAnglePort)

        val analogAngle: Angle
            get() = (((analogAngleInput.value - 170.0) / (3888.0 - 170.0) * 360.0).degrees + angleOffset).wrap()

        val driveCurrent: Double
            get() = driveMotor.current

        private val pdController = PDController(P, D)

        override val speed: Double
            get() = driveMotor.velocity

        override val currDistance: Double
            get() = driveMotor.position

        override var prevDistance: Double = 0.0

        override fun zeroEncoder() {
            driveMotor.position = 0.0
        }

        override var angleSetpoint: Angle = 0.0.degrees
            set(value) = turnMotor.setPositionSetpoint((angle + (value - angle).wrap()).asDegrees)

        override fun setDrivePower(power: Double) {
            driveMotor.setPercentOutput(power)
        }

        val error: Angle
            get() = turnMotor.closedLoopError.degrees

        init {
            turnMotor.config(20) {
                // this was from lil bois bench test of swerve
                feedbackCoefficient = 360.0 / 823.2
                setRawOffsetConfig(analogAngle)
                inverted(true)
                setSensorPhase(false)
                pid {
                    p(0.000075)
                    d(0.00025)
                }
//                burnSettings()
            }

            driveMotor.config {
                brakeMode()
                feedbackCoefficient = 1.0 / 246.0
                currentLimit(30)
                openLoopRamp(0.15)
//                burnSettings()
            }

            GlobalScope.launch {
                val table = NetworkTableInstance.getDefault().getTable(name)
                val pSwerveEntry = table.getEntry("Swerve P").apply {
                    setPersistent()
                    setDefaultDouble(0.0075)
                }
                val dSwerveEntry = table.getEntry("Swerve D").apply {
                    setPersistent()
                    setDefaultDouble(0.00075)
                }
            }

        }

        override fun driveWithDistance(angle: Angle, distance: Length) {
            driveMotor.setPositionSetpoint(distance.asFeet)
            val error = (angle - this.angle).wrap()
            pdController.update(error.asDegrees)
        }

        override fun stop() {
            driveMotor.stop()
            //turnMotor.stop()
        }
    }
}
