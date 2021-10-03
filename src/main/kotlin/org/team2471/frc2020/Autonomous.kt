package org.team2471.frc2020

import edu.wpi.first.networktables.EntryListenerFlags
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.parallel
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion.following.driveAlongPath
import org.team2471.frc.lib.motion_profiling.Autonomi
import org.team2471.frc.lib.motion_profiling.Autonomous
import org.team2471.frc.lib.motion_profiling.Path2D
import org.team2471.frc.lib.units.asFeet
import org.team2471.frc.lib.util.measureTimeFPGA
import org.team2471.frc2020.actions.*
import java.io.File

private lateinit var autonomi: Autonomi


enum class Side {
    LEFT,
    RIGHT;

    operator fun not(): Side = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

private var startingSide = Side.RIGHT


object AutoChooser {
    private val isRedAllianceEntry = NetworkTableInstance.getDefault().getTable("FMSInfo").getEntry("isRedAlliance")

    var cacheFile : File? = null
    var redSide: Boolean = true
        get() = isRedAllianceEntry.getBoolean(true)
        set(value) {
            field = value
            isRedAllianceEntry.setBoolean(value)
        }

    private val lyricsChooser = SendableChooser<String?>().apply {
        setDefaultOption("Country roads", "Country roads")
        addOption("take me home", "take me home")
    }

    private val testAutoChooser = SendableChooser<String?>().apply {
        addOption("None", null)
        addOption("20 Foot Test", "20 Foot Test")
        addOption("8 Foot Straight", "8 Foot Straight")
        addOption("2 Foot Circle", "2 Foot Circle")
        addOption("4 Foot Circle", "4 Foot Circle")
        addOption("8 Foot Circle", "8 Foot Circle")
        addOption("Hook Path", "Hook Path")
        setDefaultOption("90 Degree Turn", "90 Degree Turn")
    }

    private val autonomousChooser = SendableChooser<String?>().apply {
        setDefaultOption("Tests", "testAuto")
        addOption("5 Ball Trench Run", "trenchRun5")
        addOption("10 Ball Shield Generator", "shieldGenerator10")
        addOption("8 Ball Shield Generator", "shieldGenerator8")
        addOption("8 Ball Trench Run", "trenchRun8")
        addOption("Carpet Bias Test", "carpetBiasTest")
        addOption("Helper Paths", "helperPaths")
        addOption("Slalom Auto", "slalomAuto")
        addOption("Barrel Racing Auto", "barrelRacingAuto")

    }

    init {
//        println("Got into Autonomous' init. Hi. 222222222222222222222")
        SmartDashboard.putData("Best Song Lyrics", lyricsChooser)
        SmartDashboard.putData("Tests", testAutoChooser)
        SmartDashboard.putData("Autos", autonomousChooser)

        try {

            cacheFile = File("/home/lvuser/autonomi.json")
            if (cacheFile  != null) {
                autonomi = Autonomi.fromJsonString(cacheFile?.readText())
                println("Autonomi cache loaded.")
            } else {
                println("Autonomi failed to load!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! RESTART ROBOT!!!!!!")
            }
        } catch (_: Throwable) {
            DriverStation.reportError("Autonomi cache could not be found", false)
            autonomi = Autonomi()
        }
        println("In Auto Init. Before AddListener. Hi.")
        NetworkTableInstance.getDefault()
            .getTable("PathVisualizer")
            .getEntry("Autonomi").addListener({ event ->
                println("Automous change detected")
                val json = event.value.string
                if (!json.isEmpty()) {
                    val t = measureTimeFPGA {
                        autonomi = Autonomi.fromJsonString(json)
                    }
                    println("Loaded autonomi in $t seconds")
                    if (cacheFile != null) {
                        println("CacheFile != null. Hi.")
                        cacheFile!!.writeText(json)
                    } else {
                        println("cacheFile == null. Hi.")
                    }
                    println("New autonomi written to cache")
                } else {
                    autonomi = Autonomi()
                    DriverStation.reportWarning("Empty autonomi received from network tables", false)
                }
            }, EntryListenerFlags.kImmediate or EntryListenerFlags.kNew or EntryListenerFlags.kUpdate)
    }

    suspend fun autonomous() = use(Drive, name = "Autonomous") {
        println("Got into Auto fun autonomous. Hi. 888888888888888")
        val selAuto = SmartDashboard.getString("Autos/selected", "no auto selected")
        SmartDashboard.putString("autoStatus", "init")
        println("Selected Auto = *****************   $selAuto ****************************")
        when (selAuto) {
            "Tests" -> testAuto()
            "5 Ball Trench Run" -> trenchRun5()
            "10 Ball Shield Generator" -> shieldGenerator10()
            "8 Ball Shield Generator" -> shieldGenerator8()
            "8 Ball Trench Run" -> trenchRun8()
            "Carpet Bias Test" -> carpetBiasTest()
            "Helper Paths" -> feederToYeeter()
            "Slalom Auto" -> slalom()
            "Barrel Racing Auto" -> barrelRacingAuto()
            else -> println("No function found for ---->$selAuto<-----")
        }
        SmartDashboard.putString("autoStatus", "complete")
    }

    suspend fun testAuto() {
        val testPath = SmartDashboard.getString("Tests/selected", "no test selected") // testAutoChooser.selected
        if (testPath != null) {
            val testAutonomous = autonomi["Tests"]
            val path = testAutonomous[testPath]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun trenchRun5() = use(Drive, Shooter, Intake, Feeder, FrontLimelight) {
        try {
            val auto = autonomi["5 Ball Trench Run"]
            if (auto != null) {
                Intake.setPower(Intake.INTAKE_POWER)
                var path = auto["01- Intake 2 Cells"]
                Drive.driveAlongPath(path, true)
//            autoIntakeStop()
                path = auto["02- Shooting Position"]
                Drive.driveAlongPath(path, false)
                shootingMode(5)
            }
        } finally {
            FrontLimelight.ledEnabled = false
        }
    }

    suspend fun shieldGenerator10() = use(Drive, Shooter, Intake, Feeder, FrontLimelight) {
        try {
            FrontLimelight.ledEnabled = true
            val auto = autonomi["10 Ball Shield Generator"]
//            var auto = autonomi["Red 10 Ball Shield Generator"]
//            if (!redSide) auto = autonomi["Blue 10 Ball Shield Generator"]
            println(auto == null)
            if (true){//auto != null) {
                Intake.setPower(1.0) //Intake.INTAKE_POWER)
                Intake.extend = true
                var path = auto["01- Intake 2 Cells"]
                Drive.driveAlongPath(path, true, 0.125)
                delay(0.25)
                Intake.setPower(0.5)
//                Intake.extend = false
                parallel ({
                    delay(path.duration * 0.25)
                    val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)
                    Shooter.rpm = rpmSetpoint
                }, {
                    path = auto["02- Shooting Position"]
                    Drive.driveAlongPath(path, false)
                })
//                parallel ({
//                    shootingMode(7)
//                }, {
//                    delay(2.0)
                    shootingMode(5)
/*                    Intake.setPower(1.0)
                    Intake.extend = true
                    path = auto["03- Intake 3 Cells"]
                    Drive.driveAlongPath(path, false)
//                })
                    parallel ({
                        path = auto["04- Intake 2 Cells"]
                        Drive.driveAlongPath(path, false)
                    }, {
                        delay(path.duration * 0.9)
                        Intake.extend = true
                    })
                    Intake.setPower(1.0)
                    Intake.extend = false
                    path = auto["05- Shooting Position"]
                    Drive.driveAlongPath(path, false)
                    shootingMode(5)*/
                }
        } finally {
            Shooter.stop()
            Shooter.rpmSetpoint = 0.0
            Feeder.setPower(0.0)
            Intake.extend = false
            Intake.setPower(0.0)
            FrontLimelight.ledEnabled = false
        }
    }

    suspend fun shieldGenerator8() = use(Drive, Shooter, Intake, Feeder, FrontLimelight) {
        try {
            FrontLimelight.ledEnabled = true
            var additionalTime = 0.5
            var auto = autonomi["10 Ball Shield Generator"]
//            var auto = autonomi["Red 10 Ball Shield Generator"]
//            if (!redSide) auto = autonomi["Blue 10 Ball Shield Generator"]
            println(auto == null)
            if (true){//auto != null) {
                Intake.setPower(1.0) //Intake.INTAKE_POWER)
                Intake.extend = true
                var path = auto["01- Intake 2 Cells"]
                path.duration += additionalTime
                Drive.driveAlongPath(path, true, 0.125)
                delay(0.25)
                Intake.setPower(0.5)
//                Intake.extend = false
                parallel ({
                    delay(path.duration * 0.25)
                    val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asInches)
                    Shooter.rpm = rpmSetpoint
                }, {
                    path = auto["02- Shooting Position"]
                    path.duration += additionalTime
                    Drive.driveAlongPath(path, false)
                })
//                parallel ({
//                    shootingMode(7)
//                }, {
//                    delay(2.0)
                shootingMode(5)
                Intake.setPower(1.0)
                Intake.extend = true
                path = auto["03- Intake 3 Cells"]
                path.duration += additionalTime
                Drive.driveAlongPath(path, false)
//                })
                Intake.extend = false
                path = auto["06- 8 Ball Mod"]
                path.duration += additionalTime
                Drive.driveAlongPath(path, false)
                shootingMode(3)
            }
        } finally {
            Shooter.stop()
            Shooter.rpmSetpoint = 0.0
            Feeder.setPower(0.0)
            Intake.extend = false
            Intake.setPower(0.0)
            FrontLimelight.ledEnabled = false
        }
    }

    suspend fun trenchRun8() = use(Drive, Intake, Shooter, Feeder, FrontLimelight){
        try {
            FrontLimelight.ledEnabled = true
            println("Got into Trench Run 8")
            val auto = autonomi["8 Ball Trench Run"]
            if (true)/*auto != null)*/ {
                var path = auto["1- Collect 1 and Shoot 4 Cells"]
                Intake.setPower(Intake.INTAKE_POWER)
                Intake.extend = true
                parallel ({
                    Drive.driveAlongPath(path, true)
                }, {
                    delay(path.duration * 0.5)
                    val rpmSetpoint = Shooter.rpmCurve.getValue(FrontLimelight.distance.asFeet)
                    Shooter.rpm = rpmSetpoint
                }/*, {
                    delay(1.1 * path.duration)
                    Intake.extend = false
                */)
                shootingMode(4)
                Intake.extend = true
                path = auto["2- Collect 4 Cells"]
                Drive.driveAlongPath(path, false)
                path = auto["3- Shoot 4 Cells"]
                Drive.driveAlongPath(path, false)
                shootingMode(4)
            }
        } finally {
            Shooter.stop()
            Shooter.rpmSetpoint = 0.0
            Feeder.setPower(0.0)
            Intake.extend = false
            Intake.setPower(0.0)
            FrontLimelight.ledEnabled = false
        }
    }

    suspend fun carpetBiasTest() = use(Drive) {
        val auto = autonomi["Carpet Bias Test"]
        var path = auto["01- Forward"]
        Drive.driveAlongPath(path, false)
        path = auto["02- Backward"]
        Drive.driveAlongPath(path, false)
        path = auto["03- Left"]
        Drive.driveAlongPath(path, false)
        path = auto["04- Forward"]
        Drive.driveAlongPath(path, false)
        path = auto["05- Backward"]
    }

    suspend fun test8FtStraight() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            var path = auto["8 Foot Straight"]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun test8FtCircle() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            var path = auto["8 Foot Circle"]
            Drive.driveAlongPath(path, true)
        }
    }

    suspend fun test90DegreeTurn() = use(Drive) {
        val auto = autonomi["Tests"]
        if (auto != null) {
            Drive.driveAlongPath( auto["90 Degree Turn"], true, 2.0)
        }
    }

    suspend fun feederToYeeter() = use(Drive) {
        val auto = autonomi["Helper Paths"]
        if (auto != null) {
             var path = auto["Feeder to Yeeter"]

//            var path = Path2D()
//            if (Drive.position.y < -29) {
//                path.addPoint(Drive.position.x, Drive.position.y)
//               path.addPointAndTangent(5.74, -22.04, 0.0, -1.0)
//            } else {
//            path.addPoint(Drive.position.x, Drive.position.y)
//            path.addPointAndTangent(5.53, -29.86, 0.0, -1.0)
//            path.addPointAndTangent(5.74, -22.04, 0.0, -1.0)
//            }
            Drive.driveAlongPath(path, false, 0.0, false) {
                OI.driveTranslation.length > 0.0
            }
        }
    }

    suspend fun yeeterToFeeder() = use(Drive) {
        val auto = autonomi["Helper Paths"]
        if (auto != null) {
            // var path = auto["Yeeter to Feeder"]
            var path = Path2D()
            if (Drive.position.y > -29) {
                path.addPoint(Drive.position.x, Drive.position.y)
                path.addPointAngleAndMagnitude(4.50, -30.96, 9.5, 5.00)
                path.addPointAngleAndMagnitude(-02.81, -45.22, -13.6, 1.17)

                path.addEasePoint(0.0,0.0)
                path.addEasePoint(7.0,1.0)
            } else {
                path.addPoint(Drive.position.x, Drive.position.y)
                path.addPointAndTangent(-02.81, -45.22, 0.0, -1.0)

                path.addEasePoint(0.0,0.0)
                path.addEasePoint(3.5,1.0)
            }

            Drive.driveAlongPath(path, false, 0.0, false) {
                OI.driveTranslation.length > 0.0
            }
        }
    }

    suspend fun slalom() =use(Drive) {
        val auto = autonomi ["Slalom Auto"]
        if (auto != null) {
            var path = auto ["First Way"]
            path = path.apply {
                //addEasePoint(0.0, 0.0)
                //addEasePointSlopeAndMagnitude(path.duration / 2, 0.5, -0.5, 2.5)
                //addEasePoint(path.duration, 1.0)
            }
            Drive.driveAlongPath(path, true, 0.0, true) {
                OI.driveTranslation.length > 0.0
            }
        }
    }

    suspend fun barrelRacingAuto() = use(Drive) {
        val auto = autonomi["Barrel Racing Auto"]
        if (auto != null) {
            var path = auto["Barrel Racing Path"]
            Drive.driveAlongPath(path, true)
        }
    }
}